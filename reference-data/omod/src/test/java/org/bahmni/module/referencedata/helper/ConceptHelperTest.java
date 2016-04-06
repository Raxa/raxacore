package org.bahmni.module.referencedata.helper;


import org.bahmni.module.referencedata.contract.ConceptDetails;
import org.bahmni.test.builder.ConceptBuilder;
import org.bahmni.test.builder.ConceptNumericBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConceptHelperTest {
    @Mock
    private ConceptService conceptService;

    private ConceptHelper conceptHelper;

    private boolean withoutAttributes = false;


    @Before
    public void setUp() throws Exception {
        initMocks(this);
        conceptHelper = new ConceptHelper(conceptService);
    }

    @Test
    public void shouldGetLeafConcepts() {
        Concept weightConcept = new ConceptBuilder().withName("Weight").withClass("N/A").build();
        Concept heightConcept = new ConceptBuilder().withName("Height").withClass("N/A").build();
        Concept vitalsConcept = new ConceptBuilder().withName("Vitals").withSetMember(heightConcept).withSetMember(weightConcept).withClass("N/A").build();
        vitalsConcept.setSet(true);

        Set<ConceptDetails> leafConceptNames = conceptHelper.getLeafConceptDetails(Arrays.asList(vitalsConcept), withoutAttributes);

        assertEquals(2, leafConceptNames.size());
        Iterator<ConceptDetails> leafConceptIterator = leafConceptNames.iterator();
        assertEquals("Height", leafConceptIterator.next().getName());
        assertEquals("Weight", leafConceptIterator.next().getName());
    }

    @Test
    public void shouldGetLeafConceptsWithUnits() {
        Concept weightConcept = new ConceptNumericBuilder().withName("Weight").withClass("N/A").build();
        Concept heightConcept = new ConceptNumericBuilder().withName("Height").withClass("N/A").withUnit("Cms").build();
        Concept vitalsConcept = new ConceptNumericBuilder().withName("Vitals").withSetMember(heightConcept).withSetMember(weightConcept).withClass("N/A").build();
        vitalsConcept.setSet(true);

        Set<ConceptDetails> leafConceptNames = conceptHelper.getLeafConceptDetails(Arrays.asList(vitalsConcept), withoutAttributes);

        assertEquals(2, leafConceptNames.size());
        Iterator<ConceptDetails> leafConceptIterator = leafConceptNames.iterator();
        ConceptDetails heightConceptResult = leafConceptIterator.next();
        assertEquals("Height", heightConceptResult.getName());
        assertEquals("Cms", heightConceptResult.getUnits());
        assertEquals("Weight", leafConceptIterator.next().getName());
    }

    @Test
    public void shouldGetLeafConceptsWithAttributesForConceptSetWithConceptDetailsClass() {
        Concept weightConcept = new ConceptNumericBuilder().withName("Weight").withClass("N/A").build();
        Concept heightConcept = new ConceptNumericBuilder().withName("Height").withClass("N/A").withUnit("Cms").build();
        Concept vitalsConcept = new ConceptNumericBuilder().withName("Vitals").withSetMember(heightConcept).withSetMember(weightConcept).withClass("Concept Details").build();
        vitalsConcept.setSet(true);

        Set<ConceptDetails> leafConceptNames = conceptHelper.getLeafConceptDetails(Arrays.asList(vitalsConcept), withoutAttributes);

        assertEquals(1, leafConceptNames.size());
        Iterator<ConceptDetails> leafConceptIterator = leafConceptNames.iterator();
        ConceptDetails vitalsConceptResult = leafConceptIterator.next();
        assertEquals("Vitals", vitalsConceptResult.getName());
    }

    @Test
    public void shouldGetLeafConceptsWithExtraAttributesForConceptDetailsClassWhenWithAttributesIsTrue() {
        Concept temperatureConcept = new ConceptNumericBuilder().withName("Temperature").withClass("N/A").build();
        Concept temperatureUnknownConcept = new ConceptNumericBuilder().withName("Temperature Unknown").withClass("Unknown").build();
        Concept temperatureAbnormalConcept = new ConceptNumericBuilder().withName("Temperature Abnormal").withClass("Abnormal").build();
        Concept temperatureDataConcept = new ConceptNumericBuilder()
                .withName("Temperature Data")
                .withSetMember(temperatureConcept)
                .withSetMember(temperatureUnknownConcept)
                .withSetMember(temperatureAbnormalConcept)
                .withClass("Concept Details").build();
        temperatureDataConcept.setSet(true);

        Set<ConceptDetails> leafConceptNames = conceptHelper.getLeafConceptDetails(Arrays.asList(temperatureDataConcept), true);

        assertEquals(1, leafConceptNames.size());
        Iterator<ConceptDetails> leafConceptIterator = leafConceptNames.iterator();
        ConceptDetails temperatureConceptResult = leafConceptIterator.next();
        assertEquals("Temperature", temperatureConceptResult.getName());
        assertFalse(temperatureConceptResult.getAttributes().isEmpty());
        assertEquals("Temperature Unknown", temperatureConceptResult.getAttribute("Unknown Concept"));
    }

    @Test
    public void shouldGetLeafConceptsWithExtraAttributesForConceptDetailsClassWhenWithAttributesIsFalse() {
        Concept temperatureConcept = new ConceptNumericBuilder().withName("Temperature").withClass("N/A").build();
        Concept temperatureUnknownConcept = new ConceptNumericBuilder().withName("Temperature Unknown").withClass("Unknown").build();
        Concept temperatureAbnormalConcept = new ConceptNumericBuilder().withName("Temperature Abnormal").withClass("Abnormal").build();
        Concept temperatureDataConcept = new ConceptNumericBuilder()
                .withName("Temperature Data")
                .withSetMember(temperatureConcept)
                .withSetMember(temperatureUnknownConcept)
                .withSetMember(temperatureAbnormalConcept)
                .withClass("Concept Details").build();
        temperatureDataConcept.setSet(true);

        Set<ConceptDetails> leafConceptNames = conceptHelper.getLeafConceptDetails(Arrays.asList(temperatureDataConcept), false);

        assertEquals(1, leafConceptNames.size());
        Iterator<ConceptDetails> leafConceptIterator = leafConceptNames.iterator();
        ConceptDetails temperatureConceptResult = leafConceptIterator.next();
        assertEquals("Temperature Data", temperatureConceptResult.getName());
        assertFalse(temperatureConceptResult.getAttributes().isEmpty());
        assertEquals("Temperature Unknown", temperatureConceptResult.getAttribute("Unknown Concept"));
    }

    @Test
    public void shouldGetLeafConceptsWithUnitsLowAbsoluteAndHighAbsolute() {
        Concept weightConcept = new ConceptNumericBuilder().withName("Weight").withClass("N/A").withLowNormal(50.0).withHiNormal(100.0).build();
        Concept heightConcept = new ConceptNumericBuilder().withName("Height").withClass("N/A").withUnit("Cms").withLowNormal(140.0).withHiNormal(180.0).build();
        Concept vitalsConcept = new ConceptNumericBuilder().withName("Vitals").withSetMember(heightConcept).withSetMember(weightConcept).withClass("N/A").build();
        vitalsConcept.setSet(true);

        Set<ConceptDetails> leafConceptNames = conceptHelper.getLeafConceptDetails(Arrays.asList(vitalsConcept), withoutAttributes);

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
        Concept weightConcept = new ConceptNumericBuilder().withName("Weight").withClass("N/A").withLowNormal(10.3).withHiNormal(11.1).build();
        Concept heightConcept = new ConceptNumericBuilder().withName("Height").withClass("N/A").withUnit("Cms").build();

        Set<ConceptDetails> conceptDetailsList = conceptHelper.getConceptDetails(Arrays.asList(heightConcept, weightConcept));


        assertEquals(2, conceptDetailsList.size());
        Iterator<ConceptDetails> iterator = conceptDetailsList.iterator();
        ConceptDetails heightConceptDetails = iterator.next();
        assertEquals("Height", heightConceptDetails.getName());
        assertEquals("Cms", heightConceptDetails.getUnits());
        ConceptDetails weightConceptDetails = iterator.next();
        assertEquals("Weight", weightConceptDetails.getName());
        assertEquals(new Double(10.3), weightConceptDetails.getLowNormal());
        assertEquals(new Double(11.1), weightConceptDetails.getHiNormal());
    }
}