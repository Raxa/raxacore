package org.openmrs.module.bahmniemrapi.disposition.mapper;

import org.openmrs.User;
import org.openmrs.module.bahmniemrapi.disposition.contract.BahmniDisposition;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class BahmniDispositionMapper {

    public BahmniDisposition map(EncounterTransaction.Disposition disposition, Set<EncounterTransaction.Provider> providers, User user){
        BahmniDisposition bahmniDisposition = new BahmniDisposition();
        bahmniDisposition.setAdditionalObs(disposition.getAdditionalObs());
        bahmniDisposition.setCode(disposition.getCode());
        bahmniDisposition.setConceptName(disposition.getConceptName());
        bahmniDisposition.setDispositionDateTime(disposition.getDispositionDateTime());
        bahmniDisposition.setVoided(disposition.isVoided());
        bahmniDisposition.setExistingObs(disposition.getExistingObs());
        bahmniDisposition.setVoidReason(disposition.getVoidReason());
        bahmniDisposition.setProviders(providers);
        bahmniDisposition.setCreatorName(user.getPersonName().toString());

        return bahmniDisposition;
    }

}
