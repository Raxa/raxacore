package org.bahmni.module.bahmnicore.model;

import java.io.IOException;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.bahmni.module.bahmnicore.util.CustomDateSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Visit;



public class BahmniDrugOrder {

    @Getter @Setter
    private String drugName;
    @Getter @Setter
    private double dose;
    @Getter @Setter
    private String drugForm;
    @Setter
    private Date effectiveStopDate;
    @Setter
    private Date effectiveStartDate;
    @Getter @Setter
    private String doseUnits;
    @Getter @Setter
    private double duration;
    @Getter @Setter
    private String durationUnits;
    @Getter @Setter
    private String route;
    @Getter @Setter
    private String frequency;
    @Getter @Setter
    private VisitData visit;
    @Getter @Setter
    private BahmniDosingInstructions dosingInstructions;

    public void setDosingInstructionsFrom(String instructions) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        dosingInstructions = mapper.readValue(instructions,BahmniDosingInstructions.class);
    }

    public void setVisit(Visit visit) {
        this.visit = new VisitData(visit);
    }

    public String getEffectiveStartDate(){
        return CustomDateSerializer.serializeDate(this.effectiveStartDate);
    }

    public String getEffectiveStopDate(){
        return CustomDateSerializer.serializeDate(this.effectiveStopDate);
    }

}