package org.bahmni.module.bahmnicore.service.impl;

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

    @Autowired
    public BahmniPatientServiceImpl(BillingService billingService, PatientImageService patientImageService) {
        this.billingService = billingService;
        this.patientImageService = patientImageService;
    }

    @Override
    public Patient createPatient(BahmniPatient bahmniPatient) {
        Patient patient = null;
        patient = savePatient(bahmniPatient, patient);
        createCustomerForBilling(patient, bahmniPatient);
        return patient;
    }

    private boolean customerHasBalance(BahmniPatient patient) {
        return !Double.isNaN(patient.getBalance());
    }

    private Patient savePatient(BahmniPatient bahmniPatient, Patient patient) {
        patient = getPatientMapper().map(patient, bahmniPatient);
        Patient savedPatient = getPatientService().savePatient(patient);
        patientImageService.save(savedPatient.getPatientIdentifier().toString(), bahmniPatient.getImage());
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

    private void createCustomerForBilling(Patient patient, BahmniPatient bahmniPatient) {
        String name = patient.getPersonName().getFullName();
        String patientId = patient.getPatientIdentifier().toString();
        try{
            billingService.createCustomer(name, patientId);
            if(customerHasBalance(bahmniPatient)){
                billingService.updateCustomerBalance(patientId, bahmniPatient.getBalance());
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
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
