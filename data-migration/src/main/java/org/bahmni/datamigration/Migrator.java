package org.bahmni.datamigration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.bahmni.datamigration.request.patient.PatientRequest;
import org.bahmni.datamigration.response.AuthenticationResponse;
import org.bahmni.datamigration.response.PersonAttributeType;
import org.bahmni.datamigration.response.PersonAttributeTypes;
import org.bahmni.datamigration.session.AllPatientAttributeTypes;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Migrator {
    private RestTemplate restTemplate = new RestTemplate();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final Log log = LogFactory.getLog(Migrator.class);
    private String sessionId;
    private static Logger logger = Logger.getLogger(Migrator.class);
    private AllPatientAttributeTypes allPatientAttributeTypes;
    private OpenMRSRESTConnection openMRSRESTConnection;

    public Migrator(OpenMRSRESTConnection openMRSRESTConnection) throws IOException, URISyntaxException {
        this.openMRSRESTConnection = openMRSRESTConnection;
        authenticate();
        loadReferences();
    }

    public void authenticate() throws URISyntaxException, IOException {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", "Basic " + openMRSRESTConnection.encodedLogin());
        HttpEntity requestEntity = new HttpEntity<MultiValueMap>(new LinkedMultiValueMap<String, String>(), requestHeaders);
        String authURL = openMRSRESTConnection.getRestApiUrl() + "session";
        ResponseEntity<String> exchange = restTemplate.exchange(new URI(authURL), HttpMethod.GET, requestEntity, String.class);
        logger.info(exchange.getBody());
        AuthenticationResponse authenticationResponse = objectMapper.readValue(exchange.getBody(), AuthenticationResponse.class);
        sessionId = authenticationResponse.getSessionId();
    }

    private void loadReferences() throws URISyntaxException, IOException {
        allPatientAttributeTypes = new AllPatientAttributeTypes();
        String jsonResponse = executeHTTPMethod("personattributetype?v=full", HttpMethod.GET);
        PersonAttributeTypes personAttributeTypes = objectMapper.readValue(jsonResponse, PersonAttributeTypes.class);
        for (PersonAttributeType personAttributeType : personAttributeTypes.getResults())
            allPatientAttributeTypes.addPersonAttributeType(personAttributeType.getName(), personAttributeType.getUuid());
    }

    public AllPatientAttributeTypes getAllPatientAttributeTypes() {
        return allPatientAttributeTypes;
    }

    private String executeHTTPMethod(String urlSuffix, HttpMethod method) throws URISyntaxException {
        HttpHeaders requestHeaders = getHttpHeaders();
        String referencesURL = openMRSRESTConnection.getRestApiUrl() + urlSuffix;
        HttpEntity requestEntity = new HttpEntity<MultiValueMap>(new LinkedMultiValueMap<String, String>(), requestHeaders);
        ResponseEntity<String> exchange = restTemplate.exchange(new URI(referencesURL), method, requestEntity, String.class);
        logger.debug("(" + urlSuffix + ") - " + exchange.getBody());
        return exchange.getBody();
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", "JSESSIONID=" + sessionId);
        return requestHeaders;
    }

    public void migratePatient(PatientEnumerator patientEnumerator) {
        String url = openMRSRESTConnection.getRestApiUrl() + "bahmnicore/patient";
        int i = 0;
        while (true) {
            String jsonRequest = null;
            PatientData patientData = null;
            ResponseEntity<String> out;
            PatientRequest patientRequest = null;
            try {
                i++;
                patientData = patientEnumerator.nextPatient();
                if (patientData == null) break;

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
                patientEnumerator.failedPatient(patientData);
            } catch (Exception e) {
                log.info(String.format("%d Failed to create", i));
                log.info("Patient request: " + jsonRequest);
                log.error("Failed to process a patient", e);
                patientEnumerator.failedPatient(patientData);
            }
        }
    }
}