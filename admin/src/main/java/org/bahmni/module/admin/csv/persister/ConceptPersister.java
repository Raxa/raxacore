package org.bahmni.module.admin.csv.persister;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.concepts.mapper.ConceptMapper;
import org.bahmni.module.admin.csv.models.ConceptRow;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.springframework.stereotype.Component;


@Component
public class ConceptPersister implements EntityPersister<ConceptRow> {
    //    @Autowired
//    private ReferenceDataConceptService conceptService;
    private ConceptMapper conceptMapper;

    private static final org.apache.log4j.Logger log = Logger.getLogger(ConceptPersister.class);
    private UserContext userContext;

    public void init(UserContext userContext) {
        this.userContext = userContext;
        conceptMapper = new ConceptMapper();
    }

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
        try {
            Context.openSession();
            Context.setUserContext(userContext);
//            Concept concept = conceptMapper.map(conceptRow);
//            conceptService.saveConcept(concept);
            return new RowResult<>(conceptRow);
        } catch (Throwable e) {
            log.error(e);
            Context.clearSession();
            return new RowResult<>(conceptRow, e);
        } finally {
            Context.flushSession();
            Context.closeSession();
        }
    }

}
