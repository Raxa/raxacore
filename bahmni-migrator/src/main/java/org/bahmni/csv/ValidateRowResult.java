package org.bahmni.csv;

public class ValidateRowResult<T extends CSVEntity> {
    private final CSVEntity csvEntity;
    private final String errorMessage;

    public ValidateRowResult(CSVEntity csvEntity, String errorMessage) {
        this.csvEntity = csvEntity;
        this.errorMessage = errorMessage;
    }

    public ValidateRowResult(CSVEntity csvEntity) {
        this(csvEntity, null);
    }

    public boolean isSuccessful() {
        return errorMessage == null || errorMessage.trim().isEmpty();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String[] getRowWithErrorColumn() {
        if (csvEntity == null)
            return new String[] {};

        return csvEntity.getRowWithErrorColumn(getErrorMessage());
    }
}
