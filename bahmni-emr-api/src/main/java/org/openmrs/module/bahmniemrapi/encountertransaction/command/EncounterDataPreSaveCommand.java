package org.openmrs.module.bahmniemrapi.encountertransaction.command;

import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.springframework.beans.factory.config.BeanPostProcessor;

public interface EncounterDataPreSaveCommand extends BeanPostProcessor{

    BahmniEncounterTransaction update(BahmniEncounterTransaction bahmniEncounterTransaction);
}
