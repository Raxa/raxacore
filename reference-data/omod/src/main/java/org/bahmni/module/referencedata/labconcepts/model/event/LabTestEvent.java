package org.bahmni.module.referencedata.labconcepts.model.event;

import org.bahmni.module.referencedata.helper.ConceptHelper;
import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.isLabTestConcept;

public class LabTestEvent extends ConceptOperationEvent {

    public LabTestEvent(String url, String category, String title) {
        super(url, category, title);
    }

    public boolean isResourceConcept(Concept concept) {
        return isLabTestConcept(concept) || (getParentOfTypeLabTest(concept) != null);
    }

    private Concept getParentOfTypeLabTest(Concept concept) {
        ConceptHelper conceptHelper = new ConceptHelper(Context.getConceptService());
        List<Concept> parentConcepts = conceptHelper.getParentConcepts(concept);
        for (Concept parentConcept : parentConcepts) {
            if (isLabTestConcept(parentConcept)){
                return parentConcept;
            };
        }
        return null;
    }

    @Override
    public Event asAtomFeedEvent(Object[] arguments) throws URISyntaxException {
        Concept concept = (Concept) arguments[0];
        if (!isLabTestConcept(concept)) {
            concept = getParentOfTypeLabTest(concept);
        }
        String url = String.format(this.url, title, concept.getUuid());
        return new Event(UUID.randomUUID().toString(), title, DateTime.now(), url, url, category);
    }

}
