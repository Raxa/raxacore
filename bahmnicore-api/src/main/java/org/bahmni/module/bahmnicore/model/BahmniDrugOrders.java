/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.bahmni.module.bahmnicore.model;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class BahmniDrugOrders extends ArrayList<BahmniDrugOrder> {

    public BahmniDrugOrders(List<BahmniDrugOrder> bahmniDrugOrders) {
        super(bahmniDrugOrders);
    }

    public BahmniDrugOrders() {
        super();
    }

    public BahmniDrugOrders getUniqueOrders() {
        BahmniDrugOrders uniqueDrugOrders = new BahmniDrugOrders();
        for (BahmniDrugOrder drugOrder: this) {
            BahmniDrugOrder existingDrugOrder = uniqueDrugOrders.findOrderByUuid(drugOrder.getProductUuid());
            if(existingDrugOrder == null) {
                uniqueDrugOrders.add(drugOrder);
            } else {
                double averageDosage = (existingDrugOrder.getDosage() + drugOrder.getDosage()) / 2;
                int totalNumberOfDays = existingDrugOrder.getNumberOfDays() + drugOrder.getNumberOfDays();
                double totalQuantity = existingDrugOrder.getQuantity() + drugOrder.getQuantity();
                existingDrugOrder.setDosage(averageDosage);
                existingDrugOrder.setNumberOfDays(totalNumberOfDays);
                existingDrugOrder.setQuantity(totalQuantity);
            }
        }
        return uniqueDrugOrders;
    }

    public BahmniDrugOrder findOrderByUuid(String productUuid) {
        for (BahmniDrugOrder bahmniDrugOrder: this) {
            if(ObjectUtils.equals(bahmniDrugOrder.getProductUuid(), productUuid)) {
                return bahmniDrugOrder;
            }
        }
        return null;
    }
}
