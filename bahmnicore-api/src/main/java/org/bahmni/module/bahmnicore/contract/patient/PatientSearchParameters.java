package org.bahmni.module.bahmnicore.contract.patient;

import lombok.Data;
import org.openmrs.module.webservices.rest.web.RequestContext;

@Data
public class PatientSearchParameters {
    private String identifier;
    private String name;
    private String cityVillage;
    private Integer start;
    private Integer length;
    private String localName;

    public PatientSearchParameters(RequestContext context) {
        String query = context.getParameter("q");
        if (query.matches(".*\\d+.*")) {
            this.setIdentifier(query);
        } else {
            this.setName(query);
        }
        this.setStart(context.getStartIndex());
        this.setLength(context.getLimit());
        this.setLocalName(context.getParameter("local_name"));
        this.setCityVillage(context.getParameter("city_village"));
    }
}
