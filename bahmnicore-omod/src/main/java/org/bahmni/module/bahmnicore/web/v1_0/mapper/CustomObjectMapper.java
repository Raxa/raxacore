package org.bahmni.module.bahmnicore.web.v1_0.mapper;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.openmrs.Concept;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper() {
        super();
        configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        _serializationConfig.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        _serializationConfig.addMixInAnnotations(Concept.class, ConceptMixin.class);
        _deserializationConfig.addMixInAnnotations(Concept.class, ConceptMixin.class);
    }
}
