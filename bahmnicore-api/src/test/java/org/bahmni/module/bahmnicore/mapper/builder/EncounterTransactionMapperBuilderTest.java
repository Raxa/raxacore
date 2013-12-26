package org.bahmni.module.bahmnicore.mapper.builder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.diagnosis.DiagnosisMetadata;
import org.openmrs.module.emrapi.encounter.DiagnosisMapper;
import org.openmrs.module.emrapi.encounter.DispositionMapper;
import org.openmrs.module.emrapi.encounter.DrugOrderMapper;
import org.openmrs.module.emrapi.encounter.EncounterObservationsMapper;
import org.openmrs.module.emrapi.encounter.EncounterProviderMapper;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.ObservationMapper;
import org.openmrs.module.emrapi.encounter.TestOrderMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EncounterTransactionMapperBuilderTest {

    EncounterTransactionMapperBuilder transactionMapperBuilder;
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
    @Mock
    private ConceptService conceptService;

    @Before
    public void setUp(){
        initMocks(this);
        when(emrApiProperties.getDiagnosisMetadata()).thenReturn(diagnosisMetadata);
    }

    @Test
    public void shouldMapDiagnosesAndDispositionsWithoutOrders(){
        Obs obs1 = new Obs(1);
        Obs obs2 = new Obs(2);
        Obs obs3 = new Obs(3);
        Obs obs4 = new Obs(4);
        HashSet<Obs> allObs = new HashSet<Obs>(Arrays.asList(obs1, obs2, obs3, obs4));

        Order testOrder1 = new TestOrderBuilder().withId(1).build();
        Order testOrder2 = new TestOrderBuilder().withId(2).build();
        DrugOrder drugOrder1 = new DrugOrderBuilder().withId(1).build();
        DrugOrder drugOrder2 = new DrugOrderBuilder().withId(2).build();
        HashSet<Order> orders = new HashSet<Order>(Arrays.asList(testOrder1, drugOrder1, testOrder2, drugOrder2));

        when(diagnosisMetadata.isDiagnosis(obs1)).thenReturn(true);
        when(diagnosisMetadata.isDiagnosis(obs2)).thenReturn(false);
        when(diagnosisMetadata.isDiagnosis(obs3)).thenReturn(true);

        EncounterObservationsMapper observationsMapper = new EncounterObservationsMapper(observationMapper, diagnosisMapper,
                dispositionMapper, emrApiProperties);
        transactionMapperBuilder = new EncounterTransactionMapperBuilder(observationsMapper,null,null,new EncounterProviderMapper());
        EncounterTransactionMapper encounterTransactionMapper = transactionMapperBuilder.withProviderMapper().build();

        Encounter encounter = new EncounterBuilder().build();
        encounter.setOrders(orders);
        encounter.setObs(allObs);
        EncounterTransaction.Disposition disposition = new EncounterTransaction.Disposition();
        when(dispositionMapper.isDispositionGroup(obs4)).thenReturn(true);
        when(dispositionMapper.getDisposition(obs4)).thenReturn(disposition);

        EncounterTransaction encounterTransaction = encounterTransactionMapper.map(encounter, true);
        Assert.assertEquals(2, encounterTransaction.getDiagnoses().size());
        Assert.assertEquals(disposition, encounterTransaction.getDisposition());
        Assert.assertEquals(1, encounterTransaction.getObservations().size());
        Assert.assertEquals(0,encounterTransaction.getDrugOrders().size());
        Assert.assertEquals(0,encounterTransaction.getTestOrders().size());

    }

    @Ignore
    public void shouldMapDiagnosesAndDispositionsWithOrders(){
        Obs obs1 = new Obs(1);
        Obs obs2 = new Obs(2);
        Obs obs3 = new Obs(3);
        Obs obs4 = new Obs(4);

        HashSet<Obs> allObs = new HashSet<Obs>(Arrays.asList(obs1, obs2, obs3, obs4));

        Order testOrder1 = new TestOrderBuilder().withId(1).build();
        Order testOrder2 = new TestOrderBuilder().withId(2).build();
        DrugOrder drugOrder1 = new DrugOrderBuilder().withId(1).build();
        DrugOrder drugOrder2 = new DrugOrderBuilder().withId(2).build();
        HashSet<Order> orders = new HashSet<Order>(Arrays.asList(testOrder1, drugOrder1, testOrder2, drugOrder2));

        when(diagnosisMetadata.isDiagnosis(obs1)).thenReturn(true);
        when(diagnosisMetadata.isDiagnosis(obs2)).thenReturn(false);
        when(diagnosisMetadata.isDiagnosis(obs3)).thenReturn(true);

        EncounterObservationsMapper observationsMapper = new EncounterObservationsMapper(observationMapper, diagnosisMapper,
                dispositionMapper, emrApiProperties);
        transactionMapperBuilder = new EncounterTransactionMapperBuilder(observationsMapper,new TestOrderMapper(),new DrugOrderMapper(conceptService),new EncounterProviderMapper());
        EncounterTransactionMapper encounterTransactionMapper = transactionMapperBuilder.withProviderMapper().withOrderMapper().build();

        Encounter encounter = new EncounterBuilder().build();
        encounter.setOrders(orders);
        encounter.setObs(allObs);
        EncounterTransaction.Disposition disposition = new EncounterTransaction.Disposition();
        when(dispositionMapper.isDispositionGroup(obs4)).thenReturn(true);
        when(dispositionMapper.getDisposition(obs4)).thenReturn(disposition);

        EncounterTransaction encounterTransaction = encounterTransactionMapper.map(encounter, true);
        Assert.assertEquals(2, encounterTransaction.getDiagnoses().size());
        Assert.assertEquals(disposition, encounterTransaction.getDisposition());
        Assert.assertEquals(1, encounterTransaction.getObservations().size());
        Assert.assertEquals(2,encounterTransaction.getDrugOrders().size());
        Assert.assertEquals(2,encounterTransaction.getTestOrders().size());

    }

    private Obs getObs() {
        Obs obs = new Obs();
        obs.setObsDatetime(new Date());
        return obs;
    }

}
