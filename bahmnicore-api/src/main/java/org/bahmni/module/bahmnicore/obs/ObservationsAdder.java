package org.bahmni.module.bahmnicore.obs;

import org.openmrs.Obs;

import java.util.List;

public interface ObservationsAdder {

    void addObservations(List<Obs> observations, List<String> conceptNames);
}
