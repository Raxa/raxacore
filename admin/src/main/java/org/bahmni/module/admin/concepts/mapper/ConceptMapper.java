package org.bahmni.module.admin.concepts.mapper;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.ConceptRow;
import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptReferenceTerm;

import java.util.ArrayList;
import java.util.List;

public class ConceptMapper {

    public Concept map(ConceptRow conceptRow) {
        Concept concept = new Concept();
        concept.setClassName(conceptRow.conceptClass);
        concept.setDataType(conceptRow.getDataType());
        concept.setDescription(conceptRow.getDescription());
        concept.setUniqueName(conceptRow.name);
        concept.setDisplayName(conceptRow.getShortName());
        addSynonyms(conceptRow, concept);
        addAnswers(conceptRow, concept);
        addConceptReferenceTerm(conceptRow, concept);
        return concept;
    }

    private void addSynonyms(ConceptRow conceptRow, Concept concept) {
        List<String> synonyms = new ArrayList<>();
        for (KeyValue synonym : conceptRow.getSynonyms()) {
            if (!StringUtils.isEmpty(synonym.getValue())) {
                synonyms.add(synonym.getValue());
            }
        }
        concept.setSynonyms(synonyms);
    }

    private void addAnswers(ConceptRow conceptRow, Concept concept) {
        List<String> answers = new ArrayList<>();
        for (KeyValue answer : conceptRow.getAnswers()) {
            if (!StringUtils.isEmpty(answer.getValue())) {
                answers.add(answer.getValue());
            }
        }
        concept.setAnswers(answers);
    }

    private void addConceptReferenceTerm(ConceptRow conceptRow, Concept concept) {
        ConceptReferenceTerm conceptReferenceTerm = new ConceptReferenceTerm();
        conceptReferenceTerm.setReferenceTermCode(conceptRow.getReferenceTermCode());
        conceptReferenceTerm.setReferenceTermRelationship(conceptRow.getReferenceTermRelationship());
        conceptReferenceTerm.setReferenceTermSource(conceptRow.getReferenceTermSource());
        concept.setConceptReferenceTerm(conceptReferenceTerm);
    }

}
