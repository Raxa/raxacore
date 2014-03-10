package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.mapper.builder.EncounterTransactionMapperBuilder;
import org.bahmni.module.bahmnicore.service.VisitSummaryService;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VisitSummaryServiceImpl implements VisitSummaryService{

    private VisitService visitService;
    EncounterTransactionMapperBuilder encounterTransactionMapperBuilder;

    public VisitSummaryServiceImpl() {
    }

    @Autowired
    public VisitSummaryServiceImpl(VisitService visitService,EncounterTransactionMapperBuilder encounterTransactionMapperBuilder) {
        this.visitService = visitService;
        this.encounterTransactionMapperBuilder = encounterTransactionMapperBuilder;
    }

    public List<EncounterTransaction> getVisitSummary(String visitUUID, Boolean includeAll){
        Visit visit = visitService.getVisitByUuid(visitUUID);
        EncounterTransactionMapper encounterTransactionMapper = encounterTransactionMapperBuilder.withProviderMapper().withOrderMapper().build();
        List<EncounterTransaction> encounterTransactions = new ArrayList<EncounterTransaction>();
        for(Encounter encounter : visit.getEncounters()){
            encounterTransactions.add(encounterTransactionMapper.map(encounter, includeAll));
        }
        return encounterTransactions;
    }

}
