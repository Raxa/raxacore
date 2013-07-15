package org.bahmni.csv;

public interface EntityPersister<T extends CSVEntity> {
    RowResult<T> persist(T csvEntity);

    RowResult<T> validate(T csvEntity);
}
