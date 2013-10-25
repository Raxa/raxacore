package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.bahmnicore.ApplicationError;
import org.bahmni.module.bahmnicore.model.BahmniLabResult;
import org.bahmni.module.bahmnicore.service.BahmniLabResultService;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.EncounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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
                Obs obs = findExistingObs(encounter, order);
                try {
                    if(obs != null) {
                        update(obs, bahmniLabResult);
                    } else {
                        obs = save(bahmniLabResult, order);
                        encounter.addObs(obs);
                    }
                } catch (ParseException e) {
                    throw new ApplicationError("Error parsing Lab Result: ", e);
                }

            }
        }

        encounterService.saveEncounter(encounter);
    }

    private Obs save(BahmniLabResult bahmniLabResult, Order order) throws ParseException {
        Obs obs = new Obs();
        obs.setConcept(order.getConcept());
        obs.setOrder(order);
        obs.setComment(bahmniLabResult.getComments());
        obs.setAccessionNumber(bahmniLabResult.getAccessionNumber());
        obs.setValueAsString(bahmniLabResult.getResult());
        return obs;
    }

    private Obs update(Obs existingObs, BahmniLabResult bahmniLabResult) throws ParseException {
        existingObs.setComment(bahmniLabResult.getComments());
        existingObs.setAccessionNumber(bahmniLabResult.getAccessionNumber());
        existingObs.setValueAsString(bahmniLabResult.getResult());
        return existingObs;
    }

    private Obs findExistingObs(Encounter encounter, Order order) {
        for (Obs obs : encounter.getObs()) {
            if(obs.getOrder().equals(order)) {
                return obs;
            }
        }
        return null;
    }
}
