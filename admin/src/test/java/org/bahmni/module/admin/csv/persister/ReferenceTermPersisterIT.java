package org.bahmni.module.admin.csv.persister;

import org.bahmni.csv.Messages;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.BaseIntegrationTest;
import org.bahmni.module.admin.csv.models.PatientRow;
import org.bahmni.module.admin.csv.models.ReferenceTermRow;
import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ReferenceTermPersisterIT extends BaseIntegrationTest {

    private String path;

    @Autowired
    private ReferenceTermPersister referenceTermPersister;

    @Autowired
    private ConceptService conceptService;

    @Before
    public void setUp() throws Exception {
        path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);

        Context.authenticate("admin", "test");
        UserContext userContext = Context.getUserContext();
        referenceTermPersister.init(userContext);
    }

    @Test
    public void save_new_referenceTerm() {
        ReferenceTermRow referenceTermRow = new ReferenceTermRow("TB1002", "SNOMED CT", "Tuberclosis", null, null);
        Messages errorMessages = referenceTermPersister.persist(referenceTermRow);
        assertTrue("should have persisted the reference term row", errorMessages.isEmpty());

        Context.openSession();
        Context.authenticate("admin", "test");
        ConceptReferenceTerm conceptReferenceTerm = conceptService.getConceptReferenceTermByCode(referenceTermRow.getCode(), conceptService.getConceptSourceByName(referenceTermRow.getSource()));
        assertEquals(referenceTermRow.getCode(), conceptReferenceTerm.getCode());

        Context.flushSession();
        Context.closeSession();
    }

    @Test
    public void update_exisiting_referenceTerm() {

        ReferenceTermRow referenceTermRow = new ReferenceTermRow("TB100", "SNOMED CT", "Tuberclosis", null, null);
        referenceTermPersister.persist(referenceTermRow);

        Context.openSession();
        Context.authenticate("admin", "test");
        ConceptSource conceptSource = conceptService.getConceptSourceByName(referenceTermRow.getSource());
        ConceptReferenceTerm existingConceptReferenceTerm = conceptService.getConceptReferenceTermByCode(referenceTermRow.getCode(), conceptSource);
        assertEquals("TB100", existingConceptReferenceTerm.getCode());
        assertEquals("Tuberclosis", existingConceptReferenceTerm.getName());
        Context.flushSession();
        Context.closeSession();


        ReferenceTermRow updatedReferenceTermRow = new ReferenceTermRow("TB100", "SNOMED CT", "TuberclosisEdited", "Description", "1.1");
        referenceTermPersister.persist(updatedReferenceTermRow);

        Context.openSession();
        Context.authenticate("admin", "test");
        ConceptReferenceTerm updatedConceptReferenceTerm = conceptService.getConceptReferenceTermByCode(updatedReferenceTermRow.getCode(), conceptSource);
        assertEquals("TB100", existingConceptReferenceTerm.getCode());
        assertEquals("TuberclosisEdited", updatedConceptReferenceTerm.getName());
        assertEquals("Description", updatedConceptReferenceTerm.getDescription());
        assertEquals("1.1", updatedConceptReferenceTerm.getVersion());
        Context.flushSession();
        Context.closeSession();
    }

    @Test
    public void fails_save_when_invalid_conceptsource() {
        ReferenceTermRow referenceTermRow = new ReferenceTermRow("TB100", "ICG 11", "Tuberclosis", null, null);
        Messages errorMessages = referenceTermPersister.persist(referenceTermRow);

        assertFalse("should have persisted the reference term row", errorMessages.isEmpty());
        assertTrue(errorMessages.toString().contains("Concept reference source ICG 11 does not exists."));
    }
}