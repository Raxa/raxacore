package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.ApplicationError;
import org.bahmni.module.bahmnicore.BahmniCoreException;
import org.bahmni.module.bahmnicore.BillingSystemException;
import org.bahmni.module.bahmnicore.contract.patient.response.PatientConfigResponse;
import org.bahmni.module.bahmnicore.dao.ActivePatientListDao;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.model.ResultList;
import org.bahmni.module.bahmnicore.model.error.ErrorCode;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for REST web service access to
 * the Drug resource.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/patient")
public class BahmniPatientController extends BaseRestController {

    private static Logger logger = Logger.getLogger(BahmniPatientController.class);
    private BahmniPatientService bahmniPatientService;
    private static final String[] REQUIRED_FIELDS = {"names", "gender"};

    @Autowired
    private ActivePatientListDao activePatientListDao;

    @Autowired
    public BahmniPatientController(BahmniPatientService bahmniPatientService) {
        this.bahmniPatientService = bahmniPatientService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "config")
    @ResponseBody
    public PatientConfigResponse getConfig() {
        return bahmniPatientService.getConfig();
    }

    @RequestMapping(method = RequestMethod.POST)
    @WSDoc("Save New Patient")
    @ResponseBody
    public Object createNewPatient(@RequestBody SimpleObject post, HttpServletResponse response) {
        BahmniPatient bahmniPatient;
        try {
            validatePost(post);
            bahmniPatient = new BahmniPatient(post);
            Patient patient = bahmniPatientService.createPatient(bahmniPatient);
            return respondCreated(response, bahmniPatient, patient);
        } catch (APIAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            return respondNotCreated(response, e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/active")
    @WSDoc("Get a list of active patients")
    @ResponseBody
    public Object getActivePatientsList() {
        return createListResponse(activePatientListDao.getPatientList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/toadmit")
    @WSDoc("Get a list of active patients to be admitted")
    @ResponseBody
    public Object getActivePatientsForAdmission() {
        return createListResponse(activePatientListDao.getPatientsForAdmission());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{patientUuid}")
    @WSDoc("Update existing patient")
    @ResponseBody
    public Object updatePatient(@PathVariable("patientUuid") String patientUuid, @RequestBody SimpleObject post,
                                HttpServletResponse response)
            throws Exception {
        try {
            validatePost(post);
            BahmniPatient bahmniPatient = new BahmniPatient(post);
            bahmniPatient.setUuid(patientUuid);
            Patient patient = bahmniPatientService.updatePatient(bahmniPatient);
            return RestUtil.created(response, getPatientAsSimpleObject(patient));
        } catch (Exception e) {
            logger.error("Update patient failed", e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{patientUuid}/image")
    @WSDoc("Update patient image")
    @ResponseBody
    public Object updatePatientImage(@PathVariable("patientUuid") String patientUuid, @RequestBody SimpleObject post,
                                     HttpServletResponse response)
            throws Exception {
        try {
            bahmniPatientService.updateImage(patientUuid, (String) post.get("image"));
            return RestUtil.noContent(response);
        } catch (Exception e) {
            logger.error("Update patient image failed", e);
            throw e;
        }
    }

    private List<SimpleObject> createListResponse(ResultList resultList) {
        List<SimpleObject> patientList = new ArrayList<>();

        for (Object patientObject : resultList.getResults()) {
            SimpleObject patient = new SimpleObject();
            Object[] pObject = (Object[]) patientObject;
            patient.add("name", String.format("%s %s", pObject[0], pObject[1]));
            patient.add("identifier", pObject[2]);
            patient.add("uuid", String.valueOf(pObject[3]));
            patient.add("visitUuid", String.valueOf(pObject[4]));
            patientList.add(patient);
        }
        return patientList;
    }

    private Object respondNotCreated(HttpServletResponse response, Exception e) {
        logger.error("Patient create failed", e);
        SimpleObject obj = new SimpleObject();
        obj.add("exception", ExceptionUtils.getFullStackTrace(e));
        if (e instanceof ApplicationError) {
            ApplicationError applicationError = (ApplicationError) e;
            int errorCode = applicationError.getErrorCode();
            int statusCode = ErrorCode.duplicationError(errorCode) ? HttpServletResponse.SC_CONFLICT : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            response.setStatus(statusCode);
            Throwable cause = applicationError.getCause() == null ? applicationError : applicationError.getCause();
            obj.add("error", new SimpleObject().add("code", errorCode).add("message", cause.getMessage()));
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        if (e instanceof BillingSystemException) {
            BillingSystemException billingSystemException = (BillingSystemException) e;
            obj.add("patient", getPatientAsSimpleObject(billingSystemException.getPatient()));
        }
        return obj;
    }

    private SimpleObject respondCreated(HttpServletResponse response, BahmniPatient bahmniPatient, Patient patient) {
        response.setStatus(HttpServletResponse.SC_CREATED);
        SimpleObject obj = new SimpleObject();
        obj.add("uuid", patient == null ? null : patient.getUuid());
        obj.add("name", bahmniPatient.getPatientName());
        obj.add("identifier", patient == null ? bahmniPatient.getIdentifier() : patient.getPatientIdentifier().toString());
        return obj;
    }

    private boolean validatePost(SimpleObject post) {
        List<String> missingFields = new ArrayList<>();
        for (String REQUIRED_FIELD : REQUIRED_FIELDS) {
            if (post.get(REQUIRED_FIELD) == null) {
                missingFields.add(REQUIRED_FIELD);
            }
        }
        if (missingFields.size() > 0)
            throw new BahmniCoreException("Required field " + ArrayUtils.toString(missingFields) + " not found");
        return true;
    }

    private SimpleObject getPatientAsSimpleObject(Patient p) {
        SimpleObject obj = new SimpleObject();
        obj.add("uuid", p.getUuid());
        obj.add("name", p.getGivenName() + " " + p.getFamilyName());
        obj.add("identifier", p.getPatientIdentifier().getIdentifier());
        return obj;
    }
}
