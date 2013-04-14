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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Migrator {
    private RestTemplate restTemplate = new RestTemplate();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final Log log = LogFactory.getLog(Migrator.class);
    private static BASE64Encoder base64Encoder = new BASE64Encoder();
    private String baseURL;
    private String userId;
    private String password;
    private String sessionId;
    private static Logger logger = Logger.getLogger(Migrator.class);
    private AllPatientAttributeTypes allPatientAttributeTypes;

    public Migrator(String baseURL, String userId, String password) throws IOException, URISyntaxException {
        this.baseURL = baseURL;
        this.userId = userId;
        this.password = password;

        authenticate();
        loadReferences();
    }

    public void authenticate() throws URISyntaxException, IOException {
        String encodedLoginInfo = base64Encoder.encode((userId + ":" + password).getBytes());
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", "Basic " + encodedLoginInfo);
        HttpEntity requestEntity = new HttpEntity<MultiValueMap>(new LinkedMultiValueMap<String, String>(), requestHeaders);
        String authURL = baseURL + "allPatientAttributeTypes";
        ResponseEntity<String> exchange = restTemplate.exchange(new URI(authURL), HttpMethod.GET, requestEntity, String.class);
        logger.debug(exchange.getBody());
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
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", "JSESSIONID=" + sessionId);
        String referencesURL = baseURL + urlSuffix;
        HttpEntity requestEntity = new HttpEntity<MultiValueMap>(new LinkedMultiValueMap<String, String>(), requestHeaders);
        ResponseEntity<String> exchange = restTemplate.exchange(new URI(referencesURL), method, requestEntity, String.class);
        logger.debug(exchange.getBody());
        return exchange.getBody();
    }

    public void migratePatient(PatientReader patientReader) {
        try {
            PatientRequest patientRequest;
            while ((patientRequest = patientReader.nextPatient()) != null) {
                String jsonRequest = objectMapper.writeValueAsString(patientRequest);
                restTemplate.postForLocation(baseURL, jsonRequest);
            }
        } catch (IOException e) {
            log.error(e);
        }
    }
}