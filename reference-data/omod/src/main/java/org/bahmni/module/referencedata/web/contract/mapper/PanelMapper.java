package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.model.event.TestEvent;
import org.bahmni.module.referencedata.web.contract.Panel;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

import static org.bahmni.module.referencedata.web.contract.mapper.MapperUtils.*;

public class PanelMapper extends ResourceMapper {
    public PanelMapper() {
        super(TestEvent.TEST_PARENT_CONCEPT_NAME);
    }

    @Override
    public Panel map(Concept panelConcept) {
        Panel panel = new Panel();
        panel = mapResource(panel, panelConcept);
        panel.setDescription(getDescription(panelConcept));
        panel.setShortName(panelConcept.getShortestName(Context.getLocale(), false).getName());
        panel.setSample(getSample(panelConcept));
        panel.setTests(getTests(panelConcept));
        return panel;
    }
}
