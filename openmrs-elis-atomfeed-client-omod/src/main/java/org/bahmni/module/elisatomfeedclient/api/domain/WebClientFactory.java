package org.bahmni.module.elisatomfeedclient.api.domain;

import org.bahmni.module.elisatomfeedclient.api.ReferenceDataFeedProperties;
import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class WebClientFactory {

    public static HttpClient createReferenceDataClient(ReferenceDataFeedProperties referenceDataFeedProperties) throws IOException {
        return new HttpClient(new ConnectionDetails(referenceDataFeedProperties.getReferenceDataUri(), null, null, referenceDataFeedProperties.getConnectTimeout(), referenceDataFeedProperties.getReadTimeout()));
    }
}
