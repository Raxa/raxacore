package org.bahmni.module.admin.csv.persister;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.concepts.mapper.ConceptSetMapper;
import org.bahmni.module.admin.csv.models.ConceptSetRow;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConceptSetPersister implements EntityPersister<ConceptSetRow> {

    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;
    private UserContext userContext;
    private static final org.apache.log4j.Logger log = Logger.getLogger(ConceptSetPersister.class);

    public void init(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public RowResult<ConceptSetRow> validate(ConceptSetRow conceptSetRow) {
        StringBuilder error = new StringBuilder();
        if (StringUtils.isEmpty(conceptSetRow.name)) {
            error.append("Concept Name not specified\n");
        }
        if (StringUtils.isEmpty(conceptSetRow.conceptClass)) {
            error.append("Concept Class not specified\n");
        }
        return new RowResult<>(new ConceptSetRow(), error.toString());

    }

    @Override
    public RowResult<ConceptSetRow> persist(ConceptSetRow conceptSetRow) {
        try {
            Context.openSession();
            Context.setUserContext(userContext);
            ConceptSet concept = new ConceptSetMapper().map(conceptSetRow);
            referenceDataConceptService.saveConceptSet(concept);
            return new RowResult<>(conceptSetRow);
        } catch (Throwable e) {
            log.error(e);
            Context.clearSession();
            return new RowResult<>(conceptSetRow, e);
        } finally {
            Context.flushSession();
            Context.closeSession();
        }

    }

}
