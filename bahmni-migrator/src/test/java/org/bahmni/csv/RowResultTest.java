package org.bahmni.csv;

import junit.framework.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

public class RowResultTest {
    @Test
    public void isSuccessful_returns_true_when_no_errormessage() {
        RowResult successfulRow = new RowResult(new DummyCSVEntity("1", "name"));
        Assert.assertTrue("isSuccessful() should be true, as there is no Error Message", successfulRow.isSuccessful());
        Assert.assertTrue("isSuccessful() should be true, as there is no Error Message", RowResult.SUCCESS.isSuccessful());
    }

    @Test
    public void isSuccessful_returns_true_for_empty_errormessage() {
        RowResult successfulRow = new RowResult(new DummyCSVEntity("1", "name"), "");
        Assert.assertTrue("isSuccessful() should be true, as there is no Error Message", successfulRow.isSuccessful());
    }

    @Test
    public void isSuccessful_returns_false_when_no_errormessage() {
        RowResult validationFailedRow = new RowResult(new DummyCSVEntity("1", "name"), new FileNotFoundException("file not found"));
        Assert.assertFalse("isSuccessful() should be false, as there is an Error Message", validationFailedRow.isSuccessful());
        Assert.assertTrue("Row Error should start with the row details", validationFailedRow.getRowWithErrorColumnAsString().startsWith("1,name"));
        Assert.assertTrue("Row Error should contain the exception stack trace", validationFailedRow.getRowWithErrorColumnAsString().contains("java.io.FileNotFoundException"));
    }

    @Test
    public void getRowWithErrorColumn_returns_error_message_for_exceptions() {
        RowResult validationFailedRow = new RowResult(new DummyCSVEntity("1", "name"), new FileNotFoundException("file not found"));
        String[] rowWithErrorColumn = validationFailedRow.getRowWithErrorColumn();
        Assert.assertTrue("validation error message has stacktrace",
                rowWithErrorColumn[rowWithErrorColumn.length - 1].startsWith("java.io.FileNotFoundException"));
    }

    @Test
    public void getRowWithErrorColumn_returns_error_message_for_string_messages() {
        RowResult validationFailedRow = new RowResult(new DummyCSVEntity("1", "name"), "validation error");
        String[] rowWithErrorColumn = validationFailedRow.getRowWithErrorColumn();
        Assert.assertEquals("validation error", rowWithErrorColumn[rowWithErrorColumn.length - 1]);
    }
}
