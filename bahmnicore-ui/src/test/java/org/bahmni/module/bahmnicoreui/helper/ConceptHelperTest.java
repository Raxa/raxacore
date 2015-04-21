package org.bahmni.module.bahmnicoreui.helper;

import org.bahmni.module.bahmnimetadata.contract.ConceptDetails;
import org.bahmni.module.bahmnimetadata.helper.ConceptHelper;
import org.bahmni.test.builder.ConceptBuilder;
import org.bahmni.test.builder.ConceptNumericBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConceptHelperTest {
    @Mock
    private ConceptService conceptService;

    private ConceptHelper conceptHelper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        conceptHelper = new ConceptHelper(conceptService);
    }

    @Test
    public void shouldGetLeafConcepts() {
        List<String> obsConcepts = Arrays.asList("Vitals");
        Concept weightConcept = new ConceptBuilder().withName("Weight").withClass("N/A").build();
        Concept heightConcept = new ConceptBuilder().withName("Height").withClass("N/A").build();
        Concept vitalsConcept = new ConceptBuilder().withName("Vitals").withSetMember(heightConcept).withSetMember(weightConcept).withClass("N/A").build();
        vitalsConcept.setSet(true);
        when(conceptService.getConceptByName("Vitals")).thenReturn(vitalsConcept);

        Set<ConceptDetails> leafConceptNames = conceptHelper.getLeafConceptDetails(obsConcepts);

        assertEquals(2, leafConceptNames.size());
        Iterator<ConceptDetails> leafConceptIterator = leafConceptNames.iterator();
        assertEquals("Height", leafConceptIterator.next().getName());
        assertEquals("Weight", leafConceptIterator.next().getName());
    }

    @Test
    public void shouldGetLeafConceptsWithUnits() {
        List<String> obsConcepts = Arrays.asList("Vitals");
        Concept weightConcept = new ConceptNumericBuilder().withName("Weight").withClass("N/A").build();
        Concept heightConcept = new ConceptNumericBuilder().withName("Height").withClass("N/A").withUnit("Cms").build();
        Concept vitalsConcept = new ConceptNumericBuilder().withName("Vitals").withSetMember(heightConcept).withSetMember(weightConcept).withClass("N/A").build();
        vitalsConcept.setSet(true);
        when(conceptService.getConceptByName("Vitals")).thenReturn(vitalsConcept);

        Set<ConceptDetails> leafConceptNames = conceptHelper.getLeafConceptDetails(obsConcepts);

        assertEquals(2, leafConceptNames.size());
        Iterator<ConceptDetails> leafConceptIterator = leafConceptNames.iterator();
        ConceptDetails heightConceptResult = leafConceptIterator.next();
        assertEquals("Height", heightConceptResult.getName());
        assertEquals("Cms", heightConceptResult.getUnits());
        assertEquals("Weight", leafConceptIterator.next().getName());
    }

    @Test
    public void shouldGetLeafConceptsWithUnitsLowAbsoluteAndHighAbsolute() {
        List<String> obsConcepts = Arrays.asList("Vitals");
        Concept weightConcept = new ConceptNumericBuilder().withName("Weight").withClass("N/A").withLowNormal(50.0).withHiNormal(100.0).build();
        Concept heightConcept = new ConceptNumericBuilder().withName("Height").withClass("N/A").withUnit("Cms").withLowNormal(140.0).withHiNormal(180.0).build();
        Concept vitalsConcept = new ConceptNumericBuilder().withName("Vitals").withSetMember(heightConcept).withSetMember(weightConcept).withClass("N/A").build();
        vitalsConcept.setSet(true);
        when(conceptService.getConceptByName("Vitals")).thenReturn(vitalsConcept);

        Set<ConceptDetails> leafConceptNames = conceptHelper.getLeafConceptDetails(obsConcepts);

        assertEquals(2, leafConceptNames.size());
        Iterator<ConceptDetails> leafConceptIterator = leafConceptNames.iterator();
        ConceptDetails heightConceptResult = leafConceptIterator.next();
        assertEquals("Height", heightConceptResult.getName());
        assertEquals(new Double(140.0), heightConceptResult.getLowNormal());
        assertEquals(new Double(180.0), heightConceptResult.getHiNormal());
        assertEquals("Cms", heightConceptResult.getUnits());
        ConceptDetails weightConceptResult = leafConceptIterator.next();
        assertEquals("Weight", weightConceptResult.getName());
        assertEquals(new Double(50.0), weightConceptResult.getLowNormal());
        assertEquals(new Double(100.0), weightConceptResult.getHiNormal());
    }



    @Test
    public void shouldGetConceptDetailsFromConceptList() {
        List<String> conceptNames = Arrays.asList("Height", "Weight");
        Concept weightConcept = new ConceptNumericBuilder().withName("Weight").withClass("N/A").withLowNormal(10.3).withHiNormal(11.1).build();
        Concept heightConcept = new ConceptNumericBuilder().withName("Height").withClass("N/A").withUnit("Cms").build();

        when(conceptService.getConceptByName("Weight")).thenReturn(weightConcept);
        when(conceptService.getConceptByName("Height")).thenReturn(heightConcept);

        Set<ConceptDetails> conceptDetailsList = conceptHelper.getConceptDetails(conceptNames);


        assertEquals(2,conceptDetailsList.size());
        Iterator<ConceptDetails> iterator =  conceptDetailsList.iterator();
        ConceptDetails heightConceptDetails = iterator.next();
        assertEquals("Height", heightConceptDetails.getName());
        assertEquals("Cms", heightConceptDetails.getUnits());
        ConceptDetails weightConceptDetails = iterator.next();
        assertEquals("Weight", weightConceptDetails.getName());
        assertEquals(new Double(10.3), weightConceptDetails.getLowNormal());
        assertEquals(new Double(11.1), weightConceptDetails.getHiNormal());
    }
}