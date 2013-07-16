package org.bahmni.csv;

class AllPassEnitityPersister implements EntityPersister<DummyCSVEntity> {
    @Override
    public RowResult<DummyCSVEntity> validate(DummyCSVEntity csvEntity) {
        return RowResult.SUCCESS;
    }

    @Override
    public RowResult<DummyCSVEntity> persist(DummyCSVEntity csvEntity) {
        return RowResult.SUCCESS;
    }
}
class ValidationFailedEnitityPersister implements EntityPersister<DummyCSVEntity> {
    @Override
    public RowResult<DummyCSVEntity> validate(DummyCSVEntity csvEntity) {
        return new RowResult<>(csvEntity, new Exception("validation failed"));
    }

    @Override
    public RowResult<DummyCSVEntity> persist(DummyCSVEntity csvEntity) {
        return RowResult.SUCCESS;
    }
}
class MigrationFailedEnitityPersister implements EntityPersister<DummyCSVEntity> {
    @Override
    public RowResult<DummyCSVEntity> validate(DummyCSVEntity csvEntity) {
        return RowResult.SUCCESS;
    }

    @Override
    public RowResult<DummyCSVEntity> persist(DummyCSVEntity csvEntity) {
        return new RowResult(csvEntity, new Exception("migration failed"));
    }
}
