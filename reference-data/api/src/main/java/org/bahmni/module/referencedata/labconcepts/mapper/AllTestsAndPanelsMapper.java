package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.AllTestsAndPanels;
import org.openmrs.Concept;

public class AllTestsAndPanelsMapper extends ResourceMapper {
    public AllTestsAndPanelsMapper() {
        super(null);
    }

    @Override
    public AllTestsAndPanels map(Concept testsAndPanelsConcept) {
        AllTestsAndPanels allTestsAndPanels = new AllTestsAndPanels();
        allTestsAndPanels = mapResource(allTestsAndPanels, testsAndPanelsConcept);
        allTestsAndPanels.setDescription(MapperUtils.getDescription(testsAndPanelsConcept));

        setTestsAndPanels(allTestsAndPanels, testsAndPanelsConcept);
        return allTestsAndPanels;
    }

    private void setTestsAndPanels(AllTestsAndPanels allTestsAndPanels, Concept testsAndPanelsConcept) {
        LabTestMapper testMapper = new LabTestMapper();
        PanelMapper panelMapper = new PanelMapper();
        for (Concept setMember : testsAndPanelsConcept.getSetMembers()) {
            if(MapperUtils.isTestConcept(setMember)){
                allTestsAndPanels.addTest(testMapper.map(setMember));
            }
            else if(MapperUtils.isPanelConcept(setMember)){
                allTestsAndPanels.addPanel(panelMapper.map(setMember));
            }

        }
    }
}
