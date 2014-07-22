package org.openmrs.module.bahmniemrapi.diagnosis.contract;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BahmniDiagnosis extends EncounterTransaction.Diagnosis {
    private EncounterTransaction.Concept diagnosisStatusConcept;
    private BahmniDiagnosis firstDiagnosis;
    private boolean revised;

    public EncounterTransaction.Concept getDiagnosisStatusConcept() {
        return diagnosisStatusConcept;
    }

    public void setDiagnosisStatusConcept(EncounterTransaction.Concept diagnosisStatusConcept) {
        this.diagnosisStatusConcept = diagnosisStatusConcept;
    }

    public BahmniDiagnosis getFirstDiagnosis() {
        return firstDiagnosis;
    }

    public void setFirstDiagnosis(BahmniDiagnosis firstDiagnosis) {
        this.firstDiagnosis = firstDiagnosis;
    }

    public boolean isRevised() {
        return revised;
    }

    public void setRevised(boolean revised) {
        this.revised = revised;
    }

    public boolean isSame(EncounterTransaction.Diagnosis diagnosis) {
        if (getFreeTextAnswer() != null || diagnosis.getFreeTextAnswer() != null) {
            return isSameFreeTextAnswer(diagnosis);
        }
        return isSameCodedAnswer(diagnosis);
    }

    public boolean isSameFreeTextAnswer(EncounterTransaction.Diagnosis diagnosis) {
        if (getFreeTextAnswer() == null || diagnosis.getFreeTextAnswer() == null)
            return false;

        return getFreeTextAnswer().equals(diagnosis.getFreeTextAnswer());
    }

    public boolean isSameCodedAnswer(EncounterTransaction.Diagnosis diagnosis) {
        if (getCodedAnswer() == null || diagnosis.getCodedAnswer() == null)
            return false;

        return getCodedAnswer().getUuid().equals(diagnosis.getCodedAnswer().getUuid());
    }

}
