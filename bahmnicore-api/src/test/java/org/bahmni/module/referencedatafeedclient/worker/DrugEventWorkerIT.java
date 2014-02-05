package org.bahmni.module.referencedatafeedclient.worker;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.domain.Drug;
import org.bahmni.module.referencedatafeedclient.domain.DrugForm;
import org.bahmni.module.referencedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openmrs.api.ConceptService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"})
public class DrugEventWorkerIT extends BaseModuleWebContextSensitiveTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Mock
    private HttpClient httpClient;
    @Mock
    private ReferenceDataFeedProperties referenceDataFeedProperties;
    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;

    @Autowired
    private ConceptService conceptService;
    private final String referenceDataUri = "http://localhost";
    private DrugEventWorker drugEventWorker;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(referenceDataFeedProperties.getReferenceDataUri()).thenReturn(referenceDataUri);
        drugEventWorker = new DrugEventWorker(httpClient, referenceDataFeedProperties,referenceDataConceptService);
        executeDataSet("drugEventWorkerTestData.xml");
    }

    @Test
    public void shouldCreateDrugConceptForDrug() throws IOException {
        Event event = new Event("xxxx-yyyyy", "/reference-data/drug/860e5278-b9f3-49cb-8830-952d89ec9871");
        Drug drug = new Drug("860e5278-b9f3-49cb-8830-952d89ec9871", "calpol", "Paracetamol",
                new DrugForm("a85c5035-8d85-11e3-9b86-0800271c1b75", "tablet"), "500", "mg", "oral");
        when(httpClient.get(referenceDataUri + event.getContent(), Drug.class)).thenReturn(drug);

        drugEventWorker.process(event);

        org.openmrs.Drug savedDrug = conceptService.getDrugByUuid(drug.getId());
        assertEquals(drug.getName(), savedDrug.getName());
        assertEquals(drug.getGenericName(), savedDrug.getConcept().getName().getName());
        assertEquals(drug.getForm().getName(), savedDrug.getDosageForm().getName().getName());
        assertEquals(drug.getRoute(), savedDrug.getRoute().getName().getName());
        assertEquals(Double.parseDouble(drug.getStrength()), savedDrug.getDoseStrength());
        assertEquals(drug.getStrengthUnits(), savedDrug.getUnits());
    }

    @Test
    public void shouldCreateConceptsForGenericNameIfNotExists() throws IOException {
        Event event = new Event("xxxx-yyyyy", "/reference-data/drug/0ab1310c-8e27-11e3-9b86-0800271c1b75");
        Drug drug = new Drug("0ab1310c-8e27-11e3-9b86-0800271c1b75", "Amox", "Amoxycilin",
                new DrugForm("a85c5035-8d85-11e3-9b86-0800271c1b75", "tablet"), "500", "mg", "IV");
        when(httpClient.get(referenceDataUri + event.getContent(), Drug.class)).thenReturn(drug);

        drugEventWorker.process(event);

        org.openmrs.Drug savedDrug = conceptService.getDrugByUuid(drug.getId());
        assertEquals(drug.getName(), savedDrug.getName());
        assertNotNull(savedDrug.getConcept().getUuid());
        assertEquals(drug.getGenericName(), savedDrug.getConcept().getName().getName());
        assertNotNull(savedDrug.getRoute().getUuid());
        assertEquals(drug.getRoute(), savedDrug.getRoute().getName().getName());
        assertEquals(drug.getForm().getName(), savedDrug.getDosageForm().getName().getName());
        assertEquals(Double.parseDouble(drug.getStrength()), savedDrug.getDoseStrength());
        assertEquals(drug.getStrengthUnits(), savedDrug.getUnits());
    }

    @Test
    public void shouldFailFeedIfDrugFormDoesNotExist() throws IOException {
        Event event = new Event("xxxx-yyyyy", "/reference-data/drug/0ab1310c-8e27-11e3-9b86-0800271c1b75");
        Drug drug = new Drug("0ab1310c-8e27-11e3-9b86-0800271c1b75", "Amox", "Amoxycilin",
                new DrugForm("a85c5035-8d85-11e3-9b86-0800271c1b7a", "syrup"), "500", "mg", "IV");
        exception.expect(Exception.class);
        exception.expectMessage(String.format("Could not find dosage form for %s", drug.getForm().getName()));
        when(httpClient.get(referenceDataUri + event.getContent(), Drug.class)).thenReturn(drug);

        drugEventWorker.process(event);
    }
}
