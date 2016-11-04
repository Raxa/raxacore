package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.hibernate.SessionFactory;
import org.openmrs.EncounterType;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.accessionnote.mapper.AccessionNotesMapper;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.diagnosis.helper.BahmniDiagnosisMetadata;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.OrderWithUrgency;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.parameters.AdditionalBahmniObservationFields;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BahmniEncounterTransactionMapper {
    private AccessionNotesMapper accessionNotesMapper;
    private BahmniDiagnosisMetadata bahmniDiagnosisMetadata;
    private ObsRelationshipMapper obsRelationshipMapper;
    private PatientService patientService;
    private EncounterService encounterService;
    private ETObsToBahmniObsMapper fromETObsToBahmniObs;
    private SessionFactory sessionFactory;

    @Autowired
    public BahmniEncounterTransactionMapper(AccessionNotesMapper accessionNotesMapper,
                                            BahmniDiagnosisMetadata bahmniDiagnosisMetadata,
                                            ObsRelationshipMapper obsRelationshipMapper,
                                            PatientService patientService,
                                            EncounterService encounterService,
                                            ETObsToBahmniObsMapper fromETObsToBahmniObs, SessionFactory sessionFactory) {
        this.accessionNotesMapper = accessionNotesMapper;
        this.bahmniDiagnosisMetadata = bahmniDiagnosisMetadata;
        this.obsRelationshipMapper = obsRelationshipMapper;
        this.patientService = patientService;
        this.encounterService = encounterService;
        this.fromETObsToBahmniObs = fromETObsToBahmniObs;
        this.sessionFactory = sessionFactory;
    }

    private List<OrderWithUrgency> getOrderWithUrgencies(List<EncounterTransaction.Order> orders) {
        List<OrderWithUrgency> orderWithUrgencies = new ArrayList<>();
        for (EncounterTransaction.Order savedOrder : orders) {
            Order order = Context.getOrderService().getOrderByUuid(savedOrder.getUuid());
            sessionFactory.getCurrentSession().refresh(order);
            OrderWithUrgency orderWithUrgency = new OrderWithUrgency();
            orderWithUrgency.setUrgency(order.getUrgency().name());
            orderWithUrgency.setUuid(order.getUuid());
            orderWithUrgencies.add(orderWithUrgency);
        }
        return orderWithUrgencies;
    }

    public BahmniEncounterTransaction map(EncounterTransaction encounterTransaction, boolean includeAll) {
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction(encounterTransaction);
        bahmniEncounterTransaction.setOrdersWithUrgency(getOrderWithUrgencies(encounterTransaction.getOrders()));
        List<BahmniDiagnosisRequest> bahmniDiagnoses = bahmniDiagnosisMetadata.map(encounterTransaction.getDiagnoses(), includeAll);
        bahmniEncounterTransaction.setBahmniDiagnoses(bahmniDiagnoses);
        bahmniEncounterTransaction.setAccessionNotes(accessionNotesMapper.map(encounterTransaction));
        AdditionalBahmniObservationFields additionalBahmniObservationFields = new AdditionalBahmniObservationFields(encounterTransaction.getEncounterUuid(), encounterTransaction.getEncounterDateTime(), null,null);
        additionalBahmniObservationFields.setProviders(encounterTransaction.getProviders());
        List<BahmniObservation> bahmniObservations = fromETObsToBahmniObs.create(encounterTransaction.getObservations(), additionalBahmniObservationFields);
        bahmniEncounterTransaction.setObservations(obsRelationshipMapper.map(bahmniObservations, encounterTransaction.getEncounterUuid()));
        addPatientIdentifier(bahmniEncounterTransaction, encounterTransaction);
        addEncounterType(encounterTransaction, bahmniEncounterTransaction);
        return bahmniEncounterTransaction;
    }

    private void addEncounterType(EncounterTransaction encounterTransaction, BahmniEncounterTransaction bahmniEncounterTransaction) {
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(encounterTransaction.getEncounterTypeUuid());
        if (encounterType != null) {
            bahmniEncounterTransaction.setEncounterType(encounterType.getName());
        }
    }

    private void addPatientIdentifier(BahmniEncounterTransaction bahmniEncounterTransaction, EncounterTransaction encounterTransaction) {
        Patient patient = patientService.getPatientByUuid(encounterTransaction.getPatientUuid());
        if (patient != null) {
            PatientIdentifier patientIdentifier = patient.getPatientIdentifier();
            if(patientIdentifier != null){
                bahmniEncounterTransaction.setPatientId(patientIdentifier.getIdentifier());
            }
        }
    }
}
