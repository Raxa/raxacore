package org.bahmni.module.admin.concepts.mapper;

import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.ConceptSetRow;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptReferenceTerm;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;

import java.util.ArrayList;
import java.util.List;

public class ConceptSetMapper {

    public ConceptSet map(ConceptSetRow conceptSetRow) {
        ConceptSet conceptSet = new ConceptSet();
        conceptSet.setUniqueName(conceptSetRow.name);
        conceptSet.setDisplayName(conceptSetRow.shortName);
        conceptSet.setClassName(conceptSetRow.conceptClass);
        conceptSet.setDescription(conceptSetRow.description);
        List<String> children = new ArrayList<>();
        for (KeyValue child : conceptSetRow.children) {
            children.add(child.getValue());
        }
        conceptSet.setChildren(children);
        conceptSet.setConceptReferenceTerm(getConceptReferenceTerm(conceptSetRow));
        return conceptSet;
    }

    private ConceptReferenceTerm getConceptReferenceTerm(ConceptSetRow conceptSetRow) {
        ConceptReferenceTerm conceptReferenceTerm = new ConceptReferenceTerm();
        conceptReferenceTerm.setReferenceTermCode(conceptSetRow.referenceTermCode);
        conceptReferenceTerm.setReferenceTermRelationship(conceptSetRow.referenceTermRelationship);
        conceptReferenceTerm.setReferenceTermSource(conceptSetRow.referenceTermSource);
        return conceptReferenceTerm;
    }
}
