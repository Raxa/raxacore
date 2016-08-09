package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.properties.BahmniCoreProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BahmniCoreProperties.class, FileInputStream.class})
public class PatientDocumentServiceImplTest {

    private PatientDocumentServiceImpl patientDocumentService;

    @Test
    public void shouldCreateRightDirectoryAccordingToPatientId() {
        PowerMockito.mockStatic(BahmniCoreProperties.class);
        when(BahmniCoreProperties.getProperty("bahmnicore.documents.baseDirectory")).thenReturn("");
        patientDocumentService = new PatientDocumentServiceImpl();

        String url = patientDocumentService.createFilePath(".", 280, "Radiology", "jpeg");

        assertFalse(url.isEmpty());
        assertTrue(url.startsWith("300/280-Radiology-"));
        assertTrue(url.endsWith(".jpeg"));

        File absoluteFileDirectory = new File("./300");
        absoluteFileDirectory.delete();
    }

    @Test
    public void shouldGetImageNotFoundForIfNoImageCapturedForPatientAndNoDefaultImageNotPresent() throws Exception {
        final FileInputStream fileInputStreamMock = PowerMockito.mock(FileInputStream.class);
        PowerMockito.whenNew(FileInputStream.class).withArguments(Matchers.anyString()).thenReturn(fileInputStreamMock);
        PowerMockito.mockStatic(BahmniCoreProperties.class);
        when(BahmniCoreProperties.getProperty("bahmnicore.images.directory")).thenReturn("");
        when(BahmniCoreProperties.getProperty("bahmnicore.images.directory.defaultImage")).thenReturn("");
        patientDocumentService = new PatientDocumentServiceImpl();

        ResponseEntity<Object> responseEntity = patientDocumentService.retriveImage("patientUuid");

        Assert.assertEquals(404, responseEntity.getStatusCode().value());
    }
}
