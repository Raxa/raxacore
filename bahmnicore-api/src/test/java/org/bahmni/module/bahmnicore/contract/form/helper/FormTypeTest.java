package org.bahmni.module.bahmnicore.contract.form.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormTypeTest {

    @Test
    public void shouldReturnAllObservationTemplateFormsTypeAsV1() {
        assertEquals("v1", FormType.ALL_OBSERVATION_TEMPLATE_FORMS.get());
    }

    @Test
    public void shouldReturnFormBuilderFormsTypeAsV1() {
        assertEquals("v2", FormType.FORM_BUILDER_FORMS.get());
    }
}