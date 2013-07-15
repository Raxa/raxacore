package org.bahmni.csv;

public class MigratorBuilder<T extends CSVEntity> {

    public static final String VALIDATION_ERROR_FILE_EXTENSION = ".val.err";
    public static final String MIGRATION_ERROR_FILE_EXTENSION = ".err";

    private String inputCSVFileLocation;
    private String inputCSVFileName;
    private EntityPersister<T> entityPersister;
    private final Class<T> entityClass;
    private int numberOfValidationThreads = 1;
    private int numberOfMigrationThreads = 1;

    public MigratorBuilder(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public MigratorBuilder<T> readFrom(String inputCSVFileLocation, String inputCSVFileName) {
        this.inputCSVFileLocation = inputCSVFileLocation;
        this.inputCSVFileName = inputCSVFileName;
        return this;
    }

    public MigratorBuilder<T> persistWith(EntityPersister<T> entityPersister) {
        this.entityPersister = entityPersister;
        return this;
    }

    public MigratorBuilder<T> withMultipleMigrators(int numberOfMigrationThreads) {
        if (numberOfMigrationThreads < 0)
            throw new RuntimeException("Invalid number of threads. numberOfMigrationThreads:" + numberOfMigrationThreads);
        this.numberOfMigrationThreads = numberOfMigrationThreads;
        return this;
    }

    public MigratorBuilder<T> withMultipleValidators(int numberOfValidationThreads) {
        if (numberOfValidationThreads < 0)
            throw new RuntimeException("Invalid number of threads. numberOfValidationThreads:" + numberOfValidationThreads);
        this.numberOfValidationThreads = numberOfValidationThreads;
        return this;
    }

    public Migrator<T> build() {
        CSVFile inputCsvFile = new CSVFile(inputCSVFileLocation, inputCSVFileName, entityClass);
        CSVFile validationErrorFile = new CSVFile(inputCSVFileLocation, errorFileName(inputCSVFileName, VALIDATION_ERROR_FILE_EXTENSION), entityClass);
        CSVFile migrationErrorFile = new CSVFile(inputCSVFileLocation, errorFileName(inputCSVFileName, MIGRATION_ERROR_FILE_EXTENSION), entityClass);
        return new Migrator<T>(inputCsvFile, validationErrorFile, migrationErrorFile, entityPersister, numberOfValidationThreads, numberOfMigrationThreads);
    }

    private String errorFileName(String fileName, String fileNameAddition) {
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        return fileNameWithoutExtension + fileNameAddition + fileExtension;
    }

}
