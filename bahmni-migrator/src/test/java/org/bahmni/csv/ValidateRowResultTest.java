package org.bahmni.csv;

import junit.framework.Assert;
import org.junit.Test;

public class ValidateRowResultTest {
    @Test
    public void isSuccessful_returns_true_when_no_errormessage() {
        ValidateRowResult successfulRow = new ValidateRowResult(new DummyCSVEntity("1", "name"));
        Assert.assertTrue("isSuccessful() should be true, as there is no Error Message", successfulRow.isSuccessful());
    }

    @Test
    public void isSuccessful_returns_true_for_empty_errormessage() {
        ValidateRowResult successfulRow = new ValidateRowResult(new DummyCSVEntity("1", "name"), "");
        Assert.assertTrue("isSuccessful() should be true, as there is no Error Message", successfulRow.isSuccessful());
    }

    @Test
    public void isSuccessful_returns_false_when_no_errormessage() {
        ValidateRowResult validationFailedRow = new ValidateRowResult(new DummyCSVEntity("1", "name"), "validation error message");
        Assert.assertFalse("isSuccessful() should be false, as there is an Error Message", validationFailedRow.isSuccessful());
    }

    @Test
    public void getErrorMessage_returns_message() {
        ValidateRowResult validationFailedRow = new ValidateRowResult(new DummyCSVEntity("1", "name"), "validation error message");
        Assert.assertEquals("validation error message", validationFailedRow.getErrorMessage());
    }

    @Test
    public void getErrorMessage_returns_null_when_no_error() {
        ValidateRowResult successfulRow = new ValidateRowResult(new DummyCSVEntity("1", "name"));
        Assert.assertNull("validation error message", successfulRow.getErrorMessage());
    }

}
