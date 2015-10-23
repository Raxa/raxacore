package org.bahmni.module.bahmnicore.web.v1_0.search;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

@Component
public class VisitFormsSearchHandler implements SearchHandler {


    private final String ALL_OBSERVATION_TEMPLATES = "All Observation Templates";
    private final String QUERY_INFORMATION = "Allows you to search All Observation Templates by patientUuid";

    @Override
    public SearchConfig getSearchConfig() {
        SearchQuery searchQuery = new SearchQuery.Builder(QUERY_INFORMATION).withRequiredParameters("patient", "numberOfVisits").build();
        return new SearchConfig("byPatientUuid", RestConstants.VERSION_1 + "/obs", asList("1.10.*", "1.11.*", "1.12.*"), searchQuery);
    }

    @Override
    public PageableResult search(RequestContext context) throws ResponseException {

        String patientUuid = context.getRequest().getParameter("patient");
        int numberOfVisits = Integer.parseInt(context.getRequest().getParameter("numberOfVisits"));

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        if (patient == null) {
            throw new InvalidSearchException("Patient does not exist.");
        }

        List<Concept> conceptSetMembers = Context.getConceptService().getConcept(ALL_OBSERVATION_TEMPLATES).getSetMembers();
        List<Visit> listOfVisitsNeeded = listOfVisitsNeeded(numberOfVisits, patient);

        List<Obs> finalObsList = new ArrayList<>();
        List<Encounter> encounterList = Context.getEncounterService().getEncounters(patient, null, null, null, null, null, null, null, listOfVisitsNeeded, false);

        List<Obs> initialObsList;
        initialObsList = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()), encounterList, null, null, null, null, null, null, null, null, null, false);

        List<String> conceptUuids = new ArrayList<>();
        for (Concept conceptSetMember : conceptSetMembers) {
            conceptUuids.add(conceptSetMember.getUuid());
        }

        if(CollectionUtils.isNotEmpty(conceptUuids)) {
            for (Obs obs : initialObsList) {
                if (conceptUuids.contains(obs.getConcept().getUuid())) {
                    finalObsList.add(obs);
                }
            }
        }
        return new NeedsPaging<Obs>(finalObsList, context);
    }

    private List<Visit> listOfVisitsNeeded(int numberOfVisits, Patient patient) {
        List<Visit> visitsByPatient = Context.getVisitService().getVisitsByPatient(patient);
        int subsetVisits = numberOfVisits;
        if (visitsByPatient.size() < numberOfVisits) {
            subsetVisits = visitsByPatient.size();
        }
        return visitsByPatient.subList(0, subsetVisits);
    }
}