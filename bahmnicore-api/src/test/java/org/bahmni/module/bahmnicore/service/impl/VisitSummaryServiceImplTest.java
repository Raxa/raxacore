package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.mapper.builder.EncounterTransactionMapperBuilder;
import org.bahmni.module.bahmnicore.service.VisitSummaryService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.api.ConceptService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.diagnosis.DiagnosisMetadata;
import org.openmrs.module.emrapi.encounter.DiagnosisMapper;
import org.openmrs.module.emrapi.encounter.DispositionMapper;
import org.openmrs.module.emrapi.encounter.EncounterObservationsMapper;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.ObservationMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VisitSummaryServiceImplTest  {

    @Mock
    VisitService visitService;
    private ConceptService conceptService;
    private String visitUUID;
    private EncounterObservationsMapper encounterObservationsMapper;
    @Mock
    private DiagnosisMetadata diagnosisMetadata;
    @Mock
    private ObservationMapper observationMapper;
    @Mock
    private DiagnosisMapper diagnosisMapper;
    @Mock
    private DispositionMapper dispositionMapper;
    @Mock
    private EmrApiProperties emrApiProperties;

    @Autowired
    private EncounterTransactionMapperBuilder transactionMapperBuilder;

    VisitSummaryService visitSummaryService;


    @Before
    public void setUp(){
        initMocks(this);
        visitSummaryService = new VisitSummaryServiceImpl(visitService,transactionMapperBuilder);
        visitUUID = "12345";

//        encounterObservationsMapper = new EncounterObservationsMapper(observationMapper, diagnosisMapper, dispositionMapper, emrApiProperties);
    }

    @Ignore
    @Test
    public void shouldGetSummaryWithDiagnosesAndDisposition() throws Exception {
        EncounterTransactionMapper transactionMapper = new EncounterTransactionMapper(null,null,null);
        when(transactionMapperBuilder.withProviderMapper().build()).thenReturn(transactionMapper);


        List<EncounterTransaction> visitSummary = visitSummaryService.getVisitSummary(visitUUID);
        verify(transactionMapperBuilder).withProviderMapper();
        verify(transactionMapperBuilder).build();

    }

}





