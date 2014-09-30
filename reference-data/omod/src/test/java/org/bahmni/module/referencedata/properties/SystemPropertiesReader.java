package org.bahmni.module.referencedata.properties;

public class SystemPropertiesReader implements PropertiesReader {
    @Override
    public String getProperty(String key) {
        return System.getProperty(key);
    }
}

