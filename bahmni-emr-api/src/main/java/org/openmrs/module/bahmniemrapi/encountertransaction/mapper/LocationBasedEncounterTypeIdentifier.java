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
package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.EncounterType;
import org.openmrs.api.APIException;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmnimapping.services.BahmniLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationBasedEncounterTypeIdentifier {

    private BahmniLocationService bahmniLocationService;

    @Autowired
    public LocationBasedEncounterTypeIdentifier(BahmniLocationService bahmniLocationService) {
        this.bahmniLocationService = bahmniLocationService;
    }

    public void populateEncounterType(BahmniEncounterTransaction encounterTransaction) {
        if (StringUtils.isNotBlank(encounterTransaction.getEncounterTypeUuid()) || StringUtils.isNotBlank(encounterTransaction.getEncounterType())){
            return;
        }
        List<EncounterType> encounterTypes = bahmniLocationService.getEncounterTypes(encounterTransaction.getLocationUuid());
        if (encounterTypes.size() == 1) {
            encounterTransaction.setEncounterTypeUuid(encounterTypes.get(0).getUuid());
        }
        else if (encounterTypes.size() > 1){
            throw new APIException("The location is mapped to multiple encounter types. Please specify a encounter type for encounter");
        }
    }
}
