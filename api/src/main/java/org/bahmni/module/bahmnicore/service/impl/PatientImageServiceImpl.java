package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Service
public class PatientImageServiceImpl implements PatientImageService {
    private Log log = LogFactory.getLog(PatientImageServiceImpl.class);
    private String patientImagesPath =  "/tmp/patient_images";
    private String patientImagesFormat =  "jpeg";

    @Override
    public void save(String patientIdentifier, String image) {
        try {
            if (image == null || image.isEmpty()) return;

            byte[] decodedBytes = DatatypeConverter.parseBase64Binary(image);
            BufferedImage bfi = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            File outputfile = new File(String.format("%s/%s.%s", patientImagesPath, patientIdentifier,patientImagesFormat));
            ImageIO.write(bfi , patientImagesFormat, outputfile);
            bfi.flush();
        } catch (IOException e) {
            log.error("Could not save patient image for patient id " + patientIdentifier, e);
        }
    }
}
