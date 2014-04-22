package org.openmrs.module.bahmnicore.web.v1_0.mapper;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.text.SimpleDateFormat;

public class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper() {
        super();
        configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        _serializationConfig.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
    }
}
