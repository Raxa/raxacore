package org.bahmni.module.referncedatafeedclient.task;

import org.bahmni.module.referncedatafeedclient.client.AtomFeedProcessor;
import org.bahmni.module.referncedatafeedclient.client.FailedEventProcessor;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

public class ReferenceDataFailedEventTask extends AbstractTask {

    @Override
    public void execute() {
        FailedEventProcessor atomFeedProcessor = Context.getRegisteredComponent("referenceDataFailedEventProcessor", FailedEventProcessor.class);
        atomFeedProcessor.processFeed();
    }
}
