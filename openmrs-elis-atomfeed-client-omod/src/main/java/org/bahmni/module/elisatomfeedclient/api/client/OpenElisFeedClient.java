package org.bahmni.module.elisatomfeedclient.api.client;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.webclients.AnonymousAuthenticator;
import org.bahmni.webclients.ClientCookies;
import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.joda.time.DateTime;
import org.openmrs.module.atomfeed.common.repository.OpenMRSJdbcConnectionProvider;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class OpenElisFeedClient{
    protected AtomFeedClient atomFeedClient;
    private ElisAtomFeedProperties properties;
    private JdbcConnectionProvider jdbcConnectionProvider;
    private Logger logger = Logger.getLogger(OpenElisFeedClient.class);

    public OpenElisFeedClient(ElisAtomFeedProperties properties, PlatformTransactionManager transactionManager) {
        this.jdbcConnectionProvider = new OpenMRSJdbcConnectionProvider(transactionManager);
        this.properties = properties;
    }

    protected void initializeAtomFeedClient() {
        String feedUri = getFeedUri(properties);
        try {
            ConnectionDetails connectionDetails = createConnectionDetails(properties);
            HttpClient httpClient = new HttpClient(connectionDetails, new AnonymousAuthenticator(connectionDetails));
            ClientCookies cookies = httpClient.getCookies(new URI(feedUri));
            EventWorker openMRSEventWorker = createWorker(httpClient, properties);
            atomFeedClient = new AtomFeedClient(
                    new AllFeeds(properties, cookies),
                    new AllMarkersJdbcImpl(jdbcConnectionProvider),
                    new AllFailedEventsJdbcImpl(jdbcConnectionProvider),
                    properties,
                    jdbcConnectionProvider,
                    new URI(feedUri),
                    openMRSEventWorker);
        } catch (URISyntaxException e) {
            logger.error("openelisatomfeedclient:error instantiating client:" + e.getMessage(), e);
            throw new RuntimeException("error for uri:" + feedUri);
        }
    }

    protected abstract String getFeedUri(ElisAtomFeedProperties properties);

    private ConnectionDetails createConnectionDetails(ElisAtomFeedProperties properties) {
        return new ConnectionDetails(properties.getOpenElisUri(),null,null,properties.getConnectTimeout(),properties.getReadTimeout());
    }

    protected abstract EventWorker createWorker(HttpClient authenticatedWebClient, ElisAtomFeedProperties properties);

}
