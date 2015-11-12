package obscalculator

import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction
import org.openmrs.module.bahmniemrapi.obscalculator.ObsValueCalculator

public class TestObsValueCalculator implements ObsValueCalculator {

    public static String DEFAULT_ENCOUNTER_UUID = "defaultEncounterUuid"

    @Override
    void run(BahmniEncounterTransaction bahmniEncounterTransaction) {
        bahmniEncounterTransaction.setEncounterUuid(DEFAULT_ENCOUNTER_UUID)

    }
}