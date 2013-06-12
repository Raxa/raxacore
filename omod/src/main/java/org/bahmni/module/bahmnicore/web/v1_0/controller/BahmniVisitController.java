package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.resource.BahmniVisitResource;
import org.joda.time.DateMidnight;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 +  "/bahmnivisit")
public class BahmniVisitController extends BaseCrudController<BahmniVisitResource> {

    private VisitService visitService;
    private PatientService patientService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Object create(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
            throws ResponseException {
        Visit visit = getActiveVisit((String) post.get("patient"));
        if (visit == null) {
            RequestContext context = RestUtil.getRequestContext(request);
            Object created = getResource().create(post, context);
            return RestUtil.created(response, created);
        }

        voidExistingEncountersForMatchingEncounterType(post, visit);
        removeNonUpdateableProperties(post);
        return update(visit.getUuid(), post, request, response);
    }

    private void removeNonUpdateableProperties(SimpleObject post) {
        post.removeProperty("patient");
        post.removeProperty("startDatetime");
    }

    private void voidExistingEncountersForMatchingEncounterType(SimpleObject post, Visit visit) {
        List<LinkedHashMap> encounters = (List<LinkedHashMap>) post.get("encounters");
        for (LinkedHashMap encounter : encounters) {
            voidEncounterMatchingEncounterType((String) encounter.get("encounterType"), visit);
        }
    }

    private void voidEncounterMatchingEncounterType(String encounterType, Visit visit) {
        Set<Encounter> encounters = visit.getEncounters();
        if (encounters == null) return;
        for (Encounter encounter: encounters) {
            if (encounter.getEncounterType().getName().equals(encounterType)) {
                encounter.setVoided(true);
                voidObservations(encounter);
            }
        }
    }

    private void voidObservations(Encounter encounter) {
        for (Obs obs: encounter.getAllObs()) {
            obs.setVoided(true);
        }
    }

    private Visit getActiveVisit(String patientUuid) {
        Patient patient = getPatientService().getPatientByUuid(patientUuid);
        List<Visit> activeVisitsByPatient = getVisitService().getActiveVisitsByPatient(patient);

        for (Visit visit: activeVisitsByPatient) {
            if (visit.getStartDatetime().after(DateMidnight.now().toDate())) {
                return visit;
            }
        }
        return null;
    }

    private VisitService getVisitService() {
        if (this.visitService == null) this.visitService = Context.getVisitService();
        return this.visitService;
    }

    private PatientService getPatientService() {
        if (this.patientService == null) this.patientService = Context.getPatientService();
        return this.patientService;
    }
}
