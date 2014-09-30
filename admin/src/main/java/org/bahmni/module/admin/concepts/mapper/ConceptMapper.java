package org.bahmni.module.admin.concepts.mapper;

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
        concept.setDescription(conceptRow.description);
        concept.setUniqueName(conceptRow.name);
        concept.setDisplayName(conceptRow.shortName);
        List<String> synonyms = new ArrayList<>();
        for (KeyValue synonym : conceptRow.getSynonyms()) {
            synonyms.add(synonym.getValue());
        }
        List<String> answers = new ArrayList<>();
        for (KeyValue answer : conceptRow.getAnswers()) {
            answers.add(answer.getValue());
        }
        concept.setSynonyms(synonyms);
        concept.setAnswers(answers);
        concept.setConceptReferenceTerm(getConceptReferenceTerm(conceptRow));
        return concept;
    }

    private ConceptReferenceTerm getConceptReferenceTerm(ConceptRow conceptRow) {
        ConceptReferenceTerm conceptReferenceTerm = new ConceptReferenceTerm();
        conceptReferenceTerm.setReferenceTermCode(conceptRow.referenceTermCode);
        conceptReferenceTerm.setReferenceTermRelationship(conceptRow.referenceTermRelationship);
        conceptReferenceTerm.setReferenceTermSource(conceptRow.referenceTermSource);
        return conceptReferenceTerm;
    }

}
