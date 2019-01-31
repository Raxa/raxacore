package org.bahmni.module.bahmnicore.forms2.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormUtilTest {

    @Test
    public void shouldReturnFormNameFromGivenFormFieldPath() {
        assertEquals("FormName", FormUtil.getFormNameFromFieldPath("FormName.1/1-0"));
    }

    @Test
    public void shouldReturnEmptyStringAsFormNameIfGivenFormFieldPathDoesNotHaveFormName() {
        assertEquals("", FormUtil.getFormNameFromFieldPath(".1/1-0"));
    }

    @Test
    public void shouldReturnEmptyStringAsFormNameIfGivenFormFieldPathIsNull() {
        assertEquals("", FormUtil.getFormNameFromFieldPath(null));
    }

    @Test
    public void shouldReturnEmptyStringAsFormNameIfGivenFormFieldPathIsEmpty() {
        assertEquals("", FormUtil.getFormNameFromFieldPath(""));
    }

    @Test
    public void shouldReturnEmptyStringAsFormNameIfGivenFormFieldPathDoesNotHaveDot() {
        assertEquals("", FormUtil.getFormNameFromFieldPath("FormName1/1-0"));
    }

    @Test
    public void shouldReturnFormVersionFromGivenFormFieldPath() {
        assertEquals(2, FormUtil.getFormVersionFromFieldPath("FormName.2/1-0"));
    }

    @Test
    public void shouldReturnFormVersionAsZeroIfGivenFormFieldPathDoesNotHaveVersion() {
        assertEquals(0, FormUtil.getFormVersionFromFieldPath("FormName./1-0"));
    }

    @Test
    public void shouldReturnFormVersionAsZeroIfGivenFormFieldPathIsNull() {
        assertEquals(0, FormUtil.getFormVersionFromFieldPath(null));
    }

    @Test
    public void shouldReturnFormVersionAsZeroIfGivenFormFieldPathIsEmpty() {
        assertEquals(0, FormUtil.getFormVersionFromFieldPath(""));
    }

    @Test
    public void shouldReturnFormVersionAsZeroIfGivenFormFieldPathDoesNotHaveDot() {
        assertEquals(0, FormUtil.getFormVersionFromFieldPath("FormName2/1-0"));
    }

    @Test
    public void shouldReturnFormVersionAsZeroIfGivenFormFieldPathDoesNotHaveSlash() {
        assertEquals(0, FormUtil.getFormVersionFromFieldPath("FormName.21-0"));
    }
}