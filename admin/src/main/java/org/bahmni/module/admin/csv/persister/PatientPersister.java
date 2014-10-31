package org.bahmni.module.admin.csv.persister;

import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.PatientRow;
import org.bahmni.module.admin.csv.service.CSVPatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.springframework.stereotype.Component;

@Component
public class PatientPersister implements EntityPersister<PatientRow> {
    private UserContext userContext;

    private static final Logger log = Logger.getLogger(PatientPersister.class);

    public void init(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public RowResult<PatientRow> persist(PatientRow patientRow) {
        try {
            Context.openSession();
            Context.setUserContext(userContext);

            new CSVPatientService().save(patientRow);

            return new RowResult<>(patientRow);
        } catch (Exception e) {
            log.error(e);
            Context.clearSession();
            return new RowResult<>(patientRow, e);
        } finally {
            Context.flushSession();
            Context.closeSession();
        }
    }

    @Override
    public RowResult<PatientRow> validate(PatientRow csvEntity) {
        return new RowResult<PatientRow>(csvEntity);
    }
}
