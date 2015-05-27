package org.bahmni.module.bahmnicore.model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class WildCardParameterTest {
    @Test
    public void shouldReturnTrueWhenNoNameSearchParametersAreProvided() throws Exception {
        WildCardParameter wildCardParameter = WildCardParameter.create("");
        assertTrue(wildCardParameter.isEmpty());
    }

    @Test
    public void shouldReturnTrueWhenSearchParametersAreNull() throws Exception {
        WildCardParameter wildCardParameter = WildCardParameter.create(null);
        assertTrue(wildCardParameter.isEmpty());

    }

    @Test
    public void shouldReturnNameSearchParametersSplitBySpace() throws Exception {
        String searchParameter = "FirstName MiddleName LastName";
        String[] splitSearchParameters = searchParameter.split(" ");
        WildCardParameter wildCardParameter = WildCardParameter.create(searchParameter);
        assertFalse(wildCardParameter.isEmpty());
        assertEquals(3, wildCardParameter.getParts().length);
        for(int i=0;i<splitSearchParameters.length;i++){
            wildCardParameter.getParts()[i].contains(splitSearchParameters[i]);
        }
    }
}
