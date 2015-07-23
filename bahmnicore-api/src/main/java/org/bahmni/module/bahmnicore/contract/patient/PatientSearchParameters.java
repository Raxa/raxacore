package org.bahmni.module.bahmnicore.contract.patient;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.webservices.rest.web.RequestContext;

import java.util.Map;

@Data
public class PatientSearchParameters {
    private String identifier;
    private String name;
    private String addressFieldName;
    private String addressFieldValue;
    private Integer start;
    private Integer length;
    private String customAttribute;
    private String[] patientAttributes;

    public PatientSearchParameters(RequestContext context) {
        String query = context.getParameter("q");
        String identifier = context.getParameter("identifier");
        if (identifier != null) {
            this.setIdentifier(identifier);
        } else if (query != null) {
            if (query.matches(".*\\d+.*")) {
                this.setIdentifier(query);
            } else {
                this.setName(query);
            }
        }
        this.setStart(context.getStartIndex());
        this.setLength(context.getLimit());
        this.setCustomAttribute(context.getParameter("custom_attribute"));
        String addressFieldNameFromRequest = context.getParameter("address_field_name");
        if  (StringUtils.isNotEmpty(addressFieldNameFromRequest)){
            this.setAddressFieldName(addressFieldNameFromRequest);
        } else {
            this.setAddressFieldName("city_village");
        }
        this.setAddressFieldValue(context.getParameter("address_field_value"));
        Map parameterMap = context.getRequest().getParameterMap();
        this.patientAttributes = (String[]) parameterMap.get("patientAttributes");
    }
}
