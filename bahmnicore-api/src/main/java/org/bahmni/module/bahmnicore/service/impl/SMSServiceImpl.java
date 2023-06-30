package org.bahmni.module.bahmnicore.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bahmni.module.bahmnicore.contract.SMS.SMSRequest;
import org.bahmni.module.bahmnicore.properties.BahmniCoreProperties;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.bahmnicore.service.SMSService;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Locale;

@Service
public class SMSServiceImpl implements SMSService {
    private static Logger logger = LogManager.getLogger(BahmniDrugOrderService.class);
    private final static String REGISTRATION_SMS_TEMPLATE = "sms.registrationSMSTemplate";
    private final static String SMS_URI = "sms.uri";

    public SMSServiceImpl() {}

    @Override
    public Object sendSMS(String phoneNumber, String message,String reportingSessionCookie) {
        try {
            SMSRequest smsRequest = new SMSRequest();
            smsRequest.setPhoneNumber(phoneNumber);
            smsRequest.setMessage(message);

            ObjectMapper Obj = new ObjectMapper();
            String jsonObject = Obj.writeValueAsString(smsRequest);
            StringEntity params = new StringEntity(jsonObject);
            String smsUrl = StringUtils.isBlank(BahmniCoreProperties.getProperty("sms.uri")) ? SMS_URI : BahmniCoreProperties.getProperty("sms.uri");


            HttpPost request = new HttpPost(Context.getMessageSourceService().getMessage(smsUrl, null, new Locale("en")));
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            request.setHeader("Cookie","reporting_session=" +reportingSessionCookie);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpResponse response = httpClient.execute(request);
            httpClient.close();
            return response.getStatusLine();
        } catch (Exception e) {
            logger.error("Exception occured in sending sms ", e);
            throw new RuntimeException("Exception occured in sending sms ", e);
        }
    }

    @Override
    public String getRegistrationMessage (Locale locale, Patient patient, Location location) {
        String smsTemplate = Context.getAdministrationService().getGlobalProperty(REGISTRATION_SMS_TEMPLATE);
        String helpdeskNumber = Context.getAdministrationService().getGlobalPropertyObject("clinic.helpDeskNumber").getPropertyValue();
        String clinicTime = Context.getAdministrationService().getGlobalPropertyObject("clinic.clinicTimings").getPropertyValue();

        Object[] arguments = {location.getName(),patient.getPatientIdentifier().getIdentifier(),
                patient.getGivenName() + " " + patient.getFamilyName(),  patient.getGender(),patient.getAge().toString(),helpdeskNumber,clinicTime};
        if (StringUtils.isBlank(smsTemplate)) {
            return Context.getMessageSourceService().getMessage(REGISTRATION_SMS_TEMPLATE, arguments, locale).replace("\\n", System.lineSeparator());
        } else {
            return new MessageFormat(smsTemplate).format(arguments).replace("\\n", System.lineSeparator());
        }
    }

}