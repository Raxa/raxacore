package org.bahmni.csv;

import junit.framework.Assert;
import org.junit.Test;

public class RowResultTest {
    @Test
    public void isSuccessful_returns_true_when_no_errormessage() {
        RowResult successfulRow = new RowResult(new DummyCSVEntity("1", "name"), null);
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
        RowResult validationFailedRow = new RowResult(new DummyCSVEntity("1", "name"), "validation error message");
        Assert.assertFalse("isSuccessful() should be false, as there is an Error Message", validationFailedRow.isSuccessful());
    }

    @Test
    public void getErrorMessage_returns_message() {
        RowResult validationFailedRow = new RowResult(new DummyCSVEntity("1", "name"), "validation error message");
        String[] rowWithErrorColumn = validationFailedRow.getRowWithErrorColumn();
        Assert.assertEquals("validation error message", rowWithErrorColumn[rowWithErrorColumn.length - 1]);
    }

    @Test
    public void getErrorMessage_returns_null_when_no_error() {
        RowResult successfulRow = new RowResult(new DummyCSVEntity("1", "name"), null);
        String[] rowWithErrorColumn = successfulRow.getRowWithErrorColumn();
        Assert.assertNull("validation error message", rowWithErrorColumn[rowWithErrorColumn.length - 1]);
    }
}
