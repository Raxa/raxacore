package org.bahmni.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

// Assumption - if you use multithreading, there should be no dependency between data
public class Migrator<T extends CSVEntity> {

    private Class<T> entityClass;
    private final File csvFileToRead; // TODO : Mujir - this should perhaps be a CSVFile???
    private final EntityPersister entityPersister;
    private final String logFileName;

    public Migrator(Class<T> entityClass, File csvFileToRead, EntityPersister entityPersister, String logFileName) {
        this.entityClass = entityClass;
        this.csvFileToRead = csvFileToRead;
        this.entityPersister = entityPersister;
        this.logFileName = logFileName;
    }

    public MigrateResult<T> migrate() {
        try {
            MigrateResult<T> validationResult = validationStage();
            if (!validationResult.isValidationSuccessful()) {
                return validationResult;
            }
            return migrationStage();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Can't instantiate entity. " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MigrateResult<T> validationStage() throws IOException, InstantiationException, IllegalAccessException {
        MigrateResult<T> finalValidateResult = new MigrateResult<T>();
        CSVFile csvFile = null;
        try {
            csvFile = new CSVFile(csvFileToRead, entityClass);
            csvFile.open();
            finalValidateResult.addHeaderRow(csvFile.getHeaderRow());
            CSVEntity csvEntity;
            while ((csvEntity = csvFile.readEntity()) != null) {

                // TODO : Mujir - spawn multiple threads here to persist a batch of records
                ValidateRowResult<T> rowResult = entityPersister.validate(csvEntity);
                finalValidateResult.addValidationError(csvEntity, rowResult);
            }

        } finally {
            if (csvFile != null) csvFile.close();
        }
        return finalValidateResult;
    }

    private MigrateResult<T> migrationStage() throws IOException, InstantiationException, IllegalAccessException {
        MigrateResult<T> finalMigrateResult = new MigrateResult<T>();

        CSVFile csvFile = null;
        try {
            csvFile = new CSVFile(csvFileToRead, entityClass);
            csvFile.open();
            finalMigrateResult.addHeaderRow(csvFile.getHeaderRow());
            CSVEntity csvEntity;
            while ((csvEntity = csvFile.readEntity()) != null) {
                MigrateRowResult<T> rowResult = entityPersister.persist(csvEntity);
                if (!rowResult.isSuccessful()) {
                    finalMigrateResult.addMigrationError(rowResult);
                }
            }

        } finally {
            if (csvFile != null) csvFile.close();
        }
        return finalMigrateResult;
    }

}
