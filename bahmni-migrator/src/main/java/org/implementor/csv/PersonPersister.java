package org.implementor.csv;

import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.MigrateRowResult;
import org.bahmni.csv.ValidateRowResult;

public class PersonPersister<T extends CSVEntity> implements EntityPersister<T> {
    @Override
    public MigrateRowResult<T> persist(T csvEntity) {
//        return new MigrateRowResult();

        MigrateRowResult<T> rowError = new MigrateRowResult(csvEntity, "this row has an issue");
        return rowError;
    }

    @Override
    public ValidateRowResult<T> validate(CSVEntity csvEntity) {
//        return new ValidateRowResult<T>(csvEntity);
        return new ValidateRowResult<T>(csvEntity, "validation issue");
    }
}
