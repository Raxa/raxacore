package patientMatchingAlgorithm

import org.bahmni.csv.KeyValue
import org.bahmni.module.admin.csv.patientmatchingalgorithm.PatientMatchingAlgorithm
import org.openmrs.Patient

public class IdAndNameMatch extends PatientMatchingAlgorithm{
    @Override
    Patient run(List<Patient> patientList, List<KeyValue> patientAttributes) {
        String patientNameToMatch = getPatientGivenName(patientAttributes)

        for (Patient patient : patientList) {
            if (patient.getPatientIdentifier().getIdentifier().contains("GAN") && patient.getGivenName().equals(patientNameToMatch)) {
                return patient;
            }
        }
        return null;
    }

    private String getPatientGivenName(List<KeyValue> patientAttributes) {
        for (KeyValue patientAttribute : patientAttributes) {
            if(patientAttribute.getKey().equals("given_name")){
                return patientAttribute.getValue();
            }
        }
        return null;
    }
}