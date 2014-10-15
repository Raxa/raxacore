package org.bahmni.module.referencedata.labconcepts.model.event;

public class ConceptEventFactory {
    static final String CONCEPT_URL = "/openmrs/ws/rest/v1/reference-data/%s/%s";
    public static final String LAB = "lab";
    public static final String LAB_SAMPLE = "all-samples";
    public static final String SAMPLE = "sample";
    public static final String DEPARTMENT = "department";
    public static final String TEST = "test";
    public static final String PANEL = "panel";
    public static final String TESTS_AND_PANEL = "all-tests-and-panels";

    public static ConceptOperationEvent sampleEvent() {
        return new SampleEvent(CONCEPT_URL, LAB, SAMPLE);
    }

    public static ConceptOperationEvent labConceptSetEvent() { return new AllLabSamplesEvent(CONCEPT_URL, LAB, LAB_SAMPLE); }

    public static ConceptOperationEvent allTestsAndPanelsConceptSetEvent() { return new AllTestsPanelsConceptSetEvent(CONCEPT_URL, LAB, TESTS_AND_PANEL); }

    public static ConceptOperationEvent departmentEvent() {
        return new DepartmentEvent(CONCEPT_URL, LAB, DEPARTMENT);
    }

    public static ConceptOperationEvent panelEvent() {
        return new PanelEvent(CONCEPT_URL, LAB, PANEL);
    }

    public static ConceptOperationEvent testEvent() {
        return new LabTestEvent(CONCEPT_URL, LAB, TEST);
    }
}
