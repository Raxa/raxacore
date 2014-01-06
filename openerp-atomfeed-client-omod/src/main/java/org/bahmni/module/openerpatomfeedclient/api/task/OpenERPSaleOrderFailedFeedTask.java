package org.bahmni.module.openerpatomfeedclient.api.task;

import org.bahmni.module.openerpatomfeedclient.api.client.OpenERPSaleOrderFeedClient;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

public class OpenERPSaleOrderFailedFeedTask extends AbstractTask {

    @Override
    public void execute() {
        OpenERPSaleOrderFeedClient feedClient = Context.getService(OpenERPSaleOrderFeedClient.class);
        feedClient.processFailedFeed();
    }
}
