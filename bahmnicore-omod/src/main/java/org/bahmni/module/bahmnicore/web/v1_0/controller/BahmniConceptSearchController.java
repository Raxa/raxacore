package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSearchResult;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.concept.EmrConceptService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(method = RequestMethod.GET, value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/concept")
public class BahmniConceptSearchController extends BaseRestController {
    @Autowired
    EmrApiProperties emrApiProperties;
    @Autowired
    EmrConceptService emrService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object search(@RequestParam("term") String query) throws Exception {

        Collection<Concept> diagnosisSets = emrApiProperties.getDiagnosisSets();
        Locale locale = Locale.ENGLISH;
        List<ConceptSearchResult> conceptSearchResults = emrService.conceptSearch(query, locale, null, diagnosisSets, null, null);
        List<ConceptName> matchingConceptNames = new ArrayList<>();
        for (ConceptSearchResult searchResult : conceptSearchResults) {
            matchingConceptNames.add(searchResult.getConceptName());
        }
        return createListResponse(matchingConceptNames);
    }

    private List<SimpleObject> createListResponse(List<ConceptName> resultList) {
        List<SimpleObject> allDiagnoses = new ArrayList<>();

        for (ConceptName diagnosis : resultList) {
            SimpleObject diagnosisObject = new SimpleObject();
            if (isNotFullName(diagnosis)) {
                ConceptName fullySpecifiedName = diagnosis.getConcept().getName();
                diagnosisObject.add("fullName", fullySpecifiedName.getName());
            }
            diagnosisObject.add("matchedName", diagnosis.getName());
            allDiagnoses.add(diagnosisObject);
        }
        return allDiagnoses;
    }

    private boolean isNotFullName(ConceptName diagnosis) {
        return diagnosis.getConceptNameType() == null || !diagnosis.getConceptNameType().equals(ConceptNameType.FULLY_SPECIFIED);
    }

}
