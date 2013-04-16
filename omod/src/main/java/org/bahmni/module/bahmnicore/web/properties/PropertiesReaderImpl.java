package org.bahmni.module.bahmnicore.web.properties;

import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReaderImpl implements PropertiesReader{
    private Properties properties;

    private PropertiesReaderImpl(Properties properties) {
        this.properties = properties;
    }

    public static PropertiesReaderImpl load() {
        String propertyFile = new File(OpenmrsUtil.getApplicationDataDirectory(), "bahmnicore.properties").getAbsolutePath();
        Properties properties;
        try {
            properties = new Properties(System.getProperties());
            properties.load(new FileInputStream(propertyFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new PropertiesReaderImpl(properties);
    }

    @Override
    public String getProperty(String key){
        return properties.getProperty(key);
    }
}
