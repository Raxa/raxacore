package org.bahmni.module.elisatomfeedclient.api.mapper;

import org.bahmni.module.bahmnicore.model.BahmniLabResult;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisLabResult;

public class BahmniLabResultMapper {

    public BahmniLabResult map(OpenElisLabResult openElisLabResult){
        BahmniLabResult bahmniLabResult = new BahmniLabResult();

        bahmniLabResult.setEncounterUuid(openElisLabResult.getOrderId());
        bahmniLabResult.setAccessionNumber(openElisLabResult.getAccessionNumber());
        bahmniLabResult.setPatientUuid(openElisLabResult.getPatientExternalId());
        bahmniLabResult.setResult(openElisLabResult.getResult());
        bahmniLabResult.setTestUuid(openElisLabResult.getTestExternalId());
        bahmniLabResult.setComments(openElisLabResult.getAlerts());
        bahmniLabResult.setNotes(openElisLabResult.getNotes());
        return bahmniLabResult;
    }
}
