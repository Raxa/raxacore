package org.bahmni.module.bahmnicore.openmrsadvice;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bahmni.module.bahmnicore.events.eventPublisher.BahmniEventPublisher;
import org.bahmni.module.eventoutbox.EMREvent;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;
import java.util.Set;

public class EncounterSaveAdvice extends BaseAdvice implements AfterReturningAdvice {

    public static final String ENCOUNTER_REST_URL = "/openmrs/ws/rest/v1/encounter/{uuid}?v=full";
    public static final String TITLE = "Encounter";
    public static final String CATEGORY = "Encounter";
    private static final String SAVE_METHOD = "saveEncounter";
    private static final String eventRaiseFlagGP = "eventoutbox.publish.eventsForEncounter";
    private static final String urlTemplateGP = "eventoutbox.publish.urlTemplateForEncounter";

    private final Logger log = LogManager.getLogger(this.getClass());
    private final BahmniEventPublisher eventPublisher;
    private final Set<String> adviceMethodNames = Sets.newHashSet(SAVE_METHOD);

    public EncounterSaveAdvice() {
        this.eventPublisher = Context.getRegisteredComponent("bahmniEventPublisher", BahmniEventPublisher.class);
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] arguments, Object target) {
        if (adviceMethodNames.contains(method.getName()) && shouldRaiseEvent()) {
            Encounter encounter = (Encounter) returnValue;
            String encounterUuid = encounter.getUuid();
            String restUrl = getUrlPattern(encounterUuid);
            EMREvent<Encounter> emrEvent = new EMREvent<>(encounter, CATEGORY, TITLE, null, restUrl);
            eventPublisher.publishEvent(emrEvent);
            log.info("Successfully published EMREvent with uuid: " + encounterUuid);
        }
    }

    @Override
    protected String getDefaultUrlTemplate() {
        return ENCOUNTER_REST_URL;
    }

    @Override
    protected String getEventRaiseFlagGlobalProperty() {
        return eventRaiseFlagGP;
    }

    @Override
    protected String getUrlTemplateGlobalProperty() {
        return urlTemplateGP;
    }
}
