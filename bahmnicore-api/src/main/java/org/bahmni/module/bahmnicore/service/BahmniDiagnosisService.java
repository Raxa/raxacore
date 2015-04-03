package org.bahmni.module.bahmnicore.service;

import org.openmrs.Obs;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;

import java.util.List;

public interface BahmniDiagnosisService {
    void delete(String diagnosisObservationUuid);
    List<BahmniDiagnosisRequest> getBahmniDiagnosisByPatientAndVisit(String patientUuid,String visitUuid);
    List<BahmniDiagnosisRequest> getBahmniDiagnosisByPatientAndDate(String patientUuid, String date);
}
