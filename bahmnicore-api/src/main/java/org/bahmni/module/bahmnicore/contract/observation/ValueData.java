package org.bahmni.module.bahmnicore.contract.observation;


import org.openmrs.ConceptDatatype;
import org.openmrs.Obs;
import org.openmrs.util.LocaleUtility;

import java.util.Locale;

public class ValueData {
    private Object value;
    private String conceptDataType;

    public ValueData() {
    }

    public ValueData(Obs obs) {
        if (obs.getConcept().getDatatype().getHl7Abbreviation().equals(ConceptDatatype.CODED)) {
            this.value = obs.getValueCoded().getName(LocaleUtility.getDefaultLocale()).getName();
        } else {
            this.value = obs.getValueAsString(Locale.getDefault());
        }
        this.conceptDataType = obs.getConcept().getDatatype().getName();
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getConceptDataType() {
        return conceptDataType;
    }

    public void setConceptDataType(String conceptDataType) {
        this.conceptDataType = conceptDataType;
    }
}
