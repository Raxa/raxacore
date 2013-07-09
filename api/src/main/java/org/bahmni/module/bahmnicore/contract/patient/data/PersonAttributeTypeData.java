package org.bahmni.module.bahmnicore.contract.patient.data;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.PersonAttributeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class PersonAttributeTypeData {

    private String name;
    private String description;
    private String format;
    private Double sortWeight;
    private List<AnswerData> answers = new ArrayList<>();
    private String uuid;

    public PersonAttributeTypeData(String name, String description, String format, Double sortWeight, String uuid) {
        this.name = name;
        this.description = description;
        this.format = format;
        this.sortWeight = sortWeight;
        this.uuid = uuid;
    }

    public PersonAttributeTypeData(PersonAttributeType personAttributeType, Concept concept) {
        this(personAttributeType.getName(), personAttributeType.getDescription(), personAttributeType.getFormat(), personAttributeType.getSortWeight(), personAttributeType.getUuid());
        if (concept != null) {
            Collection<ConceptAnswer> conceptAnswers = concept.getAnswers();
            for (ConceptAnswer conceptAnswer : conceptAnswers) {
                Concept answerConcept = conceptAnswer.getAnswerConcept();
                this.answers.add(new AnswerData(String.valueOf(answerConcept.getId()), answerConcept.getFullySpecifiedName(Locale.ENGLISH).getName()));
            }
        }
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public String getFormat() {
        return format;
    }

    public Double getSortWeight() {
        return sortWeight;
    }

    public List<AnswerData> getAnswers() {
        return answers;
    }

    public String getUuid() {
        return uuid;
    }
}
