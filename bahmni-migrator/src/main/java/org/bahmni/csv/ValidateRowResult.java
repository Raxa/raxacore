package org.bahmni.csv;

public class ValidateRowResult<T extends CSVEntity> {
    public static final ValidateRowResult SUCCESS = new ValidateRowResult<>();

    private T csvEntity;
    private String errorMessage;

    public ValidateRowResult(T csvEntity, String errorMessage) {
        this.csvEntity = csvEntity;
        this.errorMessage = errorMessage;
    }

    public ValidateRowResult() {
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
