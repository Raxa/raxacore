package org.bahmni.module.admin.csv;

import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.PatientProgramRow;
import org.bahmni.module.admin.csv.patientmatchingalgorithm.BahmniPatientMatchingAlgorithm;
import org.bahmni.module.admin.csv.patientmatchingalgorithm.PatientMatchingAlgorithm;
import org.bahmni.module.admin.csv.patientmatchingalgorithm.exception.CannotMatchPatientException;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Component
public class PatientProgramPersister implements EntityPersister<PatientProgramRow> {

    @Autowired
    private BahmniPatientService patientService;
    @Autowired
    private ProgramWorkflowService programWorkflowService;

    private UserContext userContext;
    private String patientMatchingAlgorithmClassName;

    private static final Logger log = Logger.getLogger(PatientProgramPersister.class);
    public static final String PATIENT_MATCHING_ALGORITHM_DIRECTORY = "/patientMatchingAlgorithm/";

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

            List<Patient> matchingPatients = patientService.get(patientProgramRow.patientIdentifier);
            Patient patient = matchPatients(matchingPatients, patientProgramRow.patientAttributes);

            Program program = programWorkflowService.getProgramByName(patientProgramRow.programName);

            List<PatientProgram> patientPrograms = programWorkflowService.getPatientPrograms(patient, program, null, null, null, null, false);
            if (patientPrograms != null && !patientPrograms.isEmpty()) {
                PatientProgram existingProgram = patientPrograms.get(0);
                throw new RuntimeException("Patient already enrolled in " + patientProgramRow.programName + " from " + existingProgram.getDateEnrolled() + " to " + existingProgram.getDateCompleted());
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

    private Patient matchPatients(List<Patient> matchingPatients, List<KeyValue> patientAttributes) throws IOException, IllegalAccessException, InstantiationException, CannotMatchPatientException {
        if (patientMatchingAlgorithmClassName == null) {
            Patient patient = new BahmniPatientMatchingAlgorithm().run(matchingPatients, patientAttributes);
            return patient;
        }
        Class clazz = new GroovyClassLoader().parseClass(new File(getAlgorithmClassPath()));
        PatientMatchingAlgorithm patientMatchingAlgorithm = (PatientMatchingAlgorithm) clazz.newInstance();
        log.debug("PatientMatching : Using Algorithm in " + patientMatchingAlgorithm.getClass().getName());
        return patientMatchingAlgorithm.run(matchingPatients, patientAttributes);
    }

    private String getAlgorithmClassPath() {
        return OpenmrsUtil.getApplicationDataDirectory() + PATIENT_MATCHING_ALGORITHM_DIRECTORY + patientMatchingAlgorithmClassName;
    }

    private RowResult<PatientProgramRow> noMatchingPatients(PatientProgramRow patientProgramRow) {
        return new RowResult<>(patientProgramRow, "No matching patients found with ID:'" + patientProgramRow.patientIdentifier + "'");
    }

}
