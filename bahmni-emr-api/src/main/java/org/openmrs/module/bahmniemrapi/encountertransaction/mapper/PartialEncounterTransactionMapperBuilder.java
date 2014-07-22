package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.openmrs.module.emrapi.encounter.DrugOrderMapper;
import org.openmrs.module.emrapi.encounter.EncounterObservationsMapper;
import org.openmrs.module.emrapi.encounter.EncounterOrdersMapper;
import org.openmrs.module.emrapi.encounter.EncounterProviderMapper;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.TestOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PartialEncounterTransactionMapperBuilder {

    private PartialEncounterTransactionMapper encounterTransactionMapper;
    private final TestOrderMapper testOrderMapper;
    private final DrugOrderMapper drugOrderMapper;
    private EncounterProviderMapper encounterProviderMapper;

    @Autowired
    public PartialEncounterTransactionMapperBuilder(EncounterObservationsMapper encounterObservationsMapper, TestOrderMapper testOrderMapper,
                                                    DrugOrderMapper drugOrderMapper, EncounterProviderMapper encounterProviderMapper){
        this.testOrderMapper = testOrderMapper;
        this.drugOrderMapper = drugOrderMapper;
        this.encounterProviderMapper = encounterProviderMapper;
        encounterTransactionMapper = new PartialEncounterTransactionMapper(encounterObservationsMapper);
    }

    public EncounterTransactionMapper build(){
        return encounterTransactionMapper;
    }

    public PartialEncounterTransactionMapperBuilder withOrderMapper(){
        encounterTransactionMapper.setEncounterOrdersMapper(new EncounterOrdersMapper(testOrderMapper,drugOrderMapper));
        return this;
    }

    public PartialEncounterTransactionMapperBuilder withProviderMapper(){
        encounterTransactionMapper.setEncounterProviderMapper(encounterProviderMapper);
        return this;
    }

}
