package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.RegistrationSmsService;
import org.bahmni.module.bahmnicore.service.SMSService;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.patient.PatientProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class RegistrationSmsServiceImpl implements RegistrationSmsService {

    private SMSService smsService;
    @Autowired
    public RegistrationSmsServiceImpl(SMSService smsService) {
        this.smsService = smsService;
    }

    @Override
    @Transactional(readOnly = true)
    public Object sendRegistrationSMS(PatientProfile profile,String locationUuid,String reportingSessionCookie) {
        Patient patient = profile.getPatient();
        Location location = Context.getLocationService().getLocationByUuid(locationUuid);
        String message = smsService.getRegistrationMessage(new Locale("en"), patient, location);
        smsService.sendSMS(patient.getAttribute("phoneNumber").getValue(),message,reportingSessionCookie);
        return null;
    }
}
