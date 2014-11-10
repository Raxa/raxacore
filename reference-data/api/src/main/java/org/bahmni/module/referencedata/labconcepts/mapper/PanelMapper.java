package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.AllTestsAndPanels;
import org.bahmni.module.referencedata.labconcepts.contract.Panel;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

public class PanelMapper extends ResourceMapper {
    public PanelMapper() {
        super(AllTestsAndPanels.ALL_TESTS_AND_PANELS);
    }

    @Override
    public Panel map(Concept panelConcept) {
        String description;
        Panel panel = new Panel();
        panel = mapResource(panel, panelConcept);
        panel.setSampleUuid(MapperUtils.getSampleUuid(panelConcept));
        panel.setTests(MapperUtils.getTests(panelConcept));
        panel.setSortOrder(getSortWeight(panelConcept));
        panel.setDescription(MapperUtils.getDescriptionOrName(panelConcept));
        return panel;
    }
}
