package org.bahmni.module.bahmnicore.properties;

public class SystemPropertiesReader implements PropertiesReader {
    @Override
    public String getProperty(String key) {
        return System.getProperty(key);
    }
}
