package org.bahmni.datamigration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.datamigration.request.referencedata.PersonAttribute;
import org.bahmni.datamigration.request.referencedata.PersonAttributeRequest;
import org.bahmni.datamigration.response.AuthenticationResponse;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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
import java.util.List;

public class Migrator {
    private RestTemplate restTemplate = new RestTemplate();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final Log log = LogFactory.getLog(Migrator.class);
    private static BASE64Encoder base64Encoder = new BASE64Encoder();
    private String baseURL;
    private String userId;
    private String password;
    private String sessionId;

    public static void main(String[] args) throws URISyntaxException, IOException {
        String openMRSAPIUrl = "http://172.18.2.1:8080/openmrs/ws/rest/v1/";
        Migrator migrator = new Migrator(openMRSAPIUrl, "admin", "P@ssw0rd");
        migrator.authenticate();
        migrator.loadReferences();
    }

    public Migrator(String baseURL, String userId, String password) {
        this.baseURL = baseURL;
        this.userId = userId;
        this.password = password;
    }

    public void authenticate() throws URISyntaxException, IOException {
        HttpHeaders requestHeaders = new HttpHeaders();
        String encodedLoginInfo = base64Encoder.encode((userId + ":" + password).getBytes());
        requestHeaders.set("Authorization", "Basic " + encodedLoginInfo);
        HttpEntity requestEntity = new HttpEntity<MultiValueMap>(new LinkedMultiValueMap<String, String>(), requestHeaders);
        String authURL = baseURL + "session";
        ResponseEntity<String> exchange = restTemplate.exchange(new URI(authURL), HttpMethod.GET, requestEntity, String.class);
        AuthenticationResponse authenticationResponse = objectMapper.readValue(exchange.getBody(), AuthenticationResponse.class);
        sessionId = authenticationResponse.getSessionId();
    }

    public void loadReferences() throws URISyntaxException, IOException {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", "JSESSIONID=" + sessionId);
        String referencesURL = baseURL + "personattributetype?v=full";
        HttpEntity requestEntity = new HttpEntity<MultiValueMap>(new LinkedMultiValueMap<String, String>(), requestHeaders);
        ResponseEntity<String> exchange = restTemplate.exchange(new URI(referencesURL), HttpMethod.GET, requestEntity, String.class);
        System.out.println(exchange.getBody());
    }

    public void migratePatient() {
//        restTemplate.postForLocation(url, );
    }

    public void migratePersonAttribute(PersonAttributeRequest request) {
        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            restTemplate.postForLocation(baseURL, request);
        } catch (IOException e) {
            log.error(e);
        }
    }
}