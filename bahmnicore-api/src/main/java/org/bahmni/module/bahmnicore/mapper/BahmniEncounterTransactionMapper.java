package org.bahmni.module.bahmnicore.mapper;

import org.openmrs.module.emrapi.encounter.EncounterObservationsMapper;
import org.openmrs.module.emrapi.encounter.EncounterOrdersMapper;
import org.openmrs.module.emrapi.encounter.EncounterProviderMapper;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;

public class BahmniEncounterTransactionMapper extends EncounterTransactionMapper{
    public BahmniEncounterTransactionMapper(EncounterObservationsMapper encounterObservationsMapper, EncounterOrdersMapper encounterOrdersMapper, EncounterProviderMapper encounterProviderMapper) {
        super(encounterObservationsMapper, encounterOrdersMapper, encounterProviderMapper);
    }

    public BahmniEncounterTransactionMapper(EncounterObservationsMapper encounterObservationsMapper) {
        this(encounterObservationsMapper,new EncounterOrdersMapper.EmptyEncounterOrdersMapper(null,null),new EncounterProviderMapper.EmptyEncounterProviderMapper());
    }

    public void setEncounterOrdersMapper(EncounterOrdersMapper encounterOrdersMapper) {
        this.encounterOrdersMapper = encounterOrdersMapper;
    }

    public void setEncounterProviderMapper(EncounterProviderMapper encounterProviderMapper) {
        this.encounterProviderMapper = encounterProviderMapper;
    }

}
