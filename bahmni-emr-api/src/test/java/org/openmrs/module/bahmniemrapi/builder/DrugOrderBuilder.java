/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.bahmniemrapi.builder;

import org.openmrs.module.emrapi.CareSettingType;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class DrugOrderBuilder {

    private final EncounterTransaction.DrugOrder drugOrder;

    public DrugOrderBuilder() {
        drugOrder = new EncounterTransaction.DrugOrder();
        drugOrder.setCareSetting(CareSettingType.OUTPATIENT);
        drugOrder.setOrderType("Drug Order");
        withDrugUuid(UUID.randomUUID().toString());
        drugOrder.setDosingInstructionType("org.openmrs.SimpleDosingInstructions");
        EncounterTransaction.DosingInstructions dosingInstructions = DosingInstructionsBuilder.sample();
        drugOrder.setDosingInstructions(dosingInstructions);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        drugOrder.setScheduledDate(calendar.getTime());
        calendar.add(Calendar.MONTH, 1);
        EncounterTransaction.Provider provider = new EncounterTransaction.Provider();
        provider.setUuid("331c6bf8-7846-11e3-a96a-0800271c1b75");
        drugOrder.setAction("NEW");
        drugOrder.setDuration(2);
        drugOrder.setDurationUnits("Day");
    }

    public EncounterTransaction.DrugOrder build() {
        return drugOrder;
    }

    public DrugOrderBuilder withDurationUnits(String durationUnits) {
        drugOrder.setDurationUnits(durationUnits);
        return this;
    }

    public DrugOrderBuilder withDrugUuid(String drugUuid) {
        EncounterTransaction.Drug drug = new EncounterTransaction.Drug();
        drug.setUuid(drugUuid);
        drugOrder.setDrug(drug);
        return this;
    }

    public DrugOrderBuilder withNonCodedDrug(String freeTextDrug) {
        drugOrder.setDrugNonCoded(freeTextDrug);
        return this;
    }

    public DrugOrderBuilder withScheduledDate(Date scheduledDate) {
        drugOrder.setScheduledDate(scheduledDate);
        return this;
    }

    public DrugOrderBuilder withFrequency(String frequency) {
        drugOrder.getDosingInstructions().setFrequency(frequency);
        return this;
    }


    public DrugOrderBuilder withAction(String action) {
        drugOrder.setAction(action);
        return this;
    }

    public DrugOrderBuilder withPreviousOrderUuid(String previousOrderUuid) {
        drugOrder.setPreviousOrderUuid(previousOrderUuid);
        return this;
    }

    public DrugOrderBuilder withAutoExpireDate(Date date) {
        drugOrder.setAutoExpireDate(date);
        return this;
    }
}
