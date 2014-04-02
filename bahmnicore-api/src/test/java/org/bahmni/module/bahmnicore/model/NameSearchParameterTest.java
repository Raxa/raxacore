package org.bahmni.module.bahmnicore.model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class NameSearchParameterTest {
    @Test
    public void shouldReturnTrueWhenNoNameSearchParametersAreProvided() throws Exception {
        NameSearchParameter nameSearchParameter = NameSearchParameter.create("");
        assertTrue(nameSearchParameter.isEmpty());
    }

    @Test
    public void shouldReturnTrueWhenSearchParametersAreNull() throws Exception {
        NameSearchParameter nameSearchParameter = NameSearchParameter.create(null);
        assertTrue(nameSearchParameter.isEmpty());

    }

    @Test
    public void shouldReturnNameSearchParametersSplitBySpace() throws Exception {
        String searchParameter = "FirstName MiddleName LastName";
        String[] splitSearchParameters = searchParameter.split(" ");
        NameSearchParameter nameSearchParameter = NameSearchParameter.create(searchParameter);
        assertFalse(nameSearchParameter.isEmpty());
        assertEquals(3, nameSearchParameter.getNameParts().length);
        for(int i=0;i<splitSearchParameters.length;i++){
            nameSearchParameter.getNameParts()[i].contains(splitSearchParameters[i]);
        }
    }
}
