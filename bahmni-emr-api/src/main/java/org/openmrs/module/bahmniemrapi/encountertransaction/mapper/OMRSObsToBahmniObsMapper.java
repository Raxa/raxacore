package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.ObservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component(value = "omrsObsToBahmniObsMapper")
public class OMRSObsToBahmniObsMapper {
    private ETObsToBahmniObsMapper etObsToBahmniObsMapper;

    @Autowired
    public OMRSObsToBahmniObsMapper(ETObsToBahmniObsMapper etObsToBahmniObsMapper) {
        this.etObsToBahmniObsMapper = etObsToBahmniObsMapper;
    }

    public Collection<BahmniObservation> map(List<Obs> obsList, Collection<Concept> rootConcepts) {
        Collection<BahmniObservation> bahmniObservations = new ArrayList<>();
        for (Obs obs : obsList) {
            BahmniObservation bahmniObservation = map(obs);
            bahmniObservation.setConceptSortWeight(ConceptSortWeightUtil.getSortWeightFor(bahmniObservation.getConcept().getName(), rootConcepts));
            bahmniObservations.add(bahmniObservation);
        }

        return bahmniObservations;
    }

    public BahmniObservation map(Obs obs) {
        return etObsToBahmniObsMapper.map(new ObservationMapper().map(obs), obs.getEncounter().getEncounterDatetime(),obs.getEncounter().getUuid()
                , obs.getEncounter().getVisit().getStartDatetime(), Arrays.asList(obs.getConcept()), true);
    }
}
