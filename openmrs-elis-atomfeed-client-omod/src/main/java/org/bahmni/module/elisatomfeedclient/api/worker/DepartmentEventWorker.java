package org.bahmni.module.elisatomfeedclient.api.worker;

import org.bahmni.module.elisatomfeedclient.api.ReferenceDataFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.domain.Department;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Locale;

@Component
public class DepartmentEventWorker implements EventWorker {
    public static final String CONV_SET = "ConvSet";
    public static final String LAB_DEPARTMENTS = "Lab Departments";
    @Resource(name = "referenceDataHttpClient")
    private HttpClient httpClient;
    private final ReferenceDataFeedProperties referenceDataFeedProperties;
    private ConceptService conceptService;

    @Autowired
    public DepartmentEventWorker(HttpClient httpClient, ReferenceDataFeedProperties referenceDataFeedProperties, ConceptService conceptService) {
        this.httpClient = httpClient;
        this.referenceDataFeedProperties = referenceDataFeedProperties;
        this.conceptService = conceptService;
    }

    @Override
    public void process(Event event) {
        try {
            Department department = httpClient.get(referenceDataFeedProperties.getReferenceDataUri() + event.getContent(), Department.class);
            Concept concept = conceptService.getConceptByUuid(department.getId());
            if(concept == null) {
                concept = new Concept();
                concept.setUuid(department.getId());
                concept.setDatatype(conceptService.getConceptDatatypeByUuid(ConceptDatatype.N_A_UUID));
                concept.setConceptClass(conceptService.getConceptClassByName(CONV_SET));
            }
            addOrUpdateName(department, concept);
            addOrUpdateDescription(department, concept);
            Concept savedDepartmentConcept = conceptService.saveConcept(concept);
            Concept labDepartmentsConcept = conceptService.getConceptByName(LAB_DEPARTMENTS);
            if (!labDepartmentsConcept.getSetMembers().contains(savedDepartmentConcept)) {
                labDepartmentsConcept.addSetMember(savedDepartmentConcept);
                conceptService.saveConcept(labDepartmentsConcept);
            }
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    private void addOrUpdateDescription(Department department, Concept concept) {
        ConceptDescription description = concept.getDescription(Locale.ENGLISH);
        if(description != null) {
            description.setDescription(department.getDescription());
        } else {
            concept.addDescription(new ConceptDescription(department.getDescription(), Locale.ENGLISH));
        }
    }

    private void addOrUpdateName(Department department, Concept concept) {
        ConceptName name = concept.getName(Locale.ENGLISH);
        if(name != null) {
            name.setName(department.getName());
        } else {
            concept.addName(new ConceptName(department.getName(), Locale.ENGLISH));
        }
    }

    @Override
    public void cleanUp(Event event) {

    }
}
