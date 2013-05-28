package org.bahmni.module.bahmnicore.service.impl;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.BahmniCoreApiProperties;
import org.bahmni.module.bahmnicore.datamigration.ExecutionMode;
import org.bahmni.module.bahmnicore.mapper.*;
import org.bahmni.module.bahmnicore.model.BahmniAddress;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.bahmni.module.billing.BillingService;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy //to get rid of cyclic dependencies
public class BahmniPatientServiceImpl implements BahmniPatientService {
    PatientService patientService;
    private BillingService billingService;
    private PatientImageService patientImageService;
    private BahmniCoreApiProperties properties;
    private PatientMapper patientMapper;
    private static Logger logger = Logger.getLogger(BahmniPatientServiceImpl.class);

    @Autowired
    public BahmniPatientServiceImpl(BillingService billingService, PatientImageService patientImageService, BahmniCoreApiProperties properties) {
        this.billingService = billingService;
        this.patientImageService = patientImageService;
        this.properties = properties;
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
        } catch (RuntimeException e) {
            executionMode.handleOpenERPFailure(e, bahmniPatient, patient);
        }
        return patient;
    }

    private Patient savePatient(BahmniPatient bahmniPatient, Patient patient) {
        patient = getPatientMapper().map(patient, bahmniPatient);
        Patient savedPatient = getPatientService().savePatient(patient);
        String patientIdentifier = savedPatient.getPatientIdentifier().toString();
        logger.debug(String.format("[%s] : Patient saved", patientIdentifier));
        patientImageService.save(patientIdentifier, bahmniPatient.getImage());
        return savedPatient;
    }

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    public PatientService getPatientService() {
        if (patientService == null)
            patientService = Context.getPatientService();
        return patientService;
    }

    public void setPatientMapper(PatientMapper patientMapper) {
        this.patientMapper = patientMapper;
    }

    private PatientMapper getPatientMapper() {
        if (patientMapper == null) patientMapper = new PatientMapper(new PersonNameMapper(), new BirthDateMapper(), new PersonAttributeMapper(),
                new AddressMapper(), new PatientIdentifierMapper(), new HealthCenterMapper());
        return patientMapper;
    }

    @Override
    public Patient updatePatient(BahmniPatient bahmniPatient) {
        Patient patient = getPatientService().getPatientByUuid(bahmniPatient.getUuid());

        return savePatient(bahmniPatient, patient);
    }
}
