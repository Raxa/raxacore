package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.form.data.FormDetails;
import org.bahmni.module.bahmnicore.service.BahmniFormDetailsService;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BahmniFormDetailsControllerTest {

    private BahmniFormDetailsController bahmniFormDetailsController;

    private BahmniFormDetailsService bahmniFormDetailsService = mock(BahmniFormDetailsService.class);

    @Before
    public void setUp() {
        bahmniFormDetailsController = new BahmniFormDetailsController(bahmniFormDetailsService);
    }

    @Test
    public void shouldReturnCollectionOfFormDetails() {
        FormDetails formDetails = mock(FormDetails.class);
        String patientUuid = "provider-uuid";
        String formType = "v2";
        when(bahmniFormDetailsService.getFormDetails(patientUuid, formType))
                .thenReturn(Collections.singletonList(formDetails));

        Collection<FormDetails> actualFormDetails = bahmniFormDetailsController.getFormDetails(patientUuid, formType);
        assertEquals(1, actualFormDetails.size());
        assertEquals(formDetails, actualFormDetails.iterator().next());
    }
}