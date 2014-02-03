package org.bahmni.module.referencedatafeedclient.worker;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.domain.Department;
import org.bahmni.module.referencedatafeedclient.domain.ReferenceDataConcept;
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
import java.util.Set;

@Component
public class DepartmentEventWorker implements EventWorker {
    public static final String CONV_SET = "ConvSet";
    public static final String LAB_DEPARTMENTS = "Lab Departments";
    public static final String SUFFIX_FOR_DEPARTMENT = " Department";

    @Resource(name = "referenceDataHttpClient")
    private HttpClient httpClient;
    private final ReferenceDataFeedProperties referenceDataFeedProperties;
    private ConceptService conceptService;
    private ReferenceDataConceptService referenceDataConceptService;
    private EventWorkerUtility eventWorkerUtility;

    @Autowired
    public DepartmentEventWorker(HttpClient httpClient, ReferenceDataFeedProperties referenceDataFeedProperties,
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
            Department department = httpClient.get(referenceDataFeedProperties.getReferenceDataUri() + event.getContent(), Department.class);
            ReferenceDataConcept referenceDataConcept = new ReferenceDataConcept(department.getId(), department.getName()+ SUFFIX_FOR_DEPARTMENT, CONV_SET, ConceptDatatype.N_A_UUID);
            referenceDataConcept.setDescription(department.getDescription());
            referenceDataConcept.setSet(true);
            referenceDataConcept.setRetired(!department.isActive());

            referenceDataConcept.setSetMemberUuids(eventWorkerUtility.getExistingChildUuids(department.getId(), conceptService));

            Concept departmentConcept = referenceDataConceptService.saveConcept(referenceDataConcept);
            Concept labDepartmentsConcept = conceptService.getConceptByName(LAB_DEPARTMENTS);
            referenceDataConceptService.saveSetMembership(labDepartmentsConcept, departmentConcept);
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanUp(Event event) {

    }
}
