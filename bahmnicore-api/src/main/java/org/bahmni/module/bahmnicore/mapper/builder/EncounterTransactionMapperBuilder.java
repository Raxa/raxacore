package org.bahmni.module.bahmnicore.mapper.builder;

import org.bahmni.module.bahmnicore.mapper.BahmniEncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.DrugOrderMapper;
import org.openmrs.module.emrapi.encounter.EncounterObservationsMapper;
import org.openmrs.module.emrapi.encounter.EncounterOrdersMapper;
import org.openmrs.module.emrapi.encounter.EncounterProviderMapper;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.TestOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncounterTransactionMapperBuilder {

    private BahmniEncounterTransactionMapper encounterTransactionMapper;
    private final TestOrderMapper testOrderMapper;
    private final DrugOrderMapper drugOrderMapper;
    private EncounterProviderMapper encounterProviderMapper;

    @Autowired
    public EncounterTransactionMapperBuilder(EncounterObservationsMapper encounterObservationsMapper, TestOrderMapper testOrderMapper,
                                             DrugOrderMapper drugOrderMapper, EncounterProviderMapper encounterProviderMapper){
        this.testOrderMapper = testOrderMapper;
        this.drugOrderMapper = drugOrderMapper;
        this.encounterProviderMapper = encounterProviderMapper;
        encounterTransactionMapper = new BahmniEncounterTransactionMapper(encounterObservationsMapper);
    }

    public EncounterTransactionMapper build(){
        return encounterTransactionMapper;
    }

    public EncounterTransactionMapperBuilder withOrderMapper(){
        encounterTransactionMapper.setEncounterOrdersMapper(new EncounterOrdersMapper(testOrderMapper,drugOrderMapper));
        return this;
    }

    public EncounterTransactionMapperBuilder withProviderMapper(){
        encounterTransactionMapper.setEncounterProviderMapper(encounterProviderMapper);
        return this;
    }

}
