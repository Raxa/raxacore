package org.bahmni.module.admin.concepts.mapper;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.ConceptRow;
import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptReferenceTerm;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConceptMapper {


    public ConceptMapper() {
    }

    public Concept map(ConceptRow conceptRow) {
        Concept concept = new Concept();
        concept.setUuid(conceptRow.getUuid());
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

    public ConceptRow map(org.openmrs.Concept concept) {
        String conceptReferenceTermCode = null, conceptReferenceTermSource = null,
                conceptReferenceTermRelationship = null, conceptDescription = null, conceptShortname = null;
        String name = concept.getName(Context.getLocale()).getName();
        ConceptDescription description = concept.getDescription(Context.getLocale());
        if (description != null) {
            conceptDescription = description.getDescription();
        }
        ConceptName shortName = concept.getShortNameInLocale(Context.getLocale());
        if (shortName != null) {
            conceptShortname = shortName.getName();
        }
        String conceptClass = concept.getConceptClass().getName();
        String conceptDatatype = concept.getDatatype().getName();
        List<KeyValue> conceptSynonyms = getSynonyms(concept);
        List<KeyValue> conceptAnswers = getAnswers(concept);
        Collection<ConceptMap> conceptMappings = concept.getConceptMappings();
        if (conceptMappings != null && conceptMappings.size() > 0) {
            ConceptMap conceptMap = conceptMappings.iterator().next();
            conceptReferenceTermCode = conceptMap.getConceptReferenceTerm().getCode();
            conceptReferenceTermSource = conceptMap.getConceptReferenceTerm().getConceptSource().getName();
            conceptReferenceTermRelationship = conceptMap.getConceptMapType().getName();
        }
        String uuid = concept.getUuid();
        ConceptRow conceptRow = new ConceptRow(uuid, name, conceptDescription, conceptClass, conceptShortname,
                conceptReferenceTermCode, conceptReferenceTermRelationship, conceptReferenceTermSource,
                conceptDatatype, conceptSynonyms, conceptAnswers);
        return conceptRow;
    }

    public List<ConceptRow> mapAll(org.openmrs.Concept concept) {
        List<ConceptRow> conceptRows = new ArrayList<>();
        for (ConceptAnswer conceptAnswer : concept.getAnswers()) {
            conceptRows.addAll(mapAll(conceptAnswer.getAnswerConcept()));
        }
        conceptRows.add(map(concept));
        return conceptRows;
    }


    private List<KeyValue> getAnswers(org.openmrs.Concept concept) {
        Collection<ConceptAnswer> answersList = concept.getAnswers();
        List<KeyValue> answers = new ArrayList<>();
        for (ConceptAnswer answer : answersList) {
            answers.add(new KeyValue("answer", answer.getAnswerConcept().getName(Context.getLocale()).getName()));
        }
        return answers;
    }

    private List<KeyValue> getSynonyms(org.openmrs.Concept concept) {
        Collection<ConceptName> synonymsList = concept.getSynonyms();
        List<KeyValue> synonyms = new ArrayList<>();
        for (ConceptName synonym : synonymsList) {
            synonyms.add(new KeyValue("synonym", synonym.getName()));
        }
        return synonyms;
    }
}
