import org.bahmni.module.admin.csv.patientmatchingalgorithm.PatientMatchingAlgorithm
import org.bahmni.module.admin.csv.patientmatchingalgorithm.exception.CannotMatchPatientException
import org.openmrs.Patient

public class MultipleMatchPatient extends PatientMatchingAlgorithm {
    @Override
    Patient run(List<Patient> patientList, List<org.bahmni.csv.KeyValue> patientAttributes) {
        throw new CannotMatchPatientException(patientList);
    }
}