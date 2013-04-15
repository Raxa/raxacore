package org.bahmni.module.bahmnicore.service.impl;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.mapper.*;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.bahmni.module.billing.BillingService;
import org.openmrs.Patient;
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
    private PatientMapper patientMapper;
    private static Logger logger = Logger.getLogger(BahmniPatientServiceImpl.class);

    @Autowired
    public BahmniPatientServiceImpl(BillingService billingService, PatientImageService patientImageService) {
        this.billingService = billingService;
        this.patientImageService = patientImageService;
    }

    @Override
    public Patient createPatient(BahmniPatient bahmniPatient) {
        Patient patient = null;
        patient = savePatient(bahmniPatient, patient);
        createCustomerForBilling(patient);
        if(customerHasBalance(bahmniPatient)){
            updateCustomerWithBalance(patient, bahmniPatient);
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

    private void createCustomerForBilling(Patient patient) {
        String name = patient.getPersonName().getFullName();
        String patientId = patient.getPatientIdentifier().toString();
        try{
            billingService.createCustomer(name, patientId);
        }catch(Exception exception){
            logger.error(exception.getMessage(), exception);
        }
    }

    private void updateCustomerWithBalance(Patient patient, BahmniPatient bahmniPatient) {
        String patientId = patient.getPatientIdentifier().toString();
        try {
            billingService.updateCustomerBalance(patientId, bahmniPatient.getBalance());
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
    }

    private boolean customerHasBalance(BahmniPatient patient) {
        return !Double.isNaN(patient.getBalance());
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
        if(patientMapper == null) patientMapper =   new PatientMapper(new PersonNameMapper(), new BirthDateMapper(), new PersonAttributeMapper(),
                new AddressMapper(), new PatientIdentifierMapper(), new HealthCenterMapper());
        return patientMapper;
    }

    @Override
    public Patient updatePatient(BahmniPatient bahmniPatient) {
        Patient patient = getPatientService().getPatientByUuid(bahmniPatient.getUuid());

        return savePatient(bahmniPatient, patient);
    }
}
