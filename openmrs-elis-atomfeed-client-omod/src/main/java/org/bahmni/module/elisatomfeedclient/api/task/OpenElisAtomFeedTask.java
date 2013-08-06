package org.bahmni.module.elisatomfeedclient.api.task;

import org.bahmni.module.elisatomfeedclient.api.client.OpenElisFeedClientInterface;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

public class OpenElisAtomFeedTask extends AbstractTask {

    @Override
    public void execute() {
        OpenElisFeedClientInterface feedClient = Context.getService(OpenElisFeedClientInterface.class);
        feedClient.processFeed();
    }

}
