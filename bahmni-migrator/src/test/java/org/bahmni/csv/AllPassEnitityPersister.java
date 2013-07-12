package org.bahmni.csv;

class AllPassEnitityPersister implements EntityPersister<DummyCSVEntity> {
    @Override
    public ValidateRowResult<DummyCSVEntity> validate(DummyCSVEntity csvEntity) {
        return new ValidateRowResult<>(csvEntity);
    }

    @Override
    public MigrateRowResult<DummyCSVEntity> persist(DummyCSVEntity csvEntity) {
        return MigrateRowResult.SUCCESS;
    }
}
class ValidationFailedEnitityPersister implements EntityPersister<DummyCSVEntity> {
    @Override
    public ValidateRowResult<DummyCSVEntity> validate(DummyCSVEntity csvEntity) {
        return new ValidateRowResult<>(csvEntity, "validation failed");
    }

    @Override
    public MigrateRowResult<DummyCSVEntity> persist(DummyCSVEntity csvEntity) {
        return MigrateRowResult.SUCCESS;
    }
}
class MigrationFailedEnitityPersister implements EntityPersister<DummyCSVEntity> {
    @Override
    public ValidateRowResult<DummyCSVEntity> validate(DummyCSVEntity csvEntity) {
        return new ValidateRowResult<>(csvEntity);
    }

    @Override
    public MigrateRowResult<DummyCSVEntity> persist(DummyCSVEntity csvEntity) {
        return new MigrateRowResult(csvEntity, "migration failed");
    }
}
