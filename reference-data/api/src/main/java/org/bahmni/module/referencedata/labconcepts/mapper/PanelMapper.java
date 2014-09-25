package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.Panel;
import org.bahmni.module.referencedata.labconcepts.contract.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

public class PanelMapper extends ResourceMapper {
    public PanelMapper() {
        super(Test.TEST_PARENT_CONCEPT_NAME);
    }

    @Override
    public Panel map(Concept panelConcept) {
        Panel panel = new Panel();
        panel = mapResource(panel, panelConcept);
        panel.setDescription(MapperUtils.getDescription(panelConcept));
        panel.setShortName(panelConcept.getShortestName(Context.getLocale(), false).getName());
        panel.setSample(MapperUtils.getSample(panelConcept));
        panel.setTests(MapperUtils.getTests(panelConcept));
        return panel;
    }
}
