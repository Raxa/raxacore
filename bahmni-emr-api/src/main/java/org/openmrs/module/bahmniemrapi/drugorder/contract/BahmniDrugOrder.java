package org.openmrs.module.bahmniemrapi.drugorder.contract;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.FreeTextDosingInstructions;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.module.bahmniemrapi.visit.contract.VisitData;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.Date;


public class BahmniDrugOrder {

    private VisitData visit;
    private EncounterTransaction.DrugOrder drugOrder;
    private EncounterTransaction.Provider provider;

    public String getAction() {
        return drugOrder.getAction();
    }

    public Date getAutoExpireDate() {
        return drugOrder.getAutoExpireDate();
    }

    public String getCareSetting() {
        return drugOrder.getCareSetting();
    }

    public String getCommentToFulfiller() {
        return drugOrder.getCommentToFulfiller();
    }

    public Date getDateActivated() {
        return drugOrder.getDateActivated();
    }

    public Date getDateStopped() {
        return drugOrder.getDateStopped();
    }

    public EncounterTransaction.DosingInstructions getDosingInstructions() {
        return drugOrder.getDosingInstructions();
    }

    public String getDosingInstructionType() {
        return drugOrder.getDosingInstructionType();
    }

    public EncounterTransaction.Drug getDrug() {
        return drugOrder.getDrug();
    }

    public Integer getDuration() {
        return drugOrder.getDuration();
    }

    public String getDurationUnits() {
        return drugOrder.getDurationUnits();
    }

    public Date getEffectiveStartDate() {
        return drugOrder.getEffectiveStartDate();
    }

    public Date getEffectiveStopDate() {
        return drugOrder.getEffectiveStopDate();
    }

    public String getPreviousOrderUuid() {
        return drugOrder.getPreviousOrderUuid();
    }

    public String getInstructions() {
        return drugOrder.getInstructions();
    }

    public EncounterTransaction.Concept getOrderReasonConcept() {
        return drugOrder.getOrderReasonConcept();
    }

    public String getOrderReasonText() {
        return drugOrder.getOrderReasonText();
    }

    public String getOrderType() {
        return drugOrder.getOrderType();
    }

    public Date getScheduledDate() {
        return drugOrder.getScheduledDate();
    }

    public String getUuid() {
        return drugOrder.getUuid();
    }

    public void setVisit(Visit visit) {
        this.visit = new VisitData(visit);
    }

    public VisitData getVisit() {
        return visit;
    }

    public void setDrugOrder(EncounterTransaction.DrugOrder drugOrder) {
        this.drugOrder = drugOrder;
    }

    public void setProvider(EncounterTransaction.Provider provider) {
        this.provider = provider;
    }

    public EncounterTransaction.Provider getProvider() {
        return provider;
    }

    public String getOrderNumber() {
        return drugOrder.getOrderNumber();
    }
}