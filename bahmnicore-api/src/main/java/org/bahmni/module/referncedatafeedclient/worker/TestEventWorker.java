package org.bahmni.module.referncedatafeedclient.worker;

import org.bahmni.module.referncedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referncedatafeedclient.domain.ReferenceDataConcept;
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
import java.util.Collection;

@Component
public class TestEventWorker implements EventWorker {
    public static final String TEST = "Test";
    public static final String TEXT_CONCEPT_DATATYPE = "Text";

    @Resource(name = "referenceDataHttpClient")
    private HttpClient httpClient;
    private final ReferenceDataFeedProperties referenceDataFeedProperties;
    private ConceptService conceptService;
    private ReferenceDataConceptService referenceDataConceptService;

    @Autowired
    public TestEventWorker(HttpClient httpClient, ReferenceDataFeedProperties referenceDataFeedProperties, ConceptService conceptService, ReferenceDataConceptService referenceDataConceptService) {
        this.httpClient = httpClient;
        this.referenceDataFeedProperties = referenceDataFeedProperties;
        this.conceptService = conceptService;
        this.referenceDataConceptService = referenceDataConceptService;
    }

    @Override
    public void process(Event event) {
        try {
            Test test = httpClient.get(referenceDataFeedProperties.getReferenceDataUri() + event.getContent(), Test.class);

            Concept laboratoryConcept = conceptService.getConceptByName(SampleEventWorker.LABORATORY);
            Concept testConcept = conceptService.getConceptByUuid(test.getId());
            Concept sampleConcept = findExistingSampleContainingThisTest(test, laboratoryConcept);
            if (sampleConcept != null && !isTestsSampleSame(test, sampleConcept)) {
                removeTestFromOldSample(sampleConcept, testConcept);
            }

            Concept labDepartmentConcept = conceptService.getConceptByName(DepartmentEventWorker.LAB_DEPARTMENTS);
            Concept departmentConcept = findExistingDepartmentContainingThisTest(test, labDepartmentConcept);
            if (departmentConcept != null && !isTestsDepartmentSame(test, departmentConcept)) {
                removeTestFromOldDepartment(departmentConcept, testConcept);
            }

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
        referenceDataConcept.setRetired(!test.isActive());
        Concept newTestConcept = referenceDataConceptService.saveConcept(referenceDataConcept);
        addNewTestToSampleAndDepartment(test, newTestConcept);
    }

    private void addNewTestToSampleAndDepartment(Test test, Concept newTestConcept) {
        Concept parentSampleConcept = conceptService.getConceptByUuid(test.getSample().getId());
        referenceDataConceptService.saveSetMembership(parentSampleConcept, newTestConcept);

        Concept parentDepartmentConcept = conceptService.getConceptByUuid(test.getDepartment().getId());
        referenceDataConceptService.saveSetMembership(parentDepartmentConcept, newTestConcept);
    }


    private boolean isTestsSampleSame(Test test, Concept sampleConcept) {
        return test.getSample().getId().equals(sampleConcept.getUuid());
    }

    private boolean isTestsDepartmentSame(Test test, Concept departmentConcept) {
        return test.getSample().getId().equals(departmentConcept.getUuid());
    }

    private void removeTestFromOldSample(Concept sampleConcept, Concept testConcept) {
        Collection<ConceptSet> conceptSets = sampleConcept.getConceptSets();
        ConceptSet matchingOldTestConceptSet = getMatchingConceptSet(conceptSets, testConcept);
        if (matchingOldTestConceptSet != null) {
            conceptSets.remove(matchingOldTestConceptSet);
            sampleConcept.setConceptSets(conceptSets);
            conceptService.saveConcept(sampleConcept);
        }
    }

    private void removeTestFromOldDepartment(Concept departmentConcept, Concept testConcept) {
        Collection<ConceptSet> conceptSets = departmentConcept.getConceptSets();
        ConceptSet matchingOldTestConceptSet = getMatchingConceptSet(conceptSets, testConcept);
        if (matchingOldTestConceptSet != null) {
            conceptSets.remove(matchingOldTestConceptSet);
            departmentConcept.setConceptSets(conceptSets);
            conceptService.saveConcept(departmentConcept);
        }
    }

    private ConceptSet getMatchingConceptSet(Collection<ConceptSet> conceptSets, Concept testConcept) {
        for (ConceptSet conceptSet : conceptSets) {
            if (conceptSet.getConcept().equals(testConcept)) {
                return conceptSet;
            }
        }
        return null;
    }

    private Concept findExistingSampleContainingThisTest(Test test, Concept laboratoryConcept) {
        for (Concept sampleConcept : laboratoryConcept.getSetMembers()) {
            for (Concept testConcept : sampleConcept.getSetMembers()) {
                if (testConcept.getUuid().equals(test.getId())) {
                    return sampleConcept;
                }
            }
        }
        return null;
    }

    private Concept findExistingDepartmentContainingThisTest(Test test, Concept labDepartmentConcept) {
        for (Concept departmentConcept : labDepartmentConcept.getSetMembers()) {
            for (Concept testConcept : departmentConcept.getSetMembers()) {
                if (testConcept.getUuid().equals(test.getId())) {
                    return departmentConcept;
                }
            }
        }
        return null;
    }
}
