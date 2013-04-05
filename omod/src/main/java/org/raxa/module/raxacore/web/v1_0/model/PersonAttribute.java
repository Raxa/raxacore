package org.raxa.module.raxacore.web.v1_0.model;

import org.openmrs.module.webservices.rest.SimpleObject;

public class PersonAttribute{

    private String personAttributeUuid;
    private String value;

    public PersonAttribute(SimpleObject post) {
        SimpleObjectExtractor extractor = new SimpleObjectExtractor(post);
        personAttributeUuid = extractor.extract("attributetype");
        value = extractor.extract("value");
    }

    public String getPersonAttributeUuid() {
        return personAttributeUuid;
    }

    public String getValue() {
        return value;
    }
}
