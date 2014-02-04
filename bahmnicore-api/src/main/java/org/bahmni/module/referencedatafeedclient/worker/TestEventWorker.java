package org.bahmni.module.referencedatafeedclient.worker;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.domain.ReferenceDataConcept;
import org.bahmni.module.referencedatafeedclient.domain.Test;
import org.bahmni.module.referencedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Component
public class TestEventWorker implements EventWorker {
    public static final String TEST = "Test";
    public static final String TEXT_CONCEPT_DATATYPE = "Text";

    @Resource(name = "referenceDataHttpClient")
    private HttpClient httpClient;
    private final ReferenceDataFeedProperties referenceDataFeedProperties;
    private ConceptService conceptService;
    private ReferenceDataConceptService referenceDataConceptService;
    private EventWorkerUtility eventWorkerUtility;

    @Autowired
    public TestEventWorker(HttpClient httpClient, ReferenceDataFeedProperties referenceDataFeedProperties,
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
            Test test = httpClient.get(referenceDataFeedProperties.getReferenceDataUri() + event.getContent(), Test.class);
            Concept testConcept = conceptService.getConceptByUuid(test.getId());
            Concept laboratoryConcept = conceptService.getConceptByName(SampleEventWorker.LABORATORY);
            eventWorkerUtility.removeChildFromExistingParent(testConcept, laboratoryConcept, test.getId(), test.getSample().getId());

            Concept labDepartmentConcept = conceptService.getConceptByName(DepartmentEventWorker.LAB_DEPARTMENTS);
            eventWorkerUtility.removeChildFromExistingParent(testConcept, labDepartmentConcept, test.getId(), test.getDepartment().getId());

            createNewTestConcept(test);
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanUp(Event event) {

    }

    private void createNewTestConcept(Test test) {
        ConceptDatatype conceptDataType = conceptService.getConceptDatatypeByName(test.getResultType());
        if (conceptDataType == null){
            conceptDataType = conceptService.getConceptDatatypeByName(TEXT_CONCEPT_DATATYPE);
        }
        ReferenceDataConcept referenceDataConcept = new ReferenceDataConcept(test.getId(), test.getName(), TEST, conceptDataType.getUuid());
        referenceDataConcept.setDescription(test.getDescription());
        referenceDataConcept.setShortName(test.getShortName());
        referenceDataConcept.setRetired(!test.getIsActive());
        Concept newTestConcept = referenceDataConceptService.saveConcept(referenceDataConcept);
        addNewTestToSampleAndDepartment(test, newTestConcept);
        if (newTestConcept.isRetired()){
            removeTestFromSampleDepartmentAndPanel(test, newTestConcept);
        }
    }

    private void removeTestFromSampleDepartmentAndPanel(Test test, Concept newTestConcept) {
        Concept parentSampleConcept = conceptService.getConceptByUuid(test.getSample().getId());
        eventWorkerUtility.removeChildFromOldParent(parentSampleConcept, newTestConcept);

        Concept parentDepartmentConcept = conceptService.getConceptByUuid(test.getDepartment().getId());
        eventWorkerUtility.removeChildFromOldParent(parentDepartmentConcept, newTestConcept);

        ConceptClass labSetConcept = conceptService.getConceptClassByName(PanelEventWorker.LAB_SET);
        List<Concept> allPanelConcepts = conceptService.getConceptsByClass(labSetConcept);
        for (Concept panelConcept : allPanelConcepts) {
            if (panelConcept.getSetMembers().contains(newTestConcept)) {
                eventWorkerUtility.removeChildFromOldParent(panelConcept, newTestConcept);
            }
        }
    }

    private void addNewTestToSampleAndDepartment(Test test, Concept newTestConcept) {
        Concept parentSampleConcept = conceptService.getConceptByUuid(test.getSample().getId());
        referenceDataConceptService.saveSetMembership(parentSampleConcept, newTestConcept);

        Concept parentDepartmentConcept = conceptService.getConceptByUuid(test.getDepartment().getId());
        referenceDataConceptService.saveSetMembership(parentDepartmentConcept, newTestConcept);
    }
}
