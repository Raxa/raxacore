package org.bahmni.csv;

import org.bahmni.csv.CSVEntity;

public class MigrateRowResult<T extends CSVEntity> {
    private T csvEntity;
    private String errorMessage;

    public MigrateRowResult(T csvEntity, String errorMessage) {
        this.csvEntity = csvEntity;
        this.errorMessage = errorMessage;
    }

    public MigrateRowResult() {
    }

    public boolean isSuccessful() {
        return errorMessage == null || errorMessage.trim().isEmpty();
    }

    public String[] getRowWithErrorColumn() {
        return csvEntity.addErrorColumn(errorMessage);
    }
}
