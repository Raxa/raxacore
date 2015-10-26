package org.openmrs.module.bahmniemrapi.builder;

import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

public class BahmniObservationBuilder {
    private BahmniObservation bahmniObservation = new BahmniObservation();

    public BahmniObservationBuilder withConcept(EncounterTransaction.Concept concept) {
        bahmniObservation.setConcept(concept);
        return this;
    }

    public BahmniObservationBuilder withConcept(String name, boolean isSet) {
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept();
        concept.setName(name);
        concept.setSet(isSet);
        bahmniObservation.setConcept(concept);
        return this;
    }

    public BahmniObservationBuilder withValue(Object value) {
        bahmniObservation.setValue(value);
        return this;
    }

    public BahmniObservationBuilder withOrderUuid(String orderUuid) {
        bahmniObservation.setOrderUuid(orderUuid);
        return this;
    }

    public BahmniObservationBuilder withUuid(String uuid) {
        bahmniObservation.setUuid(uuid);
        return this;
    }

    public BahmniObservation build() {
        return bahmniObservation;
    }

    public BahmniObservationBuilder withGroupMember(BahmniObservation member) {
        bahmniObservation.addGroupMember(member);
        return this;
    }
}
