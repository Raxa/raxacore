package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.io.FileUtils;
import org.bahmni.module.bahmnicore.bahmniexceptions.VideoFormatNotSupportedException;
import org.bahmni.module.bahmnicore.model.VideoFormats;
import org.bahmni.module.bahmnicore.properties.BahmniCoreProperties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.openmrs.Patient;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BahmniCoreProperties.class, FileInputStream.class, FileUtils.class})
public class PatientDocumentServiceImplTest {

    private PatientDocumentServiceImpl patientDocumentService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();


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

        assertEquals(404, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldSaveVideo() throws Exception {
        PowerMockito.mockStatic(BahmniCoreProperties.class);
        when(BahmniCoreProperties.getProperty("bahmnicore.documents.baseDirectory")).thenReturn("");
        PowerMockito.mockStatic(FileUtils.class);

        Patient patient = new Patient();
        patient.setId(1);
        patient.setUuid("patient-uuid");

        patientDocumentService = new PatientDocumentServiceImpl();
        String url = patientDocumentService.saveDocument(1, "Consultation", "videoContent", "mp4", "video");

        assertTrue(url.matches(".*1-Consultation-.*.mp4"));
    }

    @Test
    public void shouldThrowExceptionWhenVideoFormatIsNotSupported() throws Exception {
        PowerMockito.mockStatic(BahmniCoreProperties.class);
        when(BahmniCoreProperties.getProperty("bahmnicore.documents.baseDirectory")).thenReturn("");
        PowerMockito.mockStatic(FileUtils.class);

        Patient patient = new Patient();
        patient.setId(1);
        patient.setUuid("patient-uuid");

        expectedException.expect(VideoFormatNotSupportedException.class);
        expectedException.expectMessage(String.format("The video format '%s' is not supported. Supported formats are %s",
                "xyz", Arrays.toString(VideoFormats.values())));

        patientDocumentService = new PatientDocumentServiceImpl();
        patientDocumentService.saveDocument(1, "Consultation", "videoContent", "xyz", "video");
    }
}
