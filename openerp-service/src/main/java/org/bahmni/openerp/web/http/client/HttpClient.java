package org.bahmni.openerp.web.http.client;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpClient {
    private static final Logger logger = Logger.getLogger(HttpClient.class);
    private RestTemplate restTemplate;

    private boolean isTimeoutSet;

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

    public void setTimeout(int replyTimeoutInMilliseconds) {
        if (!isTimeoutSet) {
            try {
                CommonsClientHttpRequestFactory requestFactoryWithTimeout = new CommonsClientHttpRequestFactory();
                requestFactoryWithTimeout.setReadTimeout(replyTimeoutInMilliseconds);
                restTemplate.setRequestFactory(requestFactoryWithTimeout);

                isTimeoutSet = true;
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
