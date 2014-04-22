package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.visitDocument.VisitDocumentRequest;
import org.bahmni.module.bahmnicore.contract.visitDocument.VisitDocumentResponse;
import org.bahmni.module.bahmnicore.service.VisitDocumentService;
import org.openmrs.Visit;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/visitDocument")
public class VisitDocumentController extends BaseRestController {
    @Autowired
    private VisitDocumentService visitDocumentService;

    @RequestMapping(method = RequestMethod.POST)
    @WSDoc("Save Patient Document")
    @ResponseBody
    public VisitDocumentResponse save(@RequestBody VisitDocumentRequest visitDocumentUpload) {
        final Visit visit = visitDocumentService.upload(visitDocumentUpload);
        return new VisitDocumentResponse(visit.getUuid());
    }
}
