package org.bahmni.module.admin.csv.persister;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.concepts.mapper.ConceptMapper;
import org.bahmni.module.admin.csv.models.ConceptRow;
import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptService;
import org.openmrs.api.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConceptPersister implements EntityPersister<ConceptRow> {

    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;

    @Override
    public RowResult<ConceptRow> validate(ConceptRow conceptRow) {
        StringBuilder error = new StringBuilder();
        if (StringUtils.isEmpty(conceptRow.name)) {
            error.append("Concept Name not specified\n");
        }
        if (StringUtils.isEmpty(conceptRow.conceptClass)) {
            error.append("Concept Class not specified\n");
        }
        return new RowResult<>(new ConceptRow(), error.toString());
    }

    @Override
    public RowResult<ConceptRow> persist(ConceptRow conceptRow) {
        Concept concept = new ConceptMapper().map(conceptRow);
        referenceDataConceptService.saveConcept(concept);
        return new RowResult<>(conceptRow);
    }
}
