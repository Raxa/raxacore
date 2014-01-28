package org.bahmni.module.openerpatomfeedclient.api.task;

import org.bahmni.module.openerpatomfeedclient.api.client.OpenERPSaleOrderFailedFeedClient;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

public class OpenERPSaleOrderFailedFeedTask extends AbstractTask {


    @Override
    public void execute() {
        OpenERPSaleOrderFailedFeedClient feedClient = Context.getService(OpenERPSaleOrderFailedFeedClient.class);
        feedClient.processFailedFeed();
    }
}
