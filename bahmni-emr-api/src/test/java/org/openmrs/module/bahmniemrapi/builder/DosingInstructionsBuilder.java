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

import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

public class DosingInstructionsBuilder {

    public static EncounterTransaction.DosingInstructions sample() {
        EncounterTransaction.DosingInstructions dosingInstructions = new EncounterTransaction.DosingInstructions();
        dosingInstructions.setDose(2.0);
        dosingInstructions.setDoseUnits("Capsule");
        dosingInstructions.setRoute("PO");
        dosingInstructions.setFrequency("QDS");
        dosingInstructions.setAsNeeded(false);
        dosingInstructions.setAdministrationInstructions("AC");
        dosingInstructions.setQuantity(1.0);
        dosingInstructions.setQuantityUnits("bottle");
        dosingInstructions.setNumberOfRefills(1);
        return dosingInstructions;
    }
}
