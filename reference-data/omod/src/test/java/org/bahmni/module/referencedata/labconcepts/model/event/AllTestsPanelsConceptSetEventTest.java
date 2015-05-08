package org.bahmni.module.referencedata.labconcepts.model.event;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.bahmni.module.referencedata.labconcepts.contract.AllTestsAndPanels;
import org.bahmni.module.referencedata.labconcepts.contract.LabTest;
import org.bahmni.module.referencedata.labconcepts.model.Operation;
import org.bahmni.test.builder.ConceptBuilder;
import org.ict4h.atomfeed.server.service.Event;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptService;

public class AllTestsPanelsConceptSetEventTest {
    private Concept parentConcept;
    private Concept testConcept;
    private Concept panelConcept;
    
    @Before
    public void setup() {
        testConcept = new ConceptBuilder().withClass(LabTest.LAB_TEST_CONCEPT_CLASS).build();
        panelConcept = new ConceptBuilder().withClassUUID(ConceptClass.LABSET_UUID).build();

        parentConcept = new ConceptBuilder().withName(AllTestsAndPanels.ALL_TESTS_AND_PANELS).withSetMember(testConcept).withSetMember(panelConcept).build();

    }

    @Test
    public void should_create_one_event_for_All_Tests_And_Panels_and_set_members() throws Exception {
        /*List<Event> events = new Operation(ConceptService.class.getMethod("saveConcept", Concept.class)).apply(new Object[]{parentConcept});
        assertEquals(events.size(),1);
        Event event = events.get(0);
        assertThat(event.getUri().toString(), containsString(parentConcept.getUuid()));
        assertEquals(ConceptServiceEventFactory.TESTS_AND_PANEL, event.getTitle());
        assertEquals("lab",event.getCategory());*/
    }
}