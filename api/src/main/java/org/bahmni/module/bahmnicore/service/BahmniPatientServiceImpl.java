package org.bahmni.module.bahmnicore.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.mapper.*;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.billing.BillingService;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class BahmniPatientServiceImpl implements BahmniPatientService {
    PatientService patientService;
    private BillingService billingService;
    private PatientMapper patientMapper;
    private final Log log = LogFactory.getLog(BahmniPatientServiceImpl.class);

    public BahmniPatientServiceImpl(BillingService billingService) {
        this.billingService = billingService;
    }


    @Override
    public Patient createPatient(BahmniPatient bahmniPatient) {
        Patient patient = null;

        patient = savePatient(bahmniPatient, patient);
        createCustomerForBilling(patient, patient.getPatientIdentifier().toString());

//        capturePhoto(bahmniPatient.getImage());

        return patient;
    }

    private Patient savePatient(BahmniPatient bahmniPerson, Patient patient) {
        patient = getPatientMapper().map(patient, bahmniPerson);
        Patient savedPatient = getPatientService().savePatient(patient);
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

    private void createCustomerForBilling(Patient patient, String patientId) {
        String name = patient.getPersonName().getFullName();
        try{
            billingService.createCustomer(name, patientId);
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

    private void capturePhoto(String image) {
        try {
            String img64 = image.replace("data:image/png;base64,","");
            byte[] decodedBytes = DatatypeConverter.parseBase64Binary(img64);
            BufferedImage bfi = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            File outputfile = new File("/tmp/saved.gif");
            ImageIO.write(bfi , "gif", outputfile);
            bfi.flush();
        } catch (IOException e) {
            log.error("errorr", e);
        }
    }

    @Override
    public Patient updatePatient(BahmniPatient bahmniPatient) {
        Patient patient = getPatientService().getPatientByUuid(bahmniPatient.getUuid());

        return savePatient(bahmniPatient, patient);
    }
}
