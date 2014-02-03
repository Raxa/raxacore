package org.bahmni.module.referncedatafeedclient.worker;

import org.bahmni.module.referncedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referncedatafeedclient.domain.ReferenceDataConcept;
import org.bahmni.module.referncedatafeedclient.domain.Panel;
import org.bahmni.module.referncedatafeedclient.domain.Test;
import org.bahmni.module.referncedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Component
public class PanelEventWorker implements EventWorker {
    public static final String LAB_SET = "LabSet";
    @Resource(name = "referenceDataHttpClient")
    private HttpClient httpClient;
    private final ReferenceDataFeedProperties referenceDataFeedProperties;
    private ConceptService conceptService;
    private ReferenceDataConceptService referenceDataConceptService;

    @Autowired
    public PanelEventWorker(HttpClient httpClient, ReferenceDataFeedProperties referenceDataFeedProperties, ConceptService conceptService, ReferenceDataConceptService referenceDataConceptService) {
        this.httpClient = httpClient;
        this.referenceDataFeedProperties = referenceDataFeedProperties;
        this.conceptService = conceptService;
        this.referenceDataConceptService = referenceDataConceptService;
    }

    @Override
    public void process(Event event) {
        try {
            Panel panel = httpClient.get(referenceDataFeedProperties.getReferenceDataUri() + event.getContent(), Panel.class);

            Concept laboratoryConcept = conceptService.getConceptByName(SampleEventWorker.LABORATORY);
            Concept panelConcept = conceptService.getConceptByUuid(panel.getId());
            Concept sampleConcept = findExistingSampleContainingThisPanel(panel, laboratoryConcept);
            if (sampleConcept != null && !isPanelsSampleSame(panel, sampleConcept)) {
                removePanelFromOldSample(sampleConcept, panelConcept);
            }

            createNewPanelConcept(panel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isPanelsSampleSame(Panel panel, Concept sampleConcept) {
        return panel.getSample().getId().equals(sampleConcept.getUuid());
    }

    private Concept findExistingSampleContainingThisPanel(Panel panel, Concept laboratoryConcept) {
        for (Concept sampleConcept : laboratoryConcept.getSetMembers()) {
            for (Concept panelConcept : sampleConcept.getSetMembers()) {
                if (panelConcept.getUuid().equals(panel.getId())) {
                    return sampleConcept;
                }
            }
        }
        return null;
    }

    private void removePanelFromOldSample(Concept sampleConcept, Concept panelConcept) {
        Collection<ConceptSet> conceptSets = sampleConcept.getConceptSets();
        ConceptSet matchingOldPanelConceptSet = getMatchingConceptSet(conceptSets, panelConcept);
        if (matchingOldPanelConceptSet != null) {
            conceptSets.remove(matchingOldPanelConceptSet);
            sampleConcept.setConceptSets(conceptSets);
            conceptService.saveConcept(sampleConcept);
        }
    }

    private ConceptSet getMatchingConceptSet(Collection<ConceptSet> conceptSets, Concept panelConcept) {
        for (ConceptSet conceptSet : conceptSets) {
            if (conceptSet.getConcept().equals(panelConcept)) {
                return conceptSet;
            }
        }
        return null;
    }

    private void createNewPanelConcept(Panel panel) {
        ReferenceDataConcept referenceDataConcept = new ReferenceDataConcept(panel.getId(), panel.getName(), LAB_SET, ConceptDatatype.N_A_UUID);
        referenceDataConcept.setDescription(panel.getDescription());
        referenceDataConcept.setShortName(panel.getShortName());
        referenceDataConcept.setSetMemberUuids(getTestUuids(panel));
        referenceDataConcept.setRetired(!panel.isActive());
        referenceDataConcept.setSet(true);

        Concept newPanelConcept = referenceDataConceptService.saveConcept(referenceDataConcept);
        addNewPanelToSample(panel, newPanelConcept);
    }

    private void addNewPanelToSample(Panel panel, Concept newPanelConcept) {
        Concept parentSampleConcept = conceptService.getConceptByUuid(panel.getSample().getId());
        referenceDataConceptService.saveSetMembership(parentSampleConcept, newPanelConcept);
    }

    private boolean doesSampleContainPanel(Concept sampleConcept, Concept panelConcept) {
        return sampleConcept.getSetMembers().contains(panelConcept);
    }

    private Set<String> getTestUuids(Panel panel) {
        HashSet<String> testUuids = new HashSet<>();
        for (Test test : panel.getTests()) {
            testUuids.add(test.getId());
        }
        return testUuids;
    }

    @Override
    public void cleanUp(Event event) {

    }
}
