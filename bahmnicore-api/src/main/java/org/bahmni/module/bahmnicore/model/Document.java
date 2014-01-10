package org.bahmni.module.bahmnicore.model;

import java.text.ParseException;
import java.util.LinkedHashMap;

public class Document {

    String images;
    String testUUID;

    public Document(String images, String testUUID) {
        this.images = images;
        this.testUUID = testUUID;
    }

    public Document(LinkedHashMap post) throws ParseException {
        SimpleObjectExtractor extractor = new SimpleObjectExtractor(post);
        testUUID = extractor.extract("testUUID");
        images = extractor.extract("images");
    }


    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getTestUUID() {
        return testUUID;
    }

    public void setTestUUID(String testUUID) {
        this.testUUID = testUUID;
    }
}
