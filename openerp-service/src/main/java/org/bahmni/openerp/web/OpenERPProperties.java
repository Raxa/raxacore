package org.bahmni.openerp.web;

import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class OpenERPProperties {
    private Properties properties;

    private OpenERPProperties(Properties properties) {
        this.properties = properties;
    }

    public static OpenERPProperties load() throws IOException {
        FileInputStream propFile =  new FileInputStream(new File(OpenmrsUtil.getApplicationDataDirectory(), "openerp.properties").getAbsolutePath());
        Properties properties = new Properties(System.getProperties());
        properties.load(propFile);
        return load(properties);
    }

    private static OpenERPProperties load(Properties properties) {
        return new OpenERPProperties(properties);
    }

    public String get(String key){
        return properties.getProperty(key);
    }
}
