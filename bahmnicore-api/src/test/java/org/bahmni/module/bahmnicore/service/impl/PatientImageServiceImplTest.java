package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.properties.BahmniCoreProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BahmniCoreProperties.class)
public class PatientImageServiceImplTest {

    private PatientImageServiceImpl patientImageService;

    @Test
    public void shouldCreateRightDirectoryAccordingToPatientId() {
        PowerMockito.mockStatic(BahmniCoreProperties.class);
        when(BahmniCoreProperties.getProperty("bahmnicore.documents.baseDirectory")).thenReturn("");
        patientImageService = new PatientImageServiceImpl();

        String url = patientImageService.createFilePath(".", 280, "Radiology", "jpeg");

        assertFalse(url.isEmpty());
        assertTrue(url.startsWith("300/280-Radiology-"));
        assertTrue(url.endsWith(".jpeg"));

        File absoluteFileDirectory = new File("./300");
        absoluteFileDirectory.delete();
    }
}
