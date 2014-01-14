package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.BahmniCoreApiProperties;
import org.bahmni.module.bahmnicore.contract.patient.response.PatientConfigResponse;
import org.bahmni.module.bahmnicore.dao.BahmniPatientDao;
import org.bahmni.module.bahmnicore.datamigration.ExecutionMode;
import org.bahmni.module.bahmnicore.mapper.PatientMapper;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.bahmni.module.bahmnicore.util.PatientMother;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BahmniPatientServiceImplTest {

    @Mock
    private PatientService patientService;
    @Mock
    private PatientImageService patientImageService;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private BahmniCoreApiProperties bahmniCoreApiProperties;
    @Mock
    private PersonService personService;
    @Mock
    private ConceptService conceptService;
    @Mock
    private BahmniPatientDao bahmniPatientDao;

    private BahmniPatientServiceImpl bahmniPatientService;

    @Before
    public void setup() {
        initMocks(this);
        when(bahmniCoreApiProperties.getExecutionMode()).thenReturn(new ExecutionMode("false"));
        bahmniPatientService = new BahmniPatientServiceImpl(patientImageService, patientService, personService, conceptService, bahmniCoreApiProperties, patientMapper, bahmniPatientDao);
    }

    @Test
    public void shouldMapPostValuesToNewPatientOnCreate() throws Exception {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(patientMother.build());
        when(patientService.savePatient(any(Patient.class))).thenReturn(patientMother.build());

        bahmniPatientService.createPatient(patientMother.buildBahmniPatient());

        verify(patientMapper).map(Matchers.<Patient>eq(null), any(BahmniPatient.class));
    }

    @Test
    public void shouldMapPostValuesToExistingPatientOnUpdate() throws Exception {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(patientMother.build());
        when(patientService.savePatient(any(Patient.class))).thenReturn(patientMother.build());
        String uuid = "a23034-asdf954-asdfasdf-342343";
        Patient patient = patientMother.build();
        when(patientService.getPatientByUuid(uuid)).thenReturn(patient);
        BahmniPatient bahmniPatient = patientMother.buildBahmniPatient();
        bahmniPatient.setUuid(uuid);

        bahmniPatientService.updatePatient(bahmniPatient);

        verify(patientMapper).map(eq(patient), any(BahmniPatient.class));
    }

    @Test
    public void shouldSaveMappedPatientOnCreate() throws Exception {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        Patient patient = patientMother.build();
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(patient);
        when(patientService.savePatient(eq(patient))).thenReturn(patientMother.build());

        bahmniPatientService.createPatient(patientMother.buildBahmniPatient());

        verify(patientService).savePatient(patient);
    }

    @Test
    public void shouldSaveMappedPatientOnUpdate() throws Exception {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        String uuid = "a23034-asdf954-asdfasdf-342343";
        Patient patient = patientMother.build();
        when(patientService.getPatientByUuid(uuid)).thenReturn(patient);
        Patient mappedPatient = patientMother.build();
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(mappedPatient);
        when(patientService.savePatient(eq(mappedPatient))).thenReturn(patientMother.build());
        BahmniPatient bahmniPatient = patientMother.buildBahmniPatient();
        bahmniPatient.setUuid(uuid);

        bahmniPatientService.updatePatient(bahmniPatient);

        verify(patientService).savePatient(mappedPatient);
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldRethrowTheApiAutheticationException() throws Exception {
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(new PatientMother().build());
        when(patientService.savePatient(any(Patient.class))).thenThrow(new APIAuthenticationException());


        bahmniPatientService.createPatient(new PatientMother().buildBahmniPatient());
    }

    @Test
    public void shouldGetPatientConfig() throws Exception {
        List<PersonAttributeType> personAttributeTypes = new ArrayList<>();
        personAttributeTypes.add(new PersonAttributeType() {{
            this.setName("class");
            this.setDescription("Class");
            this.setFormat("org.openmrs.Concept");
            this.setSortWeight(10.0);
            this.setForeignKey(10);
        }});
        personAttributeTypes.add(new PersonAttributeType() {{
            this.setName("primaryContact");
            this.setDescription("Primary Contact");
            this.setFormat("java.lang.String");
            this.setSortWeight(10.0);
        }});

        when(personService.getAllPersonAttributeTypes()).thenReturn(personAttributeTypes);
        when(conceptService.getConcept(anyInt())).thenReturn(new Concept());

        PatientConfigResponse config = bahmniPatientService.getConfig();
        assertEquals(2, config.getPersonAttributeTypes().size());
        assertEquals("class", config.getPersonAttributeTypes().get(0).getName());
        assertEquals("primaryContact", config.getPersonAttributeTypes().get(1).getName());
    }

}
