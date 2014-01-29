package org.bahmni.module.referncedatafeedclient.task;

import org.bahmni.module.referncedatafeedclient.client.ReferenceDataFeedClient;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

public class ReferenceDataFeedTask extends AbstractTask {

    @Override
    public void execute() {
        ReferenceDataFeedClient referenceDataFeedClient = Context.getService(ReferenceDataFeedClient.class);
        referenceDataFeedClient.processFeed();
    }
}
