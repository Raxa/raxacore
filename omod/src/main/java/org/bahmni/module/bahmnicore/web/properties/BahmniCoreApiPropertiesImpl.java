package org.bahmni.module.bahmnicore.web.properties;

import org.bahmni.module.bahmnicore.BahmniCoreApiProperties;
import org.bahmni.module.bahmnicore.datamigration.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BahmniCoreApiPropertiesImpl  implements BahmniCoreApiProperties {

    private PropertiesReader propertiesReader;

    @Autowired
    public BahmniCoreApiPropertiesImpl(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    @Override
    public String getImageDirectory() {
        return propertiesReader.getProperty("bahmnicore.images.directory");
    }

    @Override
    public ExecutionMode getExecutionMode() {
        String property = propertiesReader.getProperty("bahmnicore.datamigration.mode");
        return new ExecutionMode(property);
    }
}
