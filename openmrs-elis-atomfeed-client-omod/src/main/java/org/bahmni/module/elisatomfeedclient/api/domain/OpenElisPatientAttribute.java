package org.bahmni.module.elisatomfeedclient.api.domain;

import lombok.Data;

@Data
public class OpenElisPatientAttribute {
    private String name;
    private String value;

    public OpenElisPatientAttribute() {
    }

    public OpenElisPatientAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
