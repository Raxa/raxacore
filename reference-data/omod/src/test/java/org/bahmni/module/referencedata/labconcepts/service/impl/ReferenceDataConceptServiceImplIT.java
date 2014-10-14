package org.bahmni.module.referencedata.labconcepts.service.impl;

import org.bahmni.module.referencedata.labconcepts.contract.ConceptReferenceTerm;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMap;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class ReferenceDataConceptServiceImplIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        executeDataSet("labDataSetup.xml");

    }

    @Test
    public void shouldSaveNewConceptSet() throws Exception {
        ConceptSet conceptSet = new ConceptSet();
        String uniqueName = "uniqueName";
        conceptSet.setUniqueName(uniqueName);
        String displayName = "displayName";
        conceptSet.setDisplayName(displayName);
        conceptSet.setClassName("Finding");
        String description = "Description";
        conceptSet.setDescription(description);
        ConceptReferenceTerm conceptReferenceTerm = new ConceptReferenceTerm();
        conceptReferenceTerm.setReferenceTermCode("New Code");
        conceptReferenceTerm.setReferenceTermRelationship("SAME-AS");
        conceptReferenceTerm.setReferenceTermSource("org.openmrs.module.emrapi");
        conceptSet.setConceptReferenceTerm(conceptReferenceTerm);

        Concept concept = referenceDataConceptService.saveConcept(conceptSet);

        assertTrue(concept.isSet());
        assertEquals(uniqueName, concept.getFullySpecifiedName(Context.getLocale()).getName());
        assertEquals(displayName, concept.getShortNames().iterator().next().getName());
        assertEquals("Finding", concept.getConceptClass().getName());
        assertEquals(description, concept.getDescription(Context.getLocale()).getDescription());
        Collection<ConceptMap> conceptMappings = concept.getConceptMappings();
        ConceptMap conceptMap = conceptMappings.iterator().next();
        assertEquals("New Code", conceptMap.getConceptReferenceTerm().getCode());
        assertEquals("org.openmrs.module.emrapi",conceptMap.getConceptReferenceTerm().getConceptSource().getName());
        assertEquals("same-as",conceptMap.getConceptMapType().toString());
        assertEquals(ConceptDatatype.N_A_UUID, concept.getDatatype().getUuid());
    }

    @Test
    public void failIfConceptClassNotFound() throws Throwable {
        ConceptSet conceptSet = new ConceptSet();
        String uniqueName = "uniqueName";
        conceptSet.setUniqueName(uniqueName);
        String displayName = "displayName";
        conceptSet.setDisplayName(displayName);
        conceptSet.setClassName("Illegal");

        exception.expect(APIException.class);
        exception.expectMessage("Concept Class Illegal not found");

        referenceDataConceptService.saveConcept(conceptSet);
    }

    @Test
    public void shouldSaveConceptSetWithChildMembers() throws Exception {
        ConceptSet conceptSet = new ConceptSet();
        String uniqueName = "uniqueName";
        conceptSet.setUniqueName(uniqueName);
        String displayName = "displayName";
        conceptSet.setDisplayName(displayName);
        conceptSet.setClassName("Finding");
        List<String> children = new ArrayList<>();
        children.add("Child1");
        children.add("Child2");
        conceptSet.setChildren(children);
        Concept concept = referenceDataConceptService.saveConcept(conceptSet);
        List<Concept> setMembers = concept.getSetMembers();
        assertEquals(2, setMembers.size());
        assertEquals("Child1", setMembers.get(0).getName(Context.getLocale()).getName());
        assertEquals("Child2", setMembers.get(1).getName(Context.getLocale()).getName());
        assertEquals(ConceptDatatype.N_A_UUID, concept.getDatatype().getUuid());
    }

    @Test
    public void throwExceptionifChildConceptDoesntExist() throws Exception {
        ConceptSet conceptSet = new ConceptSet();
        String uniqueName = "uniqueName";
        conceptSet.setUniqueName(uniqueName);
        String displayName = "displayName";
        conceptSet.setDisplayName(displayName);
        conceptSet.setClassName("Finding");
        List<String> children = new ArrayList<>();
        children.add("Child1");
        children.add("Child3");
        children.add("Child4");
        conceptSet.setChildren(children);

        exception.expect(APIException.class);
        exception.expectMessage("Child3 Concept/ConceptAnswer not found\nChild4 Concept/ConceptAnswer not found");

        referenceDataConceptService.saveConcept(conceptSet);
    }


    @Test
    public void updateExistingConceptSet() throws Exception {
        ConceptSet conceptSet = new ConceptSet();
        String uniqueName = "Existing Concept";
        conceptSet.setUniqueName(uniqueName);
        String displayName = "NewSName";
        conceptSet.setDisplayName(displayName);
        conceptSet.setClassName("Finding");
        List<String> children = new ArrayList<>();
        String description = "Description";
        conceptSet.setDescription(description);

        children.add("Child1");
        children.add("Child2");
        conceptSet.setChildren(children);
        Concept concept = referenceDataConceptService.saveConcept(conceptSet);

        assertTrue(concept.isSet());
        assertEquals(uniqueName, concept.getName(Context.getLocale()).getName());
        assertEquals(displayName, concept.getShortestName(Context.getLocale(), false).getName());
        assertEquals("Finding", concept.getConceptClass().getName());
        assertEquals(2, concept.getSetMembers().size());
        assertEquals("5d2d4cb7-mm3b-0037-70f7-0dmimmm22222", concept.getUuid());
        assertEquals(description, concept.getDescription(Context.getLocale()).getDescription());
        assertEquals(ConceptDatatype.N_A_UUID, concept.getDatatype().getUuid());
    }



    @Test
    public void updateExistingConceptSetWithUUID() throws Exception {
        ConceptSet conceptSet = new ConceptSet();
        conceptSet.setUuid("5d2d4cb7-mm3b-0037-70f7-0dmimmm22222");
        String uniqueName = "Existing Concept New";
        conceptSet.setUniqueName(uniqueName);
        String displayName = "NewSName";
        conceptSet.setDisplayName(displayName);
        conceptSet.setClassName("Finding");
        List<String> children = new ArrayList<>();
        String description = "Description";
        conceptSet.setDescription(description);

        children.add("Child1");
        children.add("Child2");
        conceptSet.setChildren(children);
        Concept concept = referenceDataConceptService.saveConcept(conceptSet);

        assertTrue(concept.isSet());
        assertEquals(uniqueName, concept.getName(Context.getLocale()).getName());
        assertEquals(displayName, concept.getShortestName(Context.getLocale(), false).getName());
        assertEquals("Finding", concept.getConceptClass().getName());
        assertEquals(2, concept.getSetMembers().size());
        assertEquals("5d2d4cb7-mm3b-0037-70f7-0dmimmm22222", concept.getUuid());
        assertEquals(description, concept.getDescription(Context.getLocale()).getDescription());
        assertEquals(ConceptDatatype.N_A_UUID, concept.getDatatype().getUuid());
    }
}