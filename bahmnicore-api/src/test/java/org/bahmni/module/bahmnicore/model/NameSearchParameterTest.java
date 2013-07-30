package org.bahmni.module.bahmnicore.model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class NameSearchParameterTest {
    @Test
    public void hasMultipleNameParts() {

        assertTrue(NameSearchParameter.create("foo bar").hasMultipleParts());
        assertTrue(NameSearchParameter.create("foo bar qux").hasMultipleParts());

        assertFalse(NameSearchParameter.create("foo").hasMultipleParts());
        assertFalse(NameSearchParameter.create(" foo").hasMultipleParts());
        assertFalse(NameSearchParameter.create("foo   ").hasMultipleParts());

        assertFalse(NameSearchParameter.create(" ").hasMultipleParts());
        assertFalse(NameSearchParameter.create(null).hasMultipleParts());
    }


    @Test
    public void shouldGetPartsWhenItHasTwoWords(){
        NameSearchParameter nameSearchParameter = NameSearchParameter.create("foo bar");

        assertThat(nameSearchParameter.getPart1(), is("foo"));
        assertThat(nameSearchParameter.getPart2(), is("bar"));
    }

    @Test
    public void shouldGetPartsWhenItHasThreeWords(){
        NameSearchParameter nameSearchParameter = NameSearchParameter.create("foo bar qux");

        assertThat(nameSearchParameter.getPart1(), is("foo bar"));
        assertThat(nameSearchParameter.getPart2(), is("qux"));
    }

    @Test
    public void shouldNotGetPartsWhenItHasOneWord(){
        NameSearchParameter nameSearchParameter = NameSearchParameter.create("foo");

        assertThat(nameSearchParameter.getPart1(), is("foo"));
        assertThat(nameSearchParameter.getPart2(), is(""));
    }

    @Test
    public void shouldNotGetPartsWhenItHasOneWordWithSpaces(){
        NameSearchParameter nameSearchParameter = NameSearchParameter.create("foo  ");

        assertThat(nameSearchParameter.getPart1(), is("foo"));
        assertThat(nameSearchParameter.getPart2(), is(""));
    }

    @Test
    public void shouldNotGetPartsWhenItIsEmpty(){
        NameSearchParameter nameSearchParameter = NameSearchParameter.create("");

        assertThat(nameSearchParameter.getPart1(), is(""));
        assertThat(nameSearchParameter.getPart2(), is(""));
    }



}
