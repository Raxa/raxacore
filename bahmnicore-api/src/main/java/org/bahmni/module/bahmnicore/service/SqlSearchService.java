package org.bahmni.module.bahmnicore.service;

import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.List;
import java.util.Map;

public interface SqlSearchService {

    public List<SimpleObject> search(String sqlQuery, Map<String, String[]> params);

}
