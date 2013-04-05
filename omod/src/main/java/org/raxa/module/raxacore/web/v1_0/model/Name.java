package org.raxa.module.raxacore.web.v1_0.model;

import org.openmrs.module.webservices.rest.SimpleObject;

public class Name{

    private String givenName;
    private String middleName;
    private String familyName;

    public Name(SimpleObject post) {
        SimpleObjectExtractor extractor = new SimpleObjectExtractor(post);
        givenName = extractor.extract("givenName");
        middleName = extractor.extract("middleName");
        familyName = extractor.extract("familyName");
    }

    public String getGivenName() {
        return givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getFamilyName() {
        return familyName;
    }
}
