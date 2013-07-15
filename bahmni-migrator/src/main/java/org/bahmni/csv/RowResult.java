package org.bahmni.csv;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public class RowResult<T extends CSVEntity> {
    public static final RowResult SUCCESS = new RowResult<>();

    private T csvEntity;
    private String errorMessage;

    public RowResult(T csvEntity, String errorMessage) {
        this.csvEntity = csvEntity;
        this.errorMessage = errorMessage;
    }

    private RowResult() {
    }

    public boolean isSuccessful() {
        return errorMessage == null || errorMessage.trim().isEmpty();
    }

    public String[] getRowWithErrorColumn() {
        if (csvEntity == null)
            return new String[] {};

        return csvEntity.getRowWithErrorColumn(errorMessage);
    }

    public String getRowWithErrorColumnAsString() {
        return StringUtils.join(Arrays.asList(getRowWithErrorColumn()), ",");
    }
}
