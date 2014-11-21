package org.bahmni.module.referencedata.labconcepts.model.event;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.Drug;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;

public class DrugEvent implements ConceptServiceOperationEvent {
    String url;
    String category;
    String title;

    public DrugEvent(String url, String category, String title) {
        this.url = url;
        this.category = category;
        this.title = title;
    }

    public DrugEvent() {
    }


    List<String> operations() {
        return asList("saveDrug", "purgeDrug");
    }

    private boolean isDrug(Object argument) {
        try{
            Drug drug = (Drug) argument;
            Concept drugConcept = drug.getConcept();
            return drugConcept.getConceptClass().getUuid().equals(ConceptClass.DRUG_UUID);
        } catch (Exception e){
            return false;
        }
    }

    private boolean isValid(Object[] arguments) {
        return arguments != null && arguments.length > 0;
    }

    @Override
    public Boolean isApplicable(String operation, Object[] arguments) {
        return this.operations().contains(operation) && isValid(arguments) && isDrug(arguments[0]);
    }

    @Override
    public Event asAtomFeedEvent(Object[] arguments) throws URISyntaxException {
        Drug drug = (Drug) arguments[0];
        String url = String.format(this.url, title, drug.getUuid());
        return new Event(UUID.randomUUID().toString(), title, DateTime.now(), url, url, category);
    }

}
