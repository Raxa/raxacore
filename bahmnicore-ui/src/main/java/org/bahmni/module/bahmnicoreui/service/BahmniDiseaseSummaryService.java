package org.bahmni.module.bahmnicoreui.service;

import org.bahmni.module.bahmnicoreui.contract.ConceptValue;
import org.bahmni.module.bahmnicoreui.contract.DiseaseDataParams;
import org.bahmni.module.bahmnicoreui.contract.ConceptValue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BahmniDiseaseSummaryService {

    Map<String,Map<String, ConceptValue>> getDiseaseSummary(String patientUuid, DiseaseDataParams queryParams);
}


