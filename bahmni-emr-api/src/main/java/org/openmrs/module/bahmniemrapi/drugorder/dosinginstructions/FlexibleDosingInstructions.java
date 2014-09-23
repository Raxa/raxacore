package org.openmrs.module.bahmniemrapi.drugorder.dosinginstructions;

import org.openmrs.DosingInstructions;
import org.openmrs.DrugOrder;
import org.openmrs.Duration;
import org.openmrs.api.APIException;
import org.springframework.validation.Errors;

import java.util.Date;
import java.util.Locale;

public class FlexibleDosingInstructions implements DosingInstructions {

    @Override
    public String getDosingInstructionsAsString(Locale locale) {
        return null;
    }

    @Override
    public void setDosingInstructions(DrugOrder order) {
        order.setDosingType(this.getClass());
    }

    @Override
    public DosingInstructions getDosingInstructions(DrugOrder order) {
        if (!order.getDosingType().equals(this.getClass())) {
            throw new APIException("Dosing type of drug order is mismatched. Expected:" + this.getClass() + " but received:"
                    + order.getDosingType());
        }
        return new FlexibleDosingInstructions();
    }

    @Override
    public void validate(DrugOrder order, Errors errors) {

    }

    @Override
    public Date getAutoExpireDate(DrugOrder drugOrder) {
        if (drugOrder.getDuration() == null || drugOrder.getDurationUnits() == null) {
            return null;
        }
        if (drugOrder.getNumRefills() != null && drugOrder.getNumRefills() > 0) {
            return null;
        }
        String durationCode = Duration.getCode(drugOrder.getDurationUnits());
        if (durationCode == null) {
            return null;
        }
        Duration duration = new Duration(drugOrder.getDuration(), durationCode);
        return duration.addToDate(drugOrder.getEffectiveStartDate(), drugOrder.getFrequency());
    }
}
