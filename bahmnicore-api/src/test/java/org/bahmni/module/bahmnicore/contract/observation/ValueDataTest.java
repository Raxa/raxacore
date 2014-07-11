package org.bahmni.module.bahmnicore.contract.observation;


import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.util.LocaleUtility;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ValueDataTest {
    @Test
    public void value_coded_answer_should_be_set_as_name() throws Exception {
        Concept conceptAnswer = new Concept();
        conceptAnswer.setNames(getTrueConceptName());

        Concept concept = getNAConcept();

        Obs obs = new Obs();
        obs.setValueCoded(conceptAnswer);
        obs.setConcept(concept);

        ValueData valueData = new ValueData(obs);
        assertEquals("N/A", valueData.getConceptDataType());
        assertEquals("True", valueData.getValue());
    }

    @Test
    public void value_coded_answer_should_be_set_as_null_when_value_coded_absent() throws Exception {
        Concept conceptAnswer = new Concept();
        conceptAnswer.setNames(getTrueConceptName());

        Concept concept = getNAConcept();

        Obs obs = new Obs();
        obs.setConcept(concept);

        ValueData valueData = new ValueData(obs);
        assertEquals("N/A", valueData.getConceptDataType());
        assertNull(valueData.getValue());
    }

    private Concept getNAConcept() {
        Concept concept = new Concept();
        concept.setDatatype(getNAConceptDatatype());
        return concept;
    }

    private List<ConceptName> getTrueConceptName() {
        ConceptName conceptName = new ConceptName();
        conceptName.setName("True");
        conceptName.setLocale(LocaleUtility.getDefaultLocale());
        List<ConceptName> conceptNames = new ArrayList<>();
        conceptNames.add(conceptName);
        return conceptNames;
    }

    private ConceptDatatype getNAConceptDatatype() {
        ConceptDatatype conceptDatatype = new ConceptDatatype();
        conceptDatatype.setName("N/A");
        conceptDatatype.setHl7Abbreviation(ConceptDatatype.CODED);
        return conceptDatatype;
    }
}
