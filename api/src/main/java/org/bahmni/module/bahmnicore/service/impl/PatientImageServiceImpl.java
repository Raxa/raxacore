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

@Service
@Lazy
public class PatientImageServiceImpl implements PatientImageService {
    private Log log = LogFactory.getLog(PatientImageServiceImpl.class);
    private static final String patientImagesFormat =  "jpeg";
    private BahmniCoreApiProperties properties;

    @Autowired
    public PatientImageServiceImpl(BahmniCoreApiProperties properties) {
        this.properties = properties;
    }

    @Override
    public void save(String patientIdentifier, String image) {
        try {
            if (image == null || image.isEmpty()) return;

            File outputFile = new File(String.format("%s/%s.%s", properties.getImageDirectory(), patientIdentifier,patientImagesFormat));
            log.info(String.format("Creating patient image at %s", outputFile));
            byte[] decodedBytes = DatatypeConverter.parseBase64Binary(image);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            ImageIO.write(bufferedImage, patientImagesFormat, outputFile);
            bufferedImage.flush();
            log.info(String.format("Successfully created patient image at %s", outputFile));
        } catch (IOException e) {
            throw new BahmniCoreException("[%s] : Could not save patient image", e);
        }
    }
}
