package org.bahmni.datamigration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        while (true) {
            try {
                List<ParallelMigrator> migrators = new ArrayList<ParallelMigrator>();
                int noOfThreads = 10;
                for(int i =0; i < noOfThreads; i++){
                    ParallelMigrator parallelMigrator = migrator(patientEnumerator, url);
                    if (parallelMigrator == null) break;
                    migrators.add(parallelMigrator);
                    parallelMigrator.run();
                }

                Iterator<ParallelMigrator> itr = migrators.iterator();
                while(itr.hasNext()){
                    ParallelMigrator parallelMigrator = itr.next();
                    parallelMigrator.join();
                    logError(parallelMigrator,patientEnumerator);
                }

            }catch(Exception e){
                log.error("Failed to process patient", e);
            }

        }
    }

    private ParallelMigrator migrator(PatientEnumerator patientEnumerator, String url) throws Exception {
        PatientData patientData = patientEnumerator.nextPatient();
        if (patientData == null) return null;
        ParallelMigrator parallelMigrator = new ParallelMigrator(patientData,url,sessionId);
        return parallelMigrator;
    }

    private void logError(ParallelMigrator parallelMigrator, PatientEnumerator patientEnumerator) {
        List<PatientData> errorList = parallelMigrator.errorData();
        Iterator<PatientData> patientDataIterator = errorList.iterator();
        while (patientDataIterator.hasNext())  {
            patientEnumerator.failedPatient(patientDataIterator.next());
        }

    }
}