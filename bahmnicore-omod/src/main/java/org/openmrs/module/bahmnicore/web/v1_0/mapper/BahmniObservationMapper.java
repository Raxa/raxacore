package org.openmrs.module.bahmnicore.web.v1_0.mapper;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class BahmniObservationMapper {
    @Autowired
    private ConceptService conceptService;

    public BahmniObservationMapper() {
    }

    public BahmniObservationMapper(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public List<EncounterTransaction.Observation> map(EncounterTransaction encounterTransaction) {
        List<EncounterTransaction.Observation> observations = encounterTransaction.getObservations();
        sortGroupMembers(observations);
        return observations;
    }

    private void sortGroupMembers(List<EncounterTransaction.Observation> observations) {
        for (EncounterTransaction.Observation observation : observations) {
            if(observation.getGroupMembers() != null && observation.getGroupMembers().size() > 0) {
                sortObservations(observation.getGroupMembers(), observation.getConcept().getUuid());
                sortGroupMembers(observation.getGroupMembers());
            }
        }
    }

    private void sortObservations(List<EncounterTransaction.Observation> observation, String parentConceptUuid) {
        Concept concept = conceptService.getConceptByUuid(parentConceptUuid);
        final List<String> conceptUuids = getSetMemberSortWeight(concept);
        Collections.sort(observation, new Comparator<EncounterTransaction.Observation>() {
            @Override
            public int compare(EncounterTransaction.Observation o1, EncounterTransaction.Observation o2) {
                if (conceptUuids.indexOf(o1.getConcept().getUuid()) == (conceptUuids.indexOf(o2.getConcept().getUuid()))) {
                    return 0;
                }
                return conceptUuids.indexOf(o1.getConcept().getUuid()) < (conceptUuids.indexOf(o2.getConcept().getUuid())) ? -1 : 1;
            }
        });
    }

    private List<String> getSetMemberSortWeight(Concept concept) {
        List<Concept> setMembers = concept.getSetMembers();
        List<String> conceptUuids = new ArrayList<>();
        for (Concept setMember : setMembers) {
            conceptUuids.add(setMember.getUuid());
        }
        return conceptUuids;
    }
}
