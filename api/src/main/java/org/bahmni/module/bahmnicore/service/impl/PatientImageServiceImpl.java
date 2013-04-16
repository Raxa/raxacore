package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.BahmniCoreApiProperties;
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
    private String patientImagesFormat =  "jpeg";
    private BahmniCoreApiProperties properties;

    @Autowired
    public PatientImageServiceImpl(BahmniCoreApiProperties properties) {
        this.properties = properties;
    }

    @Override
    public void save(String patientIdentifier, String image) {
        try {
            if (image == null || image.isEmpty()) return;

            byte[] decodedBytes = DatatypeConverter.parseBase64Binary(image);
            BufferedImage bfi = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            File outputfile = new File(String.format("%s/%s.%s", properties.getImageDirectory(), patientIdentifier,patientImagesFormat));
            ImageIO.write(bfi , patientImagesFormat, outputfile);
            bfi.flush();
        } catch (IOException e) {
            log.error(String.format("[%s] : Could not save patient image", patientIdentifier), e);
            throw new RuntimeException(e);
        }
    }
}
