package org.openmrs.module.bahmnicore.web.v1_0.mapper;

import org.bahmni.module.bahmnicore.model.BahmniDrugOrder;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.DrugOrder;
import org.openmrs.FreeTextDosingInstructions;
import org.openmrs.SimpleDosingInstructions;

import java.io.IOException;
import java.util.*;

public class DrugOrderMapper {


    public List<BahmniDrugOrder> mapToResponse(List<DrugOrder> activeDrugOrders) throws IOException {
        List<BahmniDrugOrder> bahmniDrugOrders = new ArrayList<>();
        for (DrugOrder drugOrder : activeDrugOrders) {
            BahmniDrugOrder bahmniDrugOrder = new BahmniDrugOrder();
            bahmniDrugOrder.setDrugName(drugOrder.getDrug().getName());
            bahmniDrugOrder.setDrugForm(drugOrder.getDrug().getDosageForm().getName().getName());
            bahmniDrugOrder.setEffectiveStopDate(drugOrder.getEffectiveStopDate());
            bahmniDrugOrder.setEffectiveStartDate(drugOrder.getEffectiveStartDate());

            bahmniDrugOrder.setVisit(drugOrder.getEncounter().getVisit());

            if(drugOrder.getDosingType().equals(SimpleDosingInstructions.class)){
                populateSimpleOrderDetails(drugOrder, bahmniDrugOrder);
            }
            else if (drugOrder.getDosingType() == FreeTextDosingInstructions.class) {
                populateFreeTextOrderDetails(drugOrder, bahmniDrugOrder);
            }

            if(drugOrder.getDuration()!=null){
                bahmniDrugOrder.setDuration(drugOrder.getDuration());
            }
            else if (drugOrder.getEffectiveStopDate() != null) {    //TODO: move out logic of calculating duration after adding migration to add duration in database.
                DateTime stopDate = new DateTime(drugOrder.getEffectiveStopDate());
                DateTime startDate = new DateTime(drugOrder.getEffectiveStartDate());
                bahmniDrugOrder.setDuration(Days.daysBetween(startDate, stopDate).getDays());
            }

            bahmniDrugOrders.add(bahmniDrugOrder);
        }
        return bahmniDrugOrders;
    }

    private void populateSimpleOrderDetails(DrugOrder drugOrder,BahmniDrugOrder bahmniDrugOrder) throws IOException {
        bahmniDrugOrder.setDose(drugOrder.getDose());
        bahmniDrugOrder.setDosingInstructionsFrom(drugOrder.getDosingInstructions());
        bahmniDrugOrder.setDoseUnits(drugOrder.getDoseUnits().getName().getName());
        bahmniDrugOrder.setFrequency(drugOrder.getFrequency().getName());
        bahmniDrugOrder.setRoute(drugOrder.getRoute().getName().getName());
        bahmniDrugOrder.setDurationUnits(drugOrder.getDurationUnits().getName().getName());
    }

    private void populateFreeTextOrderDetails(DrugOrder drugOrder,BahmniDrugOrder bahmniDrugOrder) throws IOException {
        if(drugOrder.getDosingInstructions() != null){              //TODO: move out logic of calculating dose and dose units after adding migration to add them in database.
            String instructions = drugOrder.getDosingInstructions();
            String[] splittedInstructions = instructions.split("\\s+");  //Assuming dosing instructions for historic freetextOrders is containing dose and dose units for JSS records.
            bahmniDrugOrder.setDose(Double.parseDouble(splittedInstructions[0]));
            bahmniDrugOrder.setDoseUnits(splittedInstructions[1]);
        }
        bahmniDrugOrder.setDurationUnits("Days");       //TODO: default durationUnits to Days through migration.
    }
}