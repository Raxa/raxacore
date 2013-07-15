package org.bahmni.csv;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.csv.exception.MigrationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

// Assumption - if you use multithreading, there should be no dependency between the records in the file.
public class Migrator<T extends CSVEntity> {
    private CSVFile<T> inputCsvFile;
    private int numberOfMigrationThreads;
    private int numberOfValidationThreads;
    private final CSVFile<T> validationErrorFile;
    private final CSVFile<T> migrationErrorFile;
    private final EntityPersister entityPersister;

    private static Logger logger = Logger.getLogger(Migrator.class);

    public Migrator(CSVFile<T> inputCsvFile, CSVFile<T> validationErrorFile, CSVFile<T> migrationErrorFile,
                    EntityPersister entityPersister, int numberOfValidationThreads, int numberOfMigrationThreads) {
        this.inputCsvFile = inputCsvFile;
        this.validationErrorFile = validationErrorFile;
        this.migrationErrorFile = migrationErrorFile;
        this.entityPersister = entityPersister;
        this.numberOfValidationThreads = numberOfValidationThreads;
        this.numberOfMigrationThreads = numberOfMigrationThreads;
    }

    public MigrateResult<T> migrate() {
        logger.info("Starting migration using file-" + inputCsvFile.getAbsoluteFileName());
        try {
            MigrateResult<T> validationResult = validationStage();
            if (!validationResult.isValidationSuccessful()) {
                validationResult.saveValidationErrors(validationErrorFile);
                return validationResult;
            }

            MigrateResult<T> migrateResult = migrationStage();
            if (!migrateResult.isMigrationSuccessful()) {
                migrateResult.saveMigrationErrors(migrationErrorFile);
            }

            return migrateResult;
        } catch (Exception e) {
            logger.error(getStackTrace(e));
            throw new MigrationException(getStackTrace(e), e);
        }
    }

    private MigrateResult<T> validationStage() throws IOException, InstantiationException, IllegalAccessException {
        logger.info("Starting Validation Stage");
        MigrateResult<T> finalValidateResult = new MigrateResult<>();

        int countOfSuccessfulValidation = 0;
        try {
            inputCsvFile.open();
            finalValidateResult.addHeaderRow(inputCsvFile.getHeaderRow());

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfValidationThreads);
            CSVEntity csvEntity;
            List<Future<ValidateRowResult<T>>> results = new ArrayList<>();
            while ((csvEntity = inputCsvFile.readEntity()) != null) {
                Future<ValidateRowResult<T>> rowResult = executorService.submit(new ValidationCallable(entityPersister, csvEntity));
                results.add(rowResult);
            }

            for (Future<ValidateRowResult<T>> result : results) {
                ValidateRowResult<T> validateRowResult = result.get();
                if (!validateRowResult.isSuccessful()) {
                    logger.error("Failed validating record. Row Details - " +
                            StringUtils.join(Arrays.asList(validateRowResult.getRowWithErrorColumn()), ","));
                    finalValidateResult.addValidationError(validateRowResult);
                } else {
                    countOfSuccessfulValidation++;
                }

            }
            executorService.shutdown();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            logger.error("Could not execute threads. " + getStackTrace(e));
            throw new MigrationException("Could not execute threads", e);
        } finally {
            logger.warn("Failed validation for " + finalValidateResult.numberOfFailedValidationRecords() + " records. " + countOfSuccessfulValidation + " records were successfully validated.");
            inputCsvFile.close();
        }
        return finalValidateResult;
    }

    private MigrateResult<T> migrationStage() throws IOException, InstantiationException, IllegalAccessException {
        logger.info("Starting Migration Stage");
        MigrateResult<T> finalMigrateResult = new MigrateResult<>();

        int countOfSuccessfulMigration = 0;
        try {
            inputCsvFile.open();
            finalMigrateResult.addHeaderRow(inputCsvFile.getHeaderRow());

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfMigrationThreads);
            CSVEntity csvEntity;
            List<Future<MigrateRowResult<T>>> results = new ArrayList<>();
            while ((csvEntity = inputCsvFile.readEntity()) != null) {
                Future<MigrateRowResult<T>> rowResult = executorService.submit(new MigrationCallable(entityPersister, csvEntity));
                results.add(rowResult);
            }

            for (Future<MigrateRowResult<T>> result : results) {
                MigrateRowResult<T> migrateRowResult = result.get();
                if (!migrateRowResult.isSuccessful()) {
                    logger.error("Failed migrating record. Row Details - " +
                            StringUtils.join(Arrays.asList(migrateRowResult.getRowWithErrorColumn()), ","));
                    finalMigrateResult.addMigrationError(migrateRowResult);
                } else {
                    countOfSuccessfulMigration++;
                }
            }
            executorService.shutdown();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            logger.error("Could not execute threads. " + getStackTrace(e));
            throw new MigrationException("Could not execute threads", e);
        } finally {
            logger.warn("Failed migration for " + finalMigrateResult.numberOfFailedMigrationRecords() + " records. " + countOfSuccessfulMigration + " records were successfully migrated.");
            inputCsvFile.close();
        }
        return finalMigrateResult;
    }

    private static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

}

class ValidationCallable<T extends CSVEntity> implements Callable<ValidateRowResult<T>> {
    private final EntityPersister entityPersister;
    private final CSVEntity csvEntity;

    private static Logger logger = Logger.getLogger(ValidationCallable.class);

    public ValidationCallable(EntityPersister entityPersister, CSVEntity csvEntity) {
        this.entityPersister = entityPersister;
        this.csvEntity = csvEntity;
    }

    @Override
    public ValidateRowResult<T> call() throws Exception {
        try {
            return entityPersister.validate(csvEntity);
        } catch (Exception e) {
            logger.error("failed while validating. Record - " + StringUtils.join(csvEntity.getOriginalRow().toArray()));
            throw new MigrationException(e);
        }
    }
}


class MigrationCallable<T extends CSVEntity> implements Callable<MigrateRowResult<T>> {
    private final EntityPersister entityPersister;
    private final CSVEntity csvEntity;

    private static Logger logger = Logger.getLogger(MigrationCallable.class);

    public MigrationCallable(EntityPersister entityPersister, CSVEntity csvEntity) {
        this.entityPersister = entityPersister;
        this.csvEntity = csvEntity;
    }

    @Override
    public MigrateRowResult<T> call() throws Exception {
        try {
            return entityPersister.persist(csvEntity);
        } catch (Exception e) {
            logger.error("failed while persisting. Record - " + StringUtils.join(csvEntity.getOriginalRow().toArray()));
            throw new MigrationException(e);
        }
    }
}
