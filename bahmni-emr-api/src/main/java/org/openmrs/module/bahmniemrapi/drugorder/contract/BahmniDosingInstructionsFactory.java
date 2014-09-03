package org.openmrs.module.bahmniemrapi.drugorder.contract;

import org.openmrs.FreeTextDosingInstructions;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

public class BahmniDosingInstructionsFactory {

    public EncounterTransaction.DosingInstructions get(String dosingInstructionType, EncounterTransaction.DosingInstructions dosingInstructions) {
        if (FreeTextDosingInstructions.class.getName().equals(dosingInstructionType)) {
            return new BahmniFreeTextDosingInstructions(dosingInstructions);
        }
        return dosingInstructions;
    }

}
