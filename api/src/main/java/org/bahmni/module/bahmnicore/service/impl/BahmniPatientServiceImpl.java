package org.bahmni.module.bahmnicore.service.impl;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.BahmniCoreApiProperties;
import org.bahmni.module.bahmnicore.contract.patient.response.PatientConfigResponse;
import org.bahmni.module.bahmnicore.datamigration.ExecutionMode;
import org.bahmni.module.bahmnicore.mapper.PatientMapper;
import org.bahmni.module.bahmnicore.model.BahmniAddress;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.bahmni.module.billing.BillingService;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Lazy //to get rid of cyclic dependencies
public class BahmniPatientServiceImpl implements BahmniPatientService {
    private PatientService patientService;
    private BillingService billingService;
    private PatientImageService patientImageService;
    private BahmniCoreApiProperties properties;
    private PatientMapper patientMapper;
    private static Logger logger = Logger.getLogger(BahmniPatientServiceImpl.class);
    private PersonService personService;
    private ConceptService conceptService;

    @Autowired
    public BahmniPatientServiceImpl(BillingService billingService, PatientImageService patientImageService,
                                    PatientService patientService, PersonService personService, ConceptService conceptService,
                                    BahmniCoreApiProperties properties, PatientMapper patientMapper) {
        this.billingService = billingService;
        this.patientImageService = patientImageService;
        this.patientService = patientService;
        this.properties = properties;
        this.personService = personService;
        this.conceptService = conceptService;
        this.patientMapper = patientMapper;
    }

    @Override
    public PatientConfigResponse getConfig() {
        List<PersonAttributeType> personAttributeTypes = personService.getAllPersonAttributeTypes();

        PatientConfigResponse patientConfigResponse = new PatientConfigResponse();
        for (PersonAttributeType personAttributeType : personAttributeTypes) {
            Concept attributeConcept = null;
            if (personAttributeType.getFormat().equals("org.openmrs.Concept")) {
                attributeConcept = conceptService.getConcept(personAttributeType.getForeignKey());
            }
            patientConfigResponse.addPersonAttribute(personAttributeType, attributeConcept);
        }
        return patientConfigResponse;
    }

    @Override
    public Patient createPatient(BahmniPatient bahmniPatient) {
        Patient patient = null;
        ExecutionMode executionMode = properties.getExecutionMode();
        try {
            patient = savePatient(bahmniPatient, patient);
        } catch (APIAuthenticationException e) {
            throw e;
        } catch (RuntimeException e) {
            executionMode.handleSavePatientFailure(e, bahmniPatient);
        }

        try {
            String fullName = patient == null ? bahmniPatient.getFullName() : patient.getPersonName().getFullName();
            String patientId = patient == null ? bahmniPatient.getIdentifier() : patient.getPatientIdentifier().toString();
            BahmniAddress bahmniAddress = bahmniPatient.getAddresses().get(0);
            String village = bahmniAddress == null ? null : bahmniAddress.getCityVillage();
            billingService.createCustomer(fullName, patientId, village);
            if (bahmniPatient.hasBalance()) {
                billingService.updateCustomerBalance(patientId, bahmniPatient.getBalance());
            }
        } catch (Exception e) {
            executionMode.handleOpenERPFailure(e, bahmniPatient, patient);
        }
        return patient;
    }

    private Patient savePatient(BahmniPatient bahmniPatient, Patient patient) {
        patient = patientMapper.map(patient, bahmniPatient);
        Patient savedPatient = patientService.savePatient(patient);
        String patientIdentifier = savedPatient.getPatientIdentifier().toString();
        logger.debug(String.format("[%s] : Patient saved", patientIdentifier));
        patientImageService.save(patientIdentifier, bahmniPatient.getImage());
        return savedPatient;
    }

    @Override
    public Patient updatePatient(BahmniPatient bahmniPatient) {
        Patient patient = getPatientByUuid(bahmniPatient.getUuid());
        return savePatient(bahmniPatient, patient);
    }

    @Override
    public void updateImage(String uuid, String image) {
        Patient patient = getPatientByUuid(uuid);
        patientImageService.save(patient.getPatientIdentifier().getIdentifier(), image);
    }

    private Patient getPatientByUuid(String uuid) {
        return patientService.getPatientByUuid(uuid);
    }
}
