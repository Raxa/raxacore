package org.bahmni.module.bahmnicore.web.properties;

import org.bahmni.openerp.web.OpenERPProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenERPPropertiesImpl implements OpenERPProperties {

    private PropertiesReader properties;
    private String OPENERP_PREFIX = "openerp.";

    @Autowired
    public OpenERPPropertiesImpl(PropertiesReader properties) {
        this.properties = properties;
    }

    @Override
    public String getHost() {
        return properties.getProperty(nameFor("host"));
    }

    @Override
    public int getPort() {
        return Integer.parseInt(properties.getProperty(nameFor("port")));
    }

    @Override
    public String getDatabase() {
        return properties.getProperty(nameFor("database"));
    }

    @Override
    public String getUser() {
        return properties.getProperty(nameFor("user"));
    }

    @Override
    public String getPassword() {
        return properties.getProperty(nameFor("password"));
    }

    private String nameFor(String key) {
        return OPENERP_PREFIX + key;
    }
}
