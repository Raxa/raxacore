package org.bahmni.openerp.web.http.client;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpClient {
    Logger logger = Logger.getLogger(HttpClient.class);
    private RestTemplate restTemplate;

    @Autowired
    public HttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String post(String url, String formPostData) {
        try {
            logger.debug("Post Data: " + formPostData);
            String response = restTemplate.postForObject(url, formPostData, String.class);
            logger.debug("Post Data output: " + response);
            return response;
        } catch (Exception e) {
            logger.error("Could not post  to " + url, e);
            logger.error("Post data: " + formPostData);
            throw new RuntimeException("Could not post message", e);
        }
    }
}
