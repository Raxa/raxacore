package org.bahmni.module.bahmnicore.web.v1_0.controller;


import org.bahmni.module.bahmnicore.BahmniCoreApiProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.module.webservices.rest.SimpleObject;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BahmniConfigurationControllerTest {

    private BahmniConfigurationController controller;

    @Mock
    BahmniCoreApiProperties bahmniCoreApiProperties;

    @Before
    public void init() {
        initMocks(this);
        controller = new BahmniConfigurationController(bahmniCoreApiProperties);
    }

    @Test
    public void indexShouldReturnConfiguration() {
        when(bahmniCoreApiProperties.getPatientImagesUrl()).thenReturn("http://test.uri/patient_images");

        SimpleObject configuration = controller.index();

        assert(configuration.get("patientImagesUrl")).equals("http://test.uri/patient_images");
    }
}
