package org.bahmni.module.admin.csv.persister;


import org.apache.commons.lang.StringUtils;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.Messages;
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
    public Messages validate(ConceptSetRow conceptSetRow) {
        Messages messages = new Messages();
        if (StringUtils.isEmpty(conceptSetRow.name)) {
            messages.add("Concept Name not specified\n");
        }
        if (StringUtils.isEmpty(conceptSetRow.conceptClass)) {
            messages.add("Concept Class not specified\n");
        }
        return messages;

    }

    @Override
    public Messages persist(ConceptSetRow conceptSetRow) {
        ConceptSet concept = new ConceptSetMapper().map(conceptSetRow);
        referenceDataConceptService.saveConcept(concept);
        return new Messages();
    }
}
