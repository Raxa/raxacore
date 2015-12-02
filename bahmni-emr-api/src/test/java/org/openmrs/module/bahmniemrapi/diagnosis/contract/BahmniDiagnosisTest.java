package org.openmrs.module.bahmniemrapi.diagnosis.contract;

import org.junit.Test;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BahmniDiagnosisTest {

    @Test
    public void isSame_Returns_True_If_CodedAnswers_Are_Same() {
        EncounterTransaction.Concept malariaDiagnosis = new EncounterTransaction.Concept("uuid", "Malaria");

        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        bahmniDiagnosis.setCodedAnswer(malariaDiagnosis);

        EncounterTransaction.Diagnosis diagnosis = new EncounterTransaction.Diagnosis();
        diagnosis.setCodedAnswer(malariaDiagnosis);

        assertTrue("both diagnosis for malaria and are same", bahmniDiagnosis.isSame(diagnosis));
    }

    @Test
    public void isSame_Returns_False_If_CodedAnswers_Are_Not_Same() {
        EncounterTransaction.Concept malariaDiagnosis = new EncounterTransaction.Concept("uuid1", "Malaria");
        EncounterTransaction.Concept tbDiagnosis = new EncounterTransaction.Concept("uuid2", "TB");

        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        bahmniDiagnosis.setCodedAnswer(malariaDiagnosis);

        EncounterTransaction.Diagnosis diagnosis = new EncounterTransaction.Diagnosis();
        diagnosis.setCodedAnswer(tbDiagnosis);

        assertFalse("diagnoses are not same", bahmniDiagnosis.isSame(diagnosis));
    }

    @Test
    public void isSame_Returns_True_If_FreeTextAnswers_Are_Same() {
        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        bahmniDiagnosis.setFreeTextAnswer("Malaria");

        EncounterTransaction.Diagnosis diagnosis = new EncounterTransaction.Diagnosis();
        diagnosis.setFreeTextAnswer("Malaria");

        assertTrue("both diagnosis for malaria and are same", bahmniDiagnosis.isSame(diagnosis));
    }

    @Test
    public void isSame_Returns_False_If_FreeTextAnswers_Are_Not_Same() {
        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        bahmniDiagnosis.setFreeTextAnswer("Malaria");

        EncounterTransaction.Diagnosis diagnosis = new EncounterTransaction.Diagnosis();
        diagnosis.setFreeTextAnswer("TB");

        assertFalse("diagnoses are not same", bahmniDiagnosis.isSame(diagnosis));
    }

    @Test
    public void isSame_Returns_False_When_Coded_Answer_Is_Null() {
        EncounterTransaction.Concept malariaDiagnosis = new EncounterTransaction.Concept("uuid", "Malaria");

        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        bahmniDiagnosis.setCodedAnswer(malariaDiagnosis);

        EncounterTransaction.Diagnosis diagnosis = new EncounterTransaction.Diagnosis();
        diagnosis.setFreeTextAnswer("Malaria");

        assertFalse("diagnoses are not same", bahmniDiagnosis.isSame(diagnosis));
    }

    @Test
    public void isSame_Returns_False_When_FreeTextAnswer_Is_Null() {
        EncounterTransaction.Concept malariaDiagnosis = new EncounterTransaction.Concept("uuid", "Malaria");

        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        bahmniDiagnosis.setFreeTextAnswer("Malaria");

        EncounterTransaction.Diagnosis diagnosis = new EncounterTransaction.Diagnosis();
        diagnosis.setCodedAnswer(malariaDiagnosis);

        assertFalse("diagnoses are not same", bahmniDiagnosis.isSame(diagnosis));
    }

    @Test
    public void isSame_Returns_False_When_Both_Are_Null() {
        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        EncounterTransaction.Diagnosis diagnosis = new EncounterTransaction.Diagnosis();

        assertFalse("diagnoses are not same", bahmniDiagnosis.isSame(diagnosis));
    }
}
