package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.BahmniCoreApiProperties;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientImageServiceImplTest {

    private PatientImageServiceImpSubClass patientImageServiceImpSubClass;

    @Mock
    private BahmniCoreApiProperties bahmniCoreApiProperties;

    @Test
    public void shouldCreateRightDirectoryAccordingToPatientId() {
        initMocks(this);
        when(bahmniCoreApiProperties.getDocumentBaseDirectory()).thenReturn("");
        patientImageServiceImpSubClass = new PatientImageServiceImpSubClass(bahmniCoreApiProperties);

        String url = patientImageServiceImpSubClass.createFilePath(".", 280, "Radiology", "jpeg");

        assertFalse(url.isEmpty());
        assertTrue(url.startsWith("300/280-Radiology-"));
        assertTrue(url.endsWith(".jpeg"));

        File absoluteFileDirectory = new File("./300");
        absoluteFileDirectory.delete();
    }

    private class PatientImageServiceImpSubClass extends PatientImageServiceImpl {
        private PatientImageServiceImpSubClass(BahmniCoreApiProperties bahmniCoreApiProperties) {
            super(bahmniCoreApiProperties);
        }
    }
}
