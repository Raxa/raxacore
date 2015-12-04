package org.openmrs.module.bahmniemrapi.obscalculator;

import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;

public interface ObsValueCalculator {
    public void run(BahmniEncounterTransaction bahmniEncounterTransaction);
}
