package org.bahmni.module.referencedata.labconcepts.service.impl;

import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptReferenceTermService;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class ReferenceDataConceptReferenceTermServiceImplIT extends BaseModuleWebContextSensitiveTest {
    @Before
    public void setUp() throws Exception {
        executeDataSet("referenceTermSetup.xml");
    }

    @Autowired
    private ReferenceDataConceptReferenceTermService referenceDataConceptReferenceTermService;

    @Test
    public void should_get_concept_mapping() throws Exception {
        ConceptReferenceTerm referenceTerm = referenceDataConceptReferenceTermService.getConceptReferenceTerm("New Code", "org.openmrs.module.emrapi");
        assertNotNull(referenceTerm);
        assertEquals("New Code", referenceTerm.getCode());
        assertEquals("org.openmrs.module.emrapi", referenceTerm.getConceptSource().getName());
    }
}