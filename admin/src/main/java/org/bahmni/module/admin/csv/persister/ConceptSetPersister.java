package org.bahmni.module.admin.csv.persister;


import org.apache.commons.lang.StringUtils;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.concepts.mapper.ConceptSetMapper;
import org.bahmni.module.admin.csv.models.ConceptSetRow;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConceptSetPersister implements EntityPersister<ConceptSetRow> {

    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;

    @Override
    public RowResult<ConceptSetRow> validate(ConceptSetRow conceptSetRow) {
        StringBuilder error = new StringBuilder();
        if (StringUtils.isEmpty(conceptSetRow.name)) {
            error.append("Concept Name not specified\n");
        }
        if (StringUtils.isEmpty(conceptSetRow.conceptClass)) {
            error.append("Concept Class not specified\n");
        }
        return new RowResult<>(conceptSetRow, error.toString());

    }

    @Override
    public RowResult<ConceptSetRow> persist(ConceptSetRow conceptSetRow) {
        ConceptSet concept = new ConceptSetMapper().map(conceptSetRow);
        referenceDataConceptService.saveConcept(concept);
        return new RowResult<>(conceptSetRow);
    }
}
