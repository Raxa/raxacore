package org.bahmni.module.elisatomfeedclient.api.task;

import org.bahmni.module.elisatomfeedclient.api.client.FailedEventsFeedClient;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisLabResultFailedEventsFeedClient;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

public class OpenElisLabResultFeedFailedEventsTask extends AbstractTask {

    @Override
    public void execute() {
        FailedEventsFeedClient feedClient = Context.getService(OpenElisLabResultFailedEventsFeedClient.class);
        feedClient.processFailedEvents();
    }
}
