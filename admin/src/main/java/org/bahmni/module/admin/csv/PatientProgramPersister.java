package org.bahmni.module.admin.csv;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.PatientProgramRow;
import org.bahmni.module.admin.csv.service.PatientMatchService;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientProgramPersister implements EntityPersister<PatientProgramRow> {
    @Autowired
    private PatientMatchService patientMatchService;
    @Autowired
    private ProgramWorkflowService programWorkflowService;

    private UserContext userContext;

    private static final Logger log = Logger.getLogger(PatientProgramPersister.class);
    private String patientMatchingAlgorithmClassName;

    public void init(UserContext userContext, String patientMatchingAlgorithmClassName) {
        this.userContext = userContext;
        this.patientMatchingAlgorithmClassName = patientMatchingAlgorithmClassName;
    }

    @Override
    public RowResult<PatientProgramRow> validate(PatientProgramRow patientProgramRow) {
        return new RowResult<>(patientProgramRow);
    }

    @Override
    public RowResult<PatientProgramRow> persist(PatientProgramRow patientProgramRow) {
        // This validation is needed as patientservice get returns all patients for empty patient identifier
        if (StringUtils.isEmpty(patientProgramRow.patientIdentifier)) {
            return noMatchingPatients(patientProgramRow);
        }

        try {
            Context.openSession();
            Context.setUserContext(userContext);

            Patient patient = patientMatchService.getPatient(patientMatchingAlgorithmClassName, patientProgramRow.patientAttributes, patientProgramRow.patientIdentifier);
            if (patient == null) {
                return noMatchingPatients(patientProgramRow);
            }

            Program program = programWorkflowService.getProgramByName(patientProgramRow.programName);
            List<PatientProgram> existingEnrolledPrograms = programWorkflowService.getPatientPrograms(patient, program, null, null, null, null, false);
            if (existingEnrolledPrograms != null && !existingEnrolledPrograms.isEmpty()) {
                return new RowResult<>(patientProgramRow, getErrorMessage(existingEnrolledPrograms));
            }

            PatientProgram patientProgram = new PatientProgram();
            patientProgram.setPatient(patient);
            patientProgram.setProgram(program);
            patientProgram.setDateEnrolled(patientProgramRow.getEnrollmentDate());

            programWorkflowService.savePatientProgram(patientProgram);

        } catch (Exception e) {
            log.error(e);
            Context.clearSession();
            return new RowResult<>(patientProgramRow, e);
        } finally {
            Context.flushSession();
            Context.closeSession();
        }
        return new RowResult<>(patientProgramRow);
    }

    private String getErrorMessage(List<PatientProgram> patientPrograms) {
        PatientProgram existingProgram = patientPrograms.get(0);
        String errorMessage = "Patient already enrolled in " + existingProgram.getProgram().getName() + " from " + existingProgram.getDateEnrolled();
        errorMessage += existingProgram.getDateCompleted() == null ? "" : " to " + existingProgram.getDateCompleted() ;
        return errorMessage;
    }

    private RowResult<PatientProgramRow> noMatchingPatients(PatientProgramRow patientProgramRow) {
        return new RowResult<>(patientProgramRow, "No matching patients found with ID:'" + patientProgramRow.patientIdentifier + "'");
    }
}