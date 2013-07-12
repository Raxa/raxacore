package org.bahmni.csv;

public interface EntityPersister<T extends CSVEntity> {
    MigrateRowResult<T> persist(T CSVEntity);

    ValidateRowResult<T> validate(T csvEntity);
}
