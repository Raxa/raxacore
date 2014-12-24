package org.bahmni.module.admin.observation;

import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

public class DiagnosisMapperTest {
    @Test
    public void ignore_empty_diagnosis() throws ParseException {
        List<KeyValue> diagnosesKeyValues = Arrays.asList(new KeyValue("diagnosis", " "));

        ConceptService mockConceptService = mock(ConceptService.class);
        DiagnosisMapper diagnosisMapper = new DiagnosisMapper(mockConceptService);

        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterDateTime = "2012-01-01";
        encounterRow.diagnosesRows = diagnosesKeyValues;
        List<BahmniDiagnosisRequest> bahmniDiagnosis = diagnosisMapper.getBahmniDiagnosis(encounterRow);

        Assert.isTrue(bahmniDiagnosis.isEmpty(), "Should ignore empty diagnoses");
    }
}
