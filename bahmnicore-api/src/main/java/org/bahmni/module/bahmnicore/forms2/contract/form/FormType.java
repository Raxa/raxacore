package org.bahmni.module.bahmnicore.forms2.contract.form;

public enum FormType {

    ALL_OBSERVATION_TEMPLATE_FORMS("v1"), FORM_BUILDER_FORMS("v2");

    private final String type;

    FormType(String type) {
        this.type = type;
    }

    public String get() {
        return type;
    }
}
