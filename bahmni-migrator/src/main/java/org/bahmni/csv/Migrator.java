package org.bahmni.csv;

import org.apache.log4j.Logger;
import org.bahmni.csv.exception.MigrationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
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
        logger.info("Starting migration using file -" + inputCsvFile.getAbsoluteFileName());
        try {
            MigrateResult<T> validationResult = runStage(numberOfValidationThreads, Stage.VALIDATION);
            if (!validationResult.isValidationSuccessful()) {
                validationResult.saveValidationErrors(validationErrorFile);
                return validationResult;
            }

            MigrateResult<T> migrateResult = runStage(numberOfMigrationThreads, Stage.MIGRATION);
            if (!migrateResult.isMigrationSuccessful()) {
                migrateResult.saveMigrationErrors(migrationErrorFile);
            }

            return migrateResult;
        } catch(MigrationException e) {
            logger.error(getStackTrace(e));
            throw e;
        } catch (Exception e) {
            logger.error(getStackTrace(e));
            throw new MigrationException(getStackTrace(e), e);
        }
    }

    private MigrateResult<T> runStage(int numberOfThreads, Stage stage) throws IOException, InstantiationException, IllegalAccessException {
        logger.info("Starting " + stage + " Stage");
        MigrateResult<T> finalResult = new MigrateResult<>();

        int countOfSuccessfulRecords = 0;
        try {
            inputCsvFile.open();
            finalResult.addHeaderRow(inputCsvFile.getHeaderRow());

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CSVEntity csvEntity;
            List<Future<RowResult>> results = new ArrayList<>();
            while ((csvEntity = inputCsvFile.readEntity()) != null) {
                Future<RowResult> rowResult = executorService.submit(stage.getCallable(entityPersister, csvEntity));
                results.add(rowResult);
            }

            for (Future<RowResult> result : results) {
                RowResult<T> rowResult = result.get();
                if (!rowResult.isSuccessful()) {
                    logger.error("Failed " + stage + " of record. Row Details - " + rowResult.getRowWithErrorColumnAsString());
                    finalResult.addError(rowResult, stage);
                } else {
                    countOfSuccessfulRecords++;
                }
            }
            executorService.shutdown();

        } catch (InterruptedException e) {
            logger.error("Thread interrupted exception. " + getStackTrace(e));
            throw new MigrationException("Could not execute threads", e);
        } catch (ExecutionException e) {
            logger.error("Could not execute threads. " + getStackTrace(e));
            throw new MigrationException("Could not execute threads", e);
        } finally {
            logger.warn("Failed " + stage + " for " +
                    ((stage == Stage.VALIDATION) ?
                            finalResult.numberOfFailedValidationRecords() : finalResult.numberOfFailedMigrationRecords()) +
                    " records. Successful records count - " + countOfSuccessfulRecords);
            inputCsvFile.close();
        }
        return finalResult;
    }

    private static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }
}
