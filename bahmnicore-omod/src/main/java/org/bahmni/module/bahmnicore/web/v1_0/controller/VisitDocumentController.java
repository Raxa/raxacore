package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.bahmnicore.model.DocumentImage;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.document.contract.VisitDocumentRequest;
import org.openmrs.module.bahmniemrapi.document.contract.VisitDocumentResponse;
import org.openmrs.module.bahmniemrapi.document.service.VisitDocumentService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VisitDocumentController extends BaseRestController {
    private final String baseVisitDocumentUrl = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/visitDocument";
    @Autowired
    private VisitDocumentService visitDocumentService;
    @Autowired
    private PatientImageService patientImageService;
    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;

    @RequestMapping(method = RequestMethod.POST, value = baseVisitDocumentUrl)
    @WSDoc("Save Patient Document")
    @ResponseBody
    public VisitDocumentResponse save(@RequestBody VisitDocumentRequest visitDocumentUpload) {
        final Visit visit = visitDocumentService.upload(visitDocumentUpload);
        return new VisitDocumentResponse(visit.getUuid());
    }

    @RequestMapping(method = RequestMethod.POST, value = baseVisitDocumentUrl + "/uploadImage")
    @ResponseBody
    public String saveImage(@RequestBody DocumentImage image) {
        Patient patient = Context.getPatientService().getPatientByUuid(image.getPatientUuid());
        String encounterTypeName = image.getEncounterTypeName();
        if (StringUtils.isEmpty(encounterTypeName)) {
            encounterTypeName = administrationService.getGlobalProperty("bahmni.encounterType.default");
        }
        return patientImageService.saveDocument(patient.getId(), encounterTypeName, image.getImage(), image.getFormat());
    }
}
