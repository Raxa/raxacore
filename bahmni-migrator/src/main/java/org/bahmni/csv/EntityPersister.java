package org.bahmni.csv;

public interface EntityPersister<T extends CSVEntity> {
    MigrateRowResult<T> persist(T csvEntity);

    ValidateRowResult<T> validate(T csvEntity);
}
