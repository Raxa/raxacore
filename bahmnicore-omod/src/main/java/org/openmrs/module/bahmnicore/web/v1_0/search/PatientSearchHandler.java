package org.openmrs.module.bahmnicore.web.v1_0.search;

import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.bahmni.module.bahmnicore.contract.patient.PatientSearchParameters;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PatientSearchHandler implements SearchHandler {

    private BahmniPatientService bahmniPatientService;

    @Autowired
    public PatientSearchHandler(BahmniPatientService bahmniPatientService) {
        this.bahmniPatientService = bahmniPatientService;
    }

    @Override
    public SearchConfig getSearchConfig() {
        return new SearchConfig("byIdOrNameOrVillage", RestConstants.VERSION_1 + "/patient", Arrays.asList("1.9.*"),
                new SearchQuery.Builder("Allows you to find patients which map to id or name or village name given as input").withOptionalParameters("id", "name", "village").build());
    }

    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
        PatientSearchParameters searchParameters = new PatientSearchParameters(context);
        List<PatientResponse> patients = bahmniPatientService.search(searchParameters);
        return new AlreadyPaged<>(context, patients, false);
    }

}
