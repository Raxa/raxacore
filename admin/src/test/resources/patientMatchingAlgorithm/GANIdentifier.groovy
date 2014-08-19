package patientMatchingAlgorithm;

import org.bahmni.module.admin.csv.patientmatchingalgorithm.PatientMatchingAlgorithm
import org.openmrs.Patient;


public class GANIdentifier extends PatientMatchingAlgorithm {
    @Override
    Patient run(List<Patient> patientList, List<org.bahmni.csv.KeyValue> patientAttributes) {
        for (Patient patient : patientList) {
            if (patient.getPatientIdentifier().getIdentifier().contains("GAN")) {
                return patient;
            }
        }
        return null;
    }
}