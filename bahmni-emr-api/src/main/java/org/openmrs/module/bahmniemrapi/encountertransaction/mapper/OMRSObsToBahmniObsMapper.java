package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmrs.Obs;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.ObservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "omrsObsToBahmniObsMapper")
public class OMRSObsToBahmniObsMapper {
    private ETObsToBahmniObsMapper etObsToBahmniObsMapper;

    @Autowired
    public OMRSObsToBahmniObsMapper(ETObsToBahmniObsMapper etObsToBahmniObsMapper) {
        this.etObsToBahmniObsMapper = etObsToBahmniObsMapper;
    }

    public List<BahmniObservation> map(List<Obs> obsList) {
        List<BahmniObservation> bahmniObservations = new ArrayList<>();
        for (Obs obs : obsList) {
            bahmniObservations.add(map(obs));
        }
        return bahmniObservations;
    }

    public BahmniObservation map(Obs obs) {
        return etObsToBahmniObsMapper.map(new ObservationMapper().map(obs), obs.getEncounter().getEncounterDatetime(),
                obs.getEncounter().getVisit().getStartDatetime(), Arrays.asList(obs.getConcept()), true);
    }
}
