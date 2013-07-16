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
        this(csvEntity, null);
    }

    public RowResult(T csvEntity, Throwable exception) {
        this.csvEntity = csvEntity;
        this.errorMessage = getStackTrace(exception);
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

    private static String getStackTrace(Throwable exception) {
        if (exception == null)
            return null;
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        exception.printStackTrace(printWriter);
        return result.toString();
    }
}
