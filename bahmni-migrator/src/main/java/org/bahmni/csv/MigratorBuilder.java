package org.bahmni.csv;

import java.io.File;

public class MigratorBuilder<T extends CSVEntity> {
    private File csvFileToRead;
    private EntityPersister<T> entityPersister;
    private String logFileName;
    private Class<T> entityClass;

    public MigratorBuilder(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public MigratorBuilder<T> logAt(String logFileName) {
        this.logFileName = logFileName;
        return this;
    }

    public MigratorBuilder<T> readFrom(String csvFileLocation, String csvFileName) {
        // TODO : Mujir - don't assign if the file does not exist. Also this should be an abstracted concept like CSVFile?
        this.csvFileToRead = new File(csvFileLocation, csvFileName);
        if (!this.csvFileToRead.exists())
            throw new RuntimeException("file does not exist." + csvFileToRead.getAbsolutePath());
        return this;
    }

    public MigratorBuilder<T> persistWith(EntityPersister<T> entityPersister) {
        this.entityPersister = entityPersister;
        return this;
    }

    public Migrator<T> build() {
        return new Migrator<T>(entityClass, csvFileToRead, entityPersister, logFileName);
    }
}
