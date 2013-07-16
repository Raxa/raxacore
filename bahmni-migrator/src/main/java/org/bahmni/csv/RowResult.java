package org.bahmni.csv;

import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

public class RowResult<T extends CSVEntity> {
    public static final RowResult SUCCESS = new RowResult<>();

    private T csvEntity;
    private String errorMessage;

    public RowResult(T csvEntity) {
        this(csvEntity, (String) null);
    }

    public RowResult(T csvEntity, Throwable exception) {
        this(csvEntity, getStackTrace(exception));
    }

    public RowResult(T csvEntity, String errorMessage) {
        this.csvEntity = csvEntity;
        this.errorMessage = errorMessage;
    }

    private RowResult() {}

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

    private static String getStackTrace(Throwable exception) {
        if (exception == null)
            return null;
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        exception.printStackTrace(printWriter);
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RowResult rowResult = (RowResult) o;

        if (csvEntity != null ? !csvEntity.equals(rowResult.csvEntity) : rowResult.csvEntity != null) return false;
        if (errorMessage != null ? !errorMessage.equals(rowResult.errorMessage) : rowResult.errorMessage != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = csvEntity != null ? csvEntity.hashCode() : 0;
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        return result;
    }
}
