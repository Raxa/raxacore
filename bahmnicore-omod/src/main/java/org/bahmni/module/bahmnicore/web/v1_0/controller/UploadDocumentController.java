package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.model.VisitDocumentUpload;
import org.bahmni.module.bahmnicore.service.UploadDocumentService;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/uploadDocument")
public class UploadDocumentController extends BaseRestController {

    @Autowired
    private UploadDocumentService uploadDocumentService;

    @RequestMapping(method = RequestMethod.POST)
    @WSDoc("Save Patient Document")
    @ResponseBody
    public void save(@RequestBody SimpleObject post) {
        try {
            VisitDocumentUpload visitDocumentUpload = new VisitDocumentUpload(post);
            uploadDocumentService.upload(visitDocumentUpload);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (APIAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
