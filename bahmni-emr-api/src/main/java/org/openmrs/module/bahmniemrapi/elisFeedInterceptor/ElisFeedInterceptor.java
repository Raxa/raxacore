package org.openmrs.module.bahmniemrapi.elisFeedInterceptor;

import org.openmrs.Encounter;

import java.util.Set;

public interface ElisFeedInterceptor {
    public void run(Set<Encounter> encounters);
}
