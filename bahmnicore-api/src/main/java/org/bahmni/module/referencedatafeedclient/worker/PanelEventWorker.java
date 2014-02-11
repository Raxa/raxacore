package org.bahmni.module.referencedatafeedclient.worker;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.domain.Panel;
import org.bahmni.module.referencedatafeedclient.domain.ReferenceDataConcept;
import org.bahmni.module.referencedatafeedclient.domain.Test;
import org.bahmni.module.referencedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class PanelEventWorker implements EventWorker {
    public static final String LAB_SET = "LabSet";
    @Resource(name = "referenceDataHttpClient")
    private HttpClient httpClient;
    private final ReferenceDataFeedProperties referenceDataFeedProperties;
    private ConceptService conceptService;
    private ReferenceDataConceptService referenceDataConceptService;
    private EventWorkerUtility eventWorkerUtility;

    @Autowired
    public PanelEventWorker(HttpClient httpClient, ReferenceDataFeedProperties referenceDataFeedProperties,
                            ConceptService conceptService, ReferenceDataConceptService referenceDataConceptService,
                            EventWorkerUtility eventWorkerUtility) {
        this.httpClient = httpClient;
        this.referenceDataFeedProperties = referenceDataFeedProperties;
        this.conceptService = conceptService;
        this.referenceDataConceptService = referenceDataConceptService;
        this.eventWorkerUtility = eventWorkerUtility;
    }

    @Override
    public void process(Event event) {
        try {
            Panel panel = httpClient.get(referenceDataFeedProperties.getReferenceDataUri() + event.getContent(), Panel.class);
            Concept laboratoryConcept = conceptService.getConceptByName(SampleEventWorker.LABORATORY);
            Concept panelConcept = conceptService.getConceptByUuid(panel.getId());
            eventWorkerUtility.removeChildFromExistingParent(panelConcept, laboratoryConcept, panel.getId(), panel.getSample().getId());

            createNewPanelConcept(panel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanUp(Event event) {

    }

    private void createNewPanelConcept(Panel panel) {
        suffixPanelToNameIfTestWithSameNameExists(panel);

        ReferenceDataConcept referenceDataConcept = new ReferenceDataConcept(panel.getId(), panel.getName(), LAB_SET, ConceptDatatype.N_A_UUID);
        referenceDataConcept.setDescription(panel.getDescription());
        referenceDataConcept.setShortName(panel.getShortName());
        referenceDataConcept.setSetMemberUuids(getTestUuids(panel));
        referenceDataConcept.setRetired(!panel.getIsActive());
        referenceDataConcept.setSet(true);

        Concept newPanelConcept = referenceDataConceptService.saveConcept(referenceDataConcept);
        addNewPanelToSample(panel, newPanelConcept);
        if (newPanelConcept.isRetired()){
            removePanelFromSample(panel, newPanelConcept);
        }
    }

    private void suffixPanelToNameIfTestWithSameNameExists(Panel panel) {
        Concept conceptByName = conceptService.getConceptByName(panel.getName());
        if (conceptByName != null && ! conceptByName.getUuid().equals(panel.getId())) {
            panel.suffixPanelToName();
        }
    }

    private void removePanelFromSample(Panel panel, Concept newPanelConcept) {
        Concept parentSample = conceptService.getConceptByUuid(panel.getSample().getId());
        eventWorkerUtility.removeChildFromOldParent(parentSample, newPanelConcept);
    }

    private void addNewPanelToSample(Panel panel, Concept newPanelConcept) {
        Concept parentSampleConcept = conceptService.getConceptByUuid(panel.getSample().getId());
        referenceDataConceptService.saveSetMembership(parentSampleConcept, newPanelConcept);
    }

    private Set<String> getTestUuids(Panel panel) {
        HashSet<String> testUuids = new HashSet<>();
        for (Test test : panel.getTests()) {
            testUuids.add(test.getId());
        }
        return testUuids;
    }
}
