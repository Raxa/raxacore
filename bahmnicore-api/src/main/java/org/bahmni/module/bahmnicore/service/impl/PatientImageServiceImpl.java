package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.BahmniCoreApiProperties;
import org.bahmni.module.bahmnicore.BahmniCoreException;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Lazy
public class PatientImageServiceImpl implements PatientImageService {
    private Log log = LogFactory.getLog(PatientImageServiceImpl.class);
    private static final String patientImagesFormat = "jpeg";
    private BahmniCoreApiProperties properties;
    private final Integer NO_OF_PATIENT_FILE_IN_A_DIRECTORY = 100;

    @Autowired
    public PatientImageServiceImpl(BahmniCoreApiProperties properties) {
        this.properties = properties;
    }

    @Override
    public void saveImage(String patientIdentifier, String image) {
        try {
            if (image == null || image.isEmpty()) return;

            File outputFile = new File(String.format("%s/%s.%s", properties.getImageDirectory(), patientIdentifier, patientImagesFormat));
            saveImageInFile(image, outputFile);
        } catch (IOException e) {
            throw new BahmniCoreException("[%s] : Could not save patient image", e);
        }
    }

    @Override
    public String saveDocument(Integer patientId, String encounterTypeName, String images) {
        try {
            if (images == null || images.isEmpty()) return null;

            String basePath = properties.getDocumentBaseDirectory();
            String relativeFilePath = createFilePath(basePath, patientId, encounterTypeName);

            File outputFile = new File(String.format("%s%s", basePath, relativeFilePath));
            saveImageInFile(images, outputFile);

            return relativeFilePath;

        } catch (IOException e) {
            throw new BahmniCoreException("[%s] : Could not save patient Document ", e);
        }
    }

    private String createFileName(Integer patientId, String encounterTypeName) {
        String uuid = UUID.randomUUID().toString();
        return String.format("%s-%s-%s.%s", patientId, encounterTypeName, uuid, patientImagesFormat);
    }

    protected String createFilePath(String basePath, Integer patientId, String encounterTypeName) {
        String fileName = createFileName(patientId, encounterTypeName);
        String documentDirectory = findDirectoryForDocumentsByPatientId(patientId);
        String absoluteFilePath = String.format("%s/%s", basePath, documentDirectory);
        File absoluteFileDirectory = new File(absoluteFilePath);
        if (!absoluteFileDirectory.exists()) {
            absoluteFileDirectory.mkdirs();
        }
        return String.format("/%s/%s", documentDirectory,fileName);
    }

    private String findDirectoryForDocumentsByPatientId(Integer patientId) {
        Integer directory = (patientId / NO_OF_PATIENT_FILE_IN_A_DIRECTORY + 1) * NO_OF_PATIENT_FILE_IN_A_DIRECTORY;
        return directory.toString();
    }

    private void saveImageInFile(String image, File outputFile) throws IOException {
        log.info(String.format("Creating patient image at %s", outputFile));
        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(image);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));
        ImageIO.write(bufferedImage, patientImagesFormat, outputFile);
        bufferedImage.flush();
        log.info(String.format("Successfully created patient image at %s", outputFile));
    }
}
