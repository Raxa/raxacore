package org.raxa.module.raxacore.web.v1_0.model;

import org.openmrs.module.webservices.rest.SimpleObject;

public class SimpleObjectExtractor {

    private SimpleObject post;

    public SimpleObjectExtractor(SimpleObject post) {
        this.post = post;
    }

    public <T> T extract(String key) {
        return (post == null || key == null) ? null : (T) post.get(key)  ;
    }
}
