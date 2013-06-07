package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.BahmniCoreException;
import org.bahmni.module.bahmnicore.mapper.PatientMapper;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.service.BahmniPatientListService;
import org.openmrs.Location;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BahmniPatientListServiceImpl implements BahmniPatientListService {

    private VisitService visitService;
    private LocationService locationService;
    private PatientMapper mapper;

    @Autowired
    public BahmniPatientListServiceImpl(PatientMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<BahmniPatient> getAllActivePatients(String location) {
        Location visitLocation = getLocationService().getLocation(location);
        if (visitLocation == null) {
            throw new BahmniCoreException("Could not find location : "+location);
        }

        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(visitLocation);

        List<Visit> visits = getVisitService().getVisits(null, null, locations, null, null, null, null, null, null, false, false);
        List<BahmniPatient> patients = getPatientsForVisit(visits);

        return patients;
    }


    private List<BahmniPatient> getPatientsForVisit(List<Visit> visits) {
        List<BahmniPatient> bahmniPatients = new ArrayList<BahmniPatient>();
        for (Visit visit : visits) {
            bahmniPatients.add(mapper.mapFromPatient(null, visit.getPatient()));
        }
        return bahmniPatients;
    }

    private VisitService getVisitService() {
        if (visitService == null) {
            visitService = Context.getVisitService();
        }
        return visitService;
    }

    private LocationService getLocationService() {
        if (locationService == null) {
            locationService = Context.getLocationService();
        }
        return locationService;
    }


    void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }
}
