package org.bahmni.csv;

public class MigrateRowResult<T extends CSVEntity> {
    public static final MigrateRowResult SUCCESS = new MigrateRowResult();

    private T csvEntity;
    private String errorMessage;

    public MigrateRowResult(T csvEntity, String errorMessage) {
        this.csvEntity = csvEntity;
        this.errorMessage = errorMessage;
    }

    private MigrateRowResult() {
    }

    public boolean isSuccessful() {
        return errorMessage == null || errorMessage.trim().isEmpty();
    }

    public String[] getRowWithErrorColumn() {
        if (csvEntity == null)
            return new String[] {};

        return csvEntity.getRowWithErrorColumn(errorMessage);
    }
}
