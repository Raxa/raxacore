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
            this.value = obs.getValueCoded() != null ? obs.getValueCoded().getName(LocaleUtility.getDefaultLocale()).getName() : null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueData valueData = (ValueData) o;

        if (!conceptDataType.equals(valueData.conceptDataType)) return false;
        if (!value.equals(valueData.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + conceptDataType.hashCode();
        return result;
    }
}
