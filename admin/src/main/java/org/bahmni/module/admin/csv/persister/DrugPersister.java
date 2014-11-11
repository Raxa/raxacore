package org.bahmni.module.admin.csv.persister;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.concepts.mapper.DrugMapper;
import org.bahmni.module.admin.csv.models.DrugRow;
import org.bahmni.module.referencedata.labconcepts.contract.Drug;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataDrugService;
import org.openmrs.api.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DrugPersister implements EntityPersister<DrugRow> {
    @Autowired
    private ReferenceDataDrugService referenceDataDrugService;

    @Override
    public RowResult<DrugRow> validate(DrugRow drugRow) {
        StringBuilder error = new StringBuilder();
        if (StringUtils.isEmpty(drugRow.getName())) {
            error.append("Drug name not specified\n");
        }
        if (StringUtils.isEmpty(drugRow.getGenericName())) {
            error.append("Drug generic name not specified\n");
        }
        return new RowResult<>(new DrugRow(), error.toString());
    }

    @Override
    public RowResult<DrugRow> persist(DrugRow drugRow) {
        Drug drug = new DrugMapper().map(drugRow);
        referenceDataDrugService.saveDrug(drug);
        return new RowResult<>(drugRow);
    }
}