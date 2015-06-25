package org.bahmni.module.bahmnicore.util;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

public class MiscUtilsTest {

    @Test
    public void shouldReturnConceptsWhenTheyAreAvailable() {
        ConceptService conceptService = Mockito.mock(ConceptService.class);
        String nonExistantConceptName = "doesNotExist";
        String sampleConceptName = "sampleConcept";
        when(conceptService.getConceptByName(nonExistantConceptName)).thenReturn(null);
        Concept sampleConcept = new Concept();
        when(conceptService.getConceptByName(sampleConceptName)).thenReturn(sampleConcept);
        Collection<Concept> concepts = MiscUtils.getConceptsForNames(Arrays.asList(sampleConceptName, nonExistantConceptName), conceptService);
        Assert.assertThat(concepts.size(), is(equalTo(1)));
        Assert.assertThat(concepts.iterator().next(), is(sampleConcept));
    }

}