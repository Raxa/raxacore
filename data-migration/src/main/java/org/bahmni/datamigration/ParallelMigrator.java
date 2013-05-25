package org.bahmni.datamigration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.bahmni.datamigration.request.patient.PatientRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class ParallelMigrator extends Thread{

    private RestTemplate restTemplate = new RestTemplate();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final Log log = LogFactory.getLog(Migrator.class);
    private String sessionId;
    private static Logger logger = Logger.getLogger(Migrator.class);
    private OpenMRSRESTConnection openMRSRESTConnection;
    private static int count;
    PatientEnumerator patientEnumerator;
    PatientData patientData = null;
    String url;
    List<PatientData> errorList = new ArrayList<PatientData>();

    public ParallelMigrator(PatientData patientData,PatientEnumerator patientEnumerator,String url){
         this.patientEnumerator = patientEnumerator;
         this.patientData = patientData;
         this.url = url;
    }

    @Override
    public void run() {
        int i = incrementCounter();
        String jsonRequest = null;
        ResponseEntity<String> out = null;
        PatientRequest patientRequest = null;
        try{
            patientRequest = patientData.getPatientRequest();
            jsonRequest = objectMapper.writeValueAsString(patientRequest);
            if (logger.isDebugEnabled()) logger.debug(jsonRequest);

            HttpHeaders httpHeaders = getHttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity(patientRequest, httpHeaders);
            out = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (logger.isDebugEnabled()) logger.debug(out.getBody());
            log.info(String.format("%d Successfully created %s", i, patientRequest.getIdentifier()));
        } catch (HttpServerErrorException serverErrorException) {
            log.info(String.format("%d Failed to create %s", i, patientRequest.getIdentifier()));
            log.info("Patient request: " + jsonRequest);
            log.error("Patient create response: " + serverErrorException.getResponseBodyAsString());
            errorList.add(patientData);

            //patientEnumerator.failedPatient(patientData);
        } catch (Exception e) {
            log.info(String.format("%d Failed to create", i));
            log.info("Patient request: " + jsonRequest);
            log.error("Failed to process a patient", e);
            //patientEnumerator.failedPatient(patientData);
            log.info("Patient request: " + jsonRequest);
            errorList.add(patientData);
        //patientEnumerator.failedPatient(patientData);
    }
    }

    private synchronized int incrementCounter(){
            return count++;
    }

    public List<PatientData> errorData(){
        return errorList;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", "JSESSIONID=" + sessionId);
        return requestHeaders;
    }

}
