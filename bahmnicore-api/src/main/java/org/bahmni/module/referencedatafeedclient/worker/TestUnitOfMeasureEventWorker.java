package org.bahmni.module.referencedatafeedclient.worker;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.dao.BahmniTestUnitsDao;
import org.bahmni.module.referencedatafeedclient.domain.ReferenceDataConcept;
import org.bahmni.module.referencedatafeedclient.domain.TestUnitOfMeasure;
import org.bahmni.module.referencedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class TestUnitOfMeasureEventWorker implements EventWorker {
    public static final String MISC = "Misc";

    private final ConceptService conceptService;
    private final ReferenceDataConceptService referenceDataConceptService;
    private final EventWorkerUtility eventWorkerUtility;
    private BahmniTestUnitsDao bahmniTestUnitsDao;
    @Resource(name = "referenceDataHttpClient")
    private HttpClient httpClient;
    private final ReferenceDataFeedProperties referenceDataFeedProperties;

    @Autowired
    public TestUnitOfMeasureEventWorker(HttpClient httpClient, ReferenceDataFeedProperties referenceDataFeedProperties,
                                        ConceptService conceptService, ReferenceDataConceptService referenceDataConceptService,
                                        EventWorkerUtility eventWorkerUtility, BahmniTestUnitsDao bahmniTestUnitsDao) {
        this.httpClient = httpClient;
        this.referenceDataFeedProperties = referenceDataFeedProperties;
        this.conceptService = conceptService;
        this.referenceDataConceptService = referenceDataConceptService;
        this.eventWorkerUtility = eventWorkerUtility;
        this.bahmniTestUnitsDao = bahmniTestUnitsDao;
    }

    @Override
    public void process(Event event) {
        try {
            TestUnitOfMeasure newUnitOfMeasure = httpClient.get(referenceDataFeedProperties.getReferenceDataUri() + event.getContent(), TestUnitOfMeasure.class);
            ReferenceDataConcept referenceDataConcept = new ReferenceDataConcept(newUnitOfMeasure.getId(), newUnitOfMeasure.getName(), MISC, ConceptDatatype.N_A_UUID);
            referenceDataConcept.setDescription(newUnitOfMeasure.getName());
            referenceDataConcept.setRetired(!newUnitOfMeasure.getIsActive());

            Concept savedUOMConcept = conceptService.getConceptByUuid(newUnitOfMeasure.getId());
            String oldUnit = savedUOMConcept != null ? savedUOMConcept.getName().getName() : null;
            String newUnit = newUnitOfMeasure.getIsActive() ? newUnitOfMeasure.getName() : null;
            if(savedUOMConcept != null && !oldUnit.equals(newUnit)) {
                bahmniTestUnitsDao.updateUnitsForTests(newUnit, oldUnit);
            }
            referenceDataConceptService.saveConcept(referenceDataConcept);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanUp(Event event) {

    }
}
