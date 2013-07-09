package org.bahmni.module.bahmnicore.contract.patient.data;


import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.PersonAttributeType;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class PersonAttributeTypeDataTest {

    @Test
    public void shouldMapPatientAttribute(){
        PersonAttributeType personAttributeType = new PersonAttributeType() {{
            this.setUuid("uuid");
            this.setName("primaryContact");
            this.setDescription("Primary Contact");
            this.setFormat("java.lang.String");
            this.setSortWeight(10.0);
        }};

        PersonAttributeTypeData personAttributeTypeData = new PersonAttributeTypeData(personAttributeType, null);

        assertEquals("uuid", personAttributeTypeData.getUuid());
        assertEquals("primaryContact", personAttributeTypeData.getName());
        assertEquals("Primary Contact", personAttributeTypeData.getDescription());
        assertEquals("java.lang.String", personAttributeTypeData.getFormat());
        assertEquals(Double.valueOf(10.0), personAttributeTypeData.getSortWeight());

        List<AnswerData> answers = personAttributeTypeData.getAnswers();
        assertEquals(0, answers.size());
    }

    @Test
    public void shouldMapAnswersWhenAddingAttributeOfTypeConcept() throws Exception {
        PersonAttributeType personAttributeType = new PersonAttributeType() {{
            this.setName("class");
            this.setDescription("Class");
            this.setFormat("org.openmrs.Concept");
            this.setSortWeight(10.0);
            this.setForeignKey(10);
        }};
        final Concept obcConcept = new Concept(1);
        obcConcept.setFullySpecifiedName(new ConceptName("OBC", Locale.ENGLISH));
        final Concept scConcept = new Concept(2) {{
            this.setFullySpecifiedName(new ConceptName("SC", Locale.ENGLISH));
        }};
        Concept classConcept = new Concept(10) {{
            this.setFullySpecifiedName(new ConceptName("Class", Locale.ENGLISH));
            this.setAnswers(Arrays.asList(new ConceptAnswer(1) {{
                                              this.setAnswerConcept(obcConcept);
                                          }}, new ConceptAnswer(2) {{
                                              this.setAnswerConcept(scConcept);
                                          }}
            ));
        }};

        PersonAttributeTypeData personAttributeTypeData = new PersonAttributeTypeData(personAttributeType, classConcept);
        assertEquals("class", personAttributeTypeData.getName());
        assertEquals("Class", personAttributeTypeData.getDescription());
        assertEquals("org.openmrs.Concept", personAttributeTypeData.getFormat());
        assertEquals(Double.valueOf(10.0), personAttributeTypeData.getSortWeight());

        List<AnswerData> answers = personAttributeTypeData.getAnswers();
        assertEquals(2, answers.size());
        assertEquals("OBC", answers.get(0).getDescription());
        assertEquals("1", answers.get(0).getConceptId());
        assertEquals("SC", answers.get(1).getDescription());
        assertEquals("2", answers.get(1).getConceptId());

    }

}
