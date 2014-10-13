package org.bahmni.module.admin.concepts.mapper;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.ConceptRow;
import org.bahmni.module.admin.csv.models.ConceptRows;
import org.bahmni.module.admin.csv.models.ConceptSetRow;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptReferenceTerm;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.openmrs.*;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConceptSetMapper {

    private ConceptMapper conceptMapper;

    public ConceptSetMapper() {
        conceptMapper = new ConceptMapper();
    }

    public ConceptSet map(ConceptSetRow conceptSetRow) {
        ConceptSet conceptSet = new ConceptSet();
        conceptSet.setUniqueName(conceptSetRow.name);
        conceptSet.setDisplayName(conceptSetRow.shortName);
        conceptSet.setClassName(conceptSetRow.conceptClass);
        conceptSet.setDescription(conceptSetRow.description);
        conceptSet.setChildren(getChildren(conceptSetRow));
        conceptSet.setConceptReferenceTerm(getConceptReferenceTerm(conceptSetRow));
        return conceptSet;
    }

    private List<String> getChildren(ConceptSetRow conceptSetRow) {
        List<String> children = new ArrayList<>();
        for (KeyValue child : conceptSetRow.getChildren()) {
            if (!StringUtils.isEmpty(child.getValue())) {
                children.add(child.getValue());
            }
        }
        return children;
    }


    private ConceptReferenceTerm getConceptReferenceTerm(ConceptSetRow conceptSetRow) {
        ConceptReferenceTerm conceptReferenceTerm = new ConceptReferenceTerm();
        conceptReferenceTerm.setReferenceTermCode(conceptSetRow.referenceTermCode);
        conceptReferenceTerm.setReferenceTermRelationship(conceptSetRow.referenceTermRelationship);
        conceptReferenceTerm.setReferenceTermSource(conceptSetRow.referenceTermSource);
        return conceptReferenceTerm;
    }

    public ConceptSetRow map(Concept concept){
        ConceptSetRow conceptSetRow = new ConceptSetRow();
        conceptSetRow.name = concept.getName(Context.getLocale()).getName();
        ConceptDescription description = concept.getDescription(Context.getLocale());
        if (description != null) {
            conceptSetRow.description = description.getDescription();
        }
        ConceptName shortName = concept.getShortNameInLocale(Context.getLocale());
        if (shortName != null) {
            conceptSetRow.shortName = shortName.getName();
        }
        conceptSetRow.conceptClass = concept.getConceptClass().getName();
        conceptSetRow.children = getSetMembers(concept);
        Collection<ConceptMap> conceptMappings = concept.getConceptMappings();
        if (conceptMappings != null && conceptMappings.size() > 0) {
            ConceptMap conceptMap = conceptMappings.iterator().next();
            conceptSetRow.referenceTermCode = conceptMap.getConceptReferenceTerm().getCode();
            conceptSetRow.referenceTermSource = conceptMap.getConceptReferenceTerm().getConceptSource().getName();
            conceptSetRow.referenceTermRelationship = conceptMap.getConceptMapType().getName();
        }
        return conceptSetRow;
    }

    public ConceptRows mapAll(Concept concept) {
        List<ConceptSetRow> conceptSetRowsList = new ArrayList<>();
        List<ConceptRow> conceptRowsList = new ArrayList<>();
        for (Concept setMember  : concept.getSetMembers()) {
            if(setMember.isSet()){
                ConceptRows conceptRows = mapAll(setMember);
                conceptSetRowsList.addAll(conceptRows.getConceptSetRows());
                conceptRowsList.addAll(conceptRows.getConceptRows());
            }
            else{
                conceptRowsList.addAll(conceptMapper.mapAll(setMember));
            }

        }
        conceptSetRowsList.add(map(concept));
        ConceptRows conceptRows = new ConceptRows();
        conceptRows.setConceptRows(conceptRowsList);
        conceptRows.setConceptSetRows(conceptSetRowsList);
        return conceptRows;
    }

    private List<KeyValue> getSetMembers(org.openmrs.Concept concept) {
        List<Concept> setMembersList = concept.getSetMembers();
        List<KeyValue> setMembers = new ArrayList<>();
        for (Concept setMember : setMembersList) {
            setMembers.add(new KeyValue("child", setMember.getName(Context.getLocale()).getName()));
        }
        return setMembers;
    }


}
