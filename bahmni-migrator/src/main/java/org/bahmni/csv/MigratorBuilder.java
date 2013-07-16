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
        CSVFile validationErrorFile = new CSVFile(inputCSVFileLocation, errorFileName(inputCSVFileName, VALIDATION_ERROR_FILE_EXTENSION), entityClass);
        CSVFile migrationErrorFile = new CSVFile(inputCSVFileLocation, errorFileName(inputCSVFileName, MIGRATION_ERROR_FILE_EXTENSION), entityClass);
        CSVFile inputCSVFile = new CSVFile(inputCSVFileLocation, inputCSVFileName, entityClass);

        Stage validationStage = new StageBuilder().validation().withInputFile(inputCSVFile).withErrorFile(validationErrorFile).withNumberOfThreads(numberOfValidationThreads).build();
        Stage migrationStage = new StageBuilder().migration().withInputFile(inputCSVFile).withErrorFile(migrationErrorFile).withNumberOfThreads(numberOfMigrationThreads).build();

        Stages allStages = new Stages();
        allStages.addStage(validationStage);
        allStages.addStage(migrationStage);

        return new Migrator<>(entityPersister, allStages);
    }

    private String errorFileName(String fileName, String fileNameAddition) {
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        return fileNameWithoutExtension + fileNameAddition + fileExtension;
    }

}
