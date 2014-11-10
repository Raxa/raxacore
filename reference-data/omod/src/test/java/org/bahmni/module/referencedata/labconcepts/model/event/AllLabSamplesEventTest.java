package org.bahmni.module.referencedata.labconcepts.model.event;

import org.bahmni.module.referencedata.labconcepts.contract.AllSamples;
import org.bahmni.module.referencedata.labconcepts.model.Operation;
import org.bahmni.test.builder.ConceptBuilder;
import org.ict4h.atomfeed.server.service.Event;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptService;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AllLabSamplesEventTest {

    private Concept parentConcept;
    private Concept concept;
    private Concept anotherConcept;


    @Before
    public void setup() {
        concept = new ConceptBuilder().withClassUUID(ConceptClass.LABSET_UUID).build();
        anotherConcept = new ConceptBuilder().withClassUUID(ConceptClass.LABSET_UUID).build();

        parentConcept = new ConceptBuilder().withName(AllSamples.ALL_SAMPLES).withSetMember(concept).withSetMember(anotherConcept).build();

    }

    @Test
    public void should_create_one_event_for_All_Lab_Samples_and_set_members() throws Exception {
        List<Event> events = new Operation(ConceptService.class.getMethod("saveConcept", Concept.class)).apply(new Object[]{parentConcept});
        assertEquals(events.size(), 1);
        Event event = events.get(0);
        assertThat(event.getUri().toString(), containsString(parentConcept.getUuid()));
        assertEquals(event.getTitle(), ConceptEventFactory.LAB_SAMPLE);
        assertEquals(event.getCategory(), "lab");

    }
}
