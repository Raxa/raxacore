package org.bahmni.module.referencedata.web.controller;

import org.bahmni.module.referencedata.labconcepts.contract.Concepts;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptService;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ConceptsController extends BaseRestController {
    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;

    public ConceptsController() {
    }

    @RequestMapping(value = "/rest/v1/reference-data/concepts", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Concepts> create(@RequestParam(value = "conceptName", required = true) String conceptName) {
        try {
            Concepts concepts = referenceDataConceptService.getConcept(conceptName);
            return new ResponseEntity<>(concepts, HttpStatus.OK);
        } catch (Throwable error) {
            return new ResponseEntity<>(new Concepts(), HttpStatus.NOT_FOUND);
        }
    }
}
