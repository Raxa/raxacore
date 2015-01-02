package org.openmrs.module.bahmniemrapi.builder;

import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

public class BahmniObservationBuilder {
    private BahmniObservation bahmniObservation=new BahmniObservation();

    public BahmniObservationBuilder withConcept(EncounterTransaction.Concept concept){
        bahmniObservation.setConcept(concept);
        return this;
    }

    public BahmniObservationBuilder withValue(Object value){
        bahmniObservation.setValue(value);
        return  this;
    }

    public BahmniObservationBuilder withOrderUuid(String orderUuid){
        bahmniObservation.setOrderUuid(orderUuid);
        return this;
    }

    public BahmniObservationBuilder withUuid(String uuid){
        bahmniObservation.setUuid(uuid);
        return this;
    }

    public BahmniObservation build(){
        return bahmniObservation;
    }
}
