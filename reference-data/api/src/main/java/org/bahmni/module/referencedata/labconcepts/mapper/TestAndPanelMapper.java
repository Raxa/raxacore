package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.LabTest;
import org.bahmni.module.referencedata.labconcepts.contract.Panel;
import org.bahmni.module.referencedata.labconcepts.contract.TestsAndPanels;
import org.openmrs.Concept;

public class TestAndPanelMapper extends ResourceMapper{

    public TestAndPanelMapper() {
        super(null);
    }

    @Override
    public TestsAndPanels map(Concept sampleConcept) {
        TestsAndPanels testsAndPanels = new TestsAndPanels();
        for (Concept concept : sampleConcept.getSetMembers()) {
            if (MapperUtils.isActive(concept)) addConcept(testsAndPanels, concept);
        }
        return testsAndPanels;
    }

    private void addConcept(TestsAndPanels testsAndPanels, Concept concept) {
        if (MapperUtils.isLabTestConcept(concept)) {
            LabTest test = new LabTest();
            testsAndPanels.addTest(mapResource(test, concept));
            test.setDescription(MapperUtils.getDescriptionOrName(concept));
        } else if (MapperUtils.isPanelConcept(concept)) {
            Panel panel = new Panel();
            testsAndPanels.addPanel(mapResource(panel, concept));
            panel.setDescription(MapperUtils.getDescriptionOrName(concept));
        }
    }
}