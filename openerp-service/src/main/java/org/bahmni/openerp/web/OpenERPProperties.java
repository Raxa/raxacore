package org.bahmni.openerp.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
        File file = new File(OpenmrsUtil.getApplicationDataDirectory(), "openerp.properties");
        FileInputStream propFile =  new FileInputStream(file);
        Properties properties = new Properties(System.getProperties());
        properties.load(propFile);
        return new OpenERPProperties(properties);
    }

    public String get(String key){
        return properties.getProperty(key);
    }
}
