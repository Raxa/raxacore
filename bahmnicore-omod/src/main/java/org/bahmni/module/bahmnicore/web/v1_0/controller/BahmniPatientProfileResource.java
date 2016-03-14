package org.bahmni.module.bahmnicore.web.v1_0.controller;


import org.bahmni.module.bahmnicore.mapper.PatientProfileMapper;
import org.hibernate.NonUniqueObjectException;
import org.openmrs.api.ValidationException;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.emrapi.patient.EmrPatientProfileService;
import org.openmrs.module.emrapi.patient.PatientProfile;
import org.openmrs.module.idgen.webservices.services.IdentifierSourceServiceWrapper;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Controller for REST web service access to
 * the Search resource.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/patientprofile")
public class BahmniPatientProfileResource extends DelegatingCrudResource<PatientProfile> {

    private PatientProfileMapper patientProfileMapper;
    private EmrPatientProfileService emrPatientProfileService;
    private IdentifierSourceServiceWrapper identifierSourceServiceWrapper;

    @Autowired
    public BahmniPatientProfileResource(PatientProfileMapper patientProfileMapper, EmrPatientProfileService emrPatientProfileService, IdentifierSourceServiceWrapper identifierSourceServiceWrapper) {
        this.patientProfileMapper = patientProfileMapper;
        this.emrPatientProfileService = emrPatientProfileService;
        this.identifierSourceServiceWrapper = identifierSourceServiceWrapper;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> create(@RequestHeader(value = "Jump-Accepted", required = false) boolean jumpAccepted, @RequestBody SimpleObject propertiesToCreate) throws Exception {
        LinkedHashMap identifierProperties = (LinkedHashMap) ((ArrayList) ((LinkedHashMap) propertiesToCreate.get("patient")).get("identifiers")).get(0);
        String identifierPrefix = String.valueOf(identifierProperties.get("identifierPrefix"));
        identifierProperties.remove("identifierPrefix");
        String identifier;
        if (identifierProperties.get("registrationNumber") != null) {
            long givenRegistrationNumber = Long.parseLong(String.valueOf(identifierProperties.get("registrationNumber")));
            if (!jumpAccepted) {
                long latestRegistrationNumber = Long.parseLong(identifierSourceServiceWrapper.getSequenceValue(identifierPrefix));
                long sizeOfJump = givenRegistrationNumber - latestRegistrationNumber;
                if (sizeOfJump > 0) {
                    return new ResponseEntity<Object>("{\"sizeOfJump\":" + sizeOfJump + "}", HttpStatus.PRECONDITION_FAILED);
                } else if (sizeOfJump < 0) {
                    return new ResponseEntity<Object>("Given identifier is less than the last generated identifier : " + latestRegistrationNumber, HttpStatus.BAD_REQUEST);
                }
            }
            identifier = identifierPrefix + givenRegistrationNumber;
            identifierSourceServiceWrapper.saveSequenceValue(givenRegistrationNumber + 1, identifierPrefix);
        } else {
            identifier = identifierSourceServiceWrapper.generateIdentifier(identifierPrefix, "");
        }
        identifierProperties.remove("registrationNumber");
        identifierProperties.put("identifier", identifier);

        PatientProfile delegate = patientProfileMapper.mapForCreatePatient(propertiesToCreate);
        setConvertedProperties(delegate, propertiesToCreate, getCreatableProperties(), true);
        try {
            delegate = emrPatientProfileService.save(delegate);
            return new ResponseEntity<>(ConversionUtil.convertToRepresentation(delegate, Representation.FULL), HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof ContextAuthenticationException) {
                return new ResponseEntity<Object>(e, HttpStatus.FORBIDDEN);
            } else if (e instanceof NonUniqueObjectException) {
                return new ResponseEntity<Object>(e.getMessage(), HttpStatus.OK);
            }  else if (e instanceof ValidationException) {
                return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{uuid}")
    @ResponseBody
    public ResponseEntity<Object> update(@PathVariable("uuid") String uuid, @RequestBody SimpleObject propertiesToCreate) throws Exception {
        PatientProfile delegate = patientProfileMapper.mapForUpdatePatient(uuid, propertiesToCreate);
        setConvertedProperties(delegate, propertiesToCreate, getUpdatableProperties(), true);
        try {
            delegate = emrPatientProfileService.save(delegate);
            return new ResponseEntity<>(ConversionUtil.convertToRepresentation(delegate, Representation.FULL), HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof ContextAuthenticationException) {
                return new ResponseEntity<Object>(e, HttpStatus.FORBIDDEN);
            } else if (e instanceof ValidationException) {
                return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<Object>(e, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Override
    public PatientProfile getByUniqueId(String s) {
        return null;
    }

    @Override
    protected void delete(PatientProfile patientProfile, String s, RequestContext requestContext) throws ResponseException {

    }

    @Override
    public PatientProfile newDelegate() {
        return null;
    }

    @Override
    public PatientProfile save(PatientProfile patientProfile) {
        return null;
    }

    @Override
    public void purge(PatientProfile patientProfile, RequestContext requestContext) throws ResponseException {

    }

    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("patient", Representation.DEFAULT);
        description.addProperty("image", Representation.DEFAULT);
        description.addProperty("relationships", Representation.DEFAULT);
        return description;
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
        return null;
    }
}
