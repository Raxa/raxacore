package org.openmrs.module.bahmniemrapi.drugorder.contract;

import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

public class BahmniFreeTextDosingInstructions extends EncounterTransaction.DosingInstructions {

    private EncounterTransaction.DosingInstructions dosingInstructions;

    public BahmniFreeTextDosingInstructions(EncounterTransaction.DosingInstructions dosingInstructions) {

        this.dosingInstructions = dosingInstructions;
    }

    //TODO: move out logic of calculating dose and dose units after adding migration to add them in database.
    @Override
    public Double getDose() {
        String instructions = dosingInstructions.getAdministrationInstructions();
        String[] splittedInstructions = instructions.split("\\s+");
        return Double.parseDouble(splittedInstructions[0]);
    }

    @Override
    public String getDoseUnits() {
        String instructions = dosingInstructions.getAdministrationInstructions();
        String[] splittedInstructions = instructions.split("\\s+");
        return splittedInstructions[1];
    }
}
