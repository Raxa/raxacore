package org.bahmni.csv;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.csv.exception.MigrationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

// Assumption - if you use multithreading, there should be no dependency between data
public class Migrator<T extends CSVEntity> {

    private CSVFile<T> csvFile;
    private final EntityPersister entityPersister;
    private final String logFileName;

    private static Logger logger = Logger.getLogger(Migrator.class);

    public Migrator(CSVFile<T> csvFile, EntityPersister entityPersister, String logFileName) {
        this.csvFile = csvFile;
        this.entityPersister = entityPersister;
        this.logFileName = logFileName;
        this.csvFile = csvFile;
    }

    public MigrateResult<T> migrate() {
        logger.info("Starting migration using file-" + csvFile.getAbsoluteFile());
        try {
            MigrateResult<T> validationResult = validationStage();
            if (!validationResult.isValidationSuccessful()) {
                return validationResult;
            }
            return migrationStage();
        } catch (Exception e) {
            logger.error(getStackTrace(e));
            throw new MigrationException(getStackTrace(e), e);
        }
    }

    private MigrateResult<T> validationStage() throws IOException, InstantiationException, IllegalAccessException {
        logger.info("Starting Validation Stage");
        MigrateResult<T> finalValidateResult = new MigrateResult<>(csvFile.getFileName());

        try {
            csvFile.open();
            finalValidateResult.addHeaderRow(csvFile.getHeaderRow());
            CSVEntity csvEntity;
            while ((csvEntity = csvFile.readEntity()) != null) {

                // TODO : Mujir - spawn multiple threads here to persist a batch of records
                ValidateRowResult<T> rowResult = entityPersister.validate(csvEntity);
                if (!rowResult.isSuccessful()) {
                    logger.error("Failed migrating record. Row Details - " +
                            StringUtils.join(Arrays.asList(rowResult.getRowWithErrorColumn()), ","));
                }

                finalValidateResult.addValidatedRecord(csvEntity, rowResult);
            }

        } finally {
            logger.warn("Validated total of " + finalValidateResult.numberOfValidatedRecords() + " records.");
            csvFile.close();
        }
        return finalValidateResult;
    }

    private MigrateResult<T> migrationStage() throws IOException, InstantiationException, IllegalAccessException {
        logger.info("Starting Migration Stage");
        MigrateResult<T> finalMigrateResult = new MigrateResult<>(csvFile.getFileName());

        int countOfSuccessfulMigration = 0;
        try {
            csvFile.open();
            finalMigrateResult.addHeaderRow(csvFile.getHeaderRow());
            CSVEntity csvEntity;
            while ((csvEntity = csvFile.readEntity()) != null) {
                MigrateRowResult<T> rowResult = entityPersister.persist(csvEntity);
                if (!rowResult.isSuccessful()) {
                    logger.error("Failed migrating record. Row Details - " +
                            StringUtils.join(Arrays.asList(rowResult.getRowWithErrorColumn()), ","));
                    finalMigrateResult.addMigrationError(rowResult);
                } else {
                    countOfSuccessfulMigration++;
                }
            }

        } finally {
            logger.warn("Failed migration for " + finalMigrateResult.numberOfFailedRecords() + " records. " + countOfSuccessfulMigration + " records were successfully migrated.");
            csvFile.close();
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
