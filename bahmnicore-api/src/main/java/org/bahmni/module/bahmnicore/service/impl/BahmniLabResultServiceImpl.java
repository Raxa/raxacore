package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.bahmnicore.model.BahmniLabResult;
import org.bahmni.module.bahmnicore.service.BahmniLabResultService;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.EncounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BahmniLabResultServiceImpl implements BahmniLabResultService {

    private EncounterService encounterService;

    @Autowired
    public BahmniLabResultServiceImpl(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    @Override
    public void add(BahmniLabResult bahmniLabResult) {
        if(StringUtils.isEmpty(bahmniLabResult.getEncounterUuid())) {
            return;
        }
        Encounter encounter = encounterService.getEncounterByUuid(bahmniLabResult.getEncounterUuid());

        Set<Order> orders = encounter.getOrders();
        for (Order order : orders) {
            if(order.getConcept().getUuid().equals(bahmniLabResult.getTestUuid())) {
                Obs obs = new Obs();
                obs.setConcept(order.getConcept());
                obs.setOrder(order);
                obs.setComment(bahmniLabResult.getComments());
                obs.setAccessionNumber(bahmniLabResult.getAccessionNumber());
                if(bahmniLabResult.getResultType().equals("Numeric")){
                    obs.setValueNumeric((Double.parseDouble(bahmniLabResult.getResult())));
                }
                else if(bahmniLabResult.getResultType().equals("String")){
                    obs.setValueText(bahmniLabResult.getResult());
                }
                encounter.addObs(obs);
            }
        }

        encounterService.saveEncounter(encounter);
    }
}
