package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.LabTest;
import org.bahmni.module.referencedata.labconcepts.contract.Panel;
import org.bahmni.module.referencedata.labconcepts.contract.TestsAndPanels;
import org.openmrs.Concept;

public class TestAndPanelMapper extends ResourceMapper {

    private final LabTestMapper labTestMapper;
    private PanelMapper panelMapper;

    public TestAndPanelMapper() {
        super(null);
        labTestMapper = new LabTestMapper();
        panelMapper = new PanelMapper();
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
            LabTest test = labTestMapper.map(concept);
            testsAndPanels.addTest(test);
        } else if (MapperUtils.isPanelConcept(concept)) {
            Panel panel = panelMapper.map(concept);
            testsAndPanels.addPanel(panel);
        }
    }
}