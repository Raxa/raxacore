package org.bahmni.csv;

class AllPassEntityPersister implements EntityPersister<DummyCSVEntity> {
    @Override
    public RowResult<DummyCSVEntity> validate(DummyCSVEntity csvEntity) {
        return RowResult.SUCCESS;
    }

    @Override
    public RowResult<DummyCSVEntity> persist(DummyCSVEntity csvEntity) {
        return RowResult.SUCCESS;
    }
}

class ValidationFailedEntityPersister implements EntityPersister<DummyCSVEntity> {
    private String message;

    ValidationFailedEntityPersister(String message) {
        this.message = message;
    }

    @Override
    public RowResult<DummyCSVEntity> validate(DummyCSVEntity csvEntity) {
        return new RowResult<>(csvEntity, message);
    }

    @Override
    public RowResult<DummyCSVEntity> persist(DummyCSVEntity csvEntity) {
        return RowResult.SUCCESS;
    }
}

class MigrationFailedEntityPersister implements EntityPersister<DummyCSVEntity> {
    private Exception e;

    public MigrationFailedEntityPersister(Exception e) {
        this.e = e;
    }

    @Override
    public RowResult<DummyCSVEntity> validate(DummyCSVEntity csvEntity) {
        return RowResult.SUCCESS;
    }

    @Override
    public RowResult<DummyCSVEntity> persist(DummyCSVEntity csvEntity) {
        return new RowResult(csvEntity, e);
    }
}
