package org.bahmni.openerp.web.request.builder;

import java.util.List;

import static java.util.Arrays.asList;

public class OpenERPRequestTestHelper {
    public OpenERPRequestTestHelper() {
    }

    Parameter createParameter(String name, String value, String type) {
        return new Parameter(name, value, type);
    }

    public List<Parameter> createCustomerRequest(String patientName, String patientId, String village) {
        return asList(createParameter("name", patientName, "string"),
                createParameter("ref", patientId, "string"),
                createParameter("village", village, "string"));
    }

}