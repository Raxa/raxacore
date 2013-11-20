package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.model.VisitSummary;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.visit.EmrVisitService;
import org.openmrs.module.emrapi.visit.contract.VisitRequest;
import org.openmrs.module.emrapi.visit.contract.VisitResponse;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VisitSummaryControllerTest {

    @Mock
    private EmrVisitService emrVisitService;

    @Before
    public void before() {
        initMocks(this);
    }


    @Test
    public void shouldGetDispositionsAndDiagnosesFor_A_VisitAsSummary() {

        String visitUuid = "abcd-1232-asdf";
        VisitRequest visitRequest = new VisitRequest(visitUuid);

        VisitResponseMother visitResponseMother = new VisitResponseMother();
        VisitResponse visitResponse = visitResponseMother
                .withEncounterTransaction(
                        new EncounterTransactionMother()
                                .withPrimaryDiagnosis("TUBERCULOSIS")
                                .withSecondaryDiagnosis("FEVER")
                                .withDisposition("ADMIT")
                                .build())
                .withEncounterTransaction(new EncounterTransactionMother().withPrimaryDiagnosis("COUGH")
                        .withSecondaryDiagnosis("COLD")
                        .withDisposition("ADMIT")
                        .build())
                .build();

        when(emrVisitService.find(any(VisitRequest.class))).thenReturn(visitResponse);

        VisitSummary summary = new VisitSummaryController(emrVisitService).get(visitUuid);

        assertEquals(summary.getDiagnoses().size(), 4);
        assertEquals(summary.getDispositions().size(), 2);
    }

    private EncounterTransaction.Diagnosis getDiagnosis(String uuid, String name, String order) {
        EncounterTransaction.Diagnosis diagnosis = new EncounterTransaction.Diagnosis();
        EncounterTransaction.Concept codedAnswer = new EncounterTransaction.Concept(uuid, name);
        diagnosis.setCertainty("CERTAIN").setCodedAnswer(codedAnswer).setOrder(order);
        return diagnosis;
    }


}

class EncounterTransactionMother {

    private EncounterTransaction encounterTransaction;

    public EncounterTransactionMother() {
        encounterTransaction = new EncounterTransaction();
    }

    public EncounterTransactionMother withPrimaryDiagnosis(String diseaseName) {
        encounterTransaction.addDiagnosis(new DiagnosisMother().withDiagnosis(diseaseName, "Primary").build());
        return this;
    }

    public EncounterTransactionMother withDisposition(String disposition) {
        encounterTransaction.setDisposition(new EncounterTransaction.Disposition(disposition));
        return this;
    }

    public EncounterTransaction build() {
        return encounterTransaction;
    }

    public EncounterTransactionMother withSecondaryDiagnosis(String diseaseName) {
        encounterTransaction.addDiagnosis(new DiagnosisMother().withDiagnosis(diseaseName, "Secondary").build());
        return this;
    }
}

class VisitResponseMother {

    private EncounterTransaction encounterTransaction;
    private VisitResponse visitResponse;

    public VisitResponseMother() {
        this.visitResponse = new VisitResponse(UUID.randomUUID().toString());
    }

    public VisitResponseMother withEncounterTransaction(EncounterTransaction encounterTransaction) {
        encounterTransaction.setVisitUuid(visitResponse.getVisitUuid());
        visitResponse.addEncounter(encounterTransaction);
        return this;
    }

    public VisitResponse build() {
        return visitResponse;
    }
}

class DiagnosisMother {
    private EncounterTransaction.Diagnosis diagnosis;

    public DiagnosisMother() {
        this.diagnosis = new EncounterTransaction.Diagnosis()
                .setCertainty("Certain")
                .setOrder("Primary")
                .setCodedAnswer(new EncounterTransaction.Concept(UUID.randomUUID().toString(), "TUBERCULOSIS"));
    }

    public EncounterTransaction.Diagnosis build() {
        return diagnosis;
    }

    public DiagnosisMother withPrimaryDiagnosis(String diseaseName) {
        return withDiagnosis(diseaseName, "Primary");
    }

    public DiagnosisMother withSecondaryDiagnosis(String diseaseName) {
        return withDiagnosis(diseaseName, "Secondary");
    }

    public DiagnosisMother withDiagnosis(String diseaseName, String order) {
        diagnosis.setCodedAnswer(new EncounterTransaction.Concept(UUID.randomUUID().toString(), diseaseName));
        diagnosis.setOrder(order);
        return this;
    }

}


