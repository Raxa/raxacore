package org.bahmni.module.referencedata.labconcepts.model;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.Drug;

public class DrugMetaData {
    private Concept drugConcept;
    private Concept dosageForm;
    private ConceptClass drugConceptClass;
    private Drug existingDrug;

    public DrugMetaData(Drug existingDrug, Concept drugConcept, Concept dosageFormConcept, ConceptClass drugConceptClass) {
        this.drugConcept = drugConcept;
        this.existingDrug = existingDrug;
        this.drugConceptClass = drugConceptClass;
        this.dosageForm = dosageFormConcept;
    }

    public DrugMetaData() {
    }

    public Concept getDrugConcept() {
        if (drugConcept == null) {
            if (existingDrug != null) {
                return existingDrug.getConcept();
            } else {
                Concept drugConcept = new Concept();
                drugConcept.setConceptClass(drugConceptClass);
                return drugConcept;
            }
        } else {
            drugConcept.setConceptClass(drugConceptClass);
            return drugConcept;
        }
    }

    public void setDrugConcept(Concept drugConcept) {
        this.drugConcept = drugConcept;
    }

    public Concept getDosageForm() {
        if (dosageForm == null && existingDrug != null && existingDrug.getDosageForm() != null) {
            return existingDrug.getDosageForm();
        } else {
            return dosageForm;
        }
    }

    public void setDosageForm(Concept dosageForm) {
        this.dosageForm = dosageForm;
    }

    public ConceptClass getDrugConceptClass() {
        return drugConceptClass;
    }

    public void setDrugConceptClass(ConceptClass drugConceptClass) {
        this.drugConceptClass = drugConceptClass;
    }

    public Drug getExistingDrug() {
        return existingDrug == null ? new Drug() : existingDrug;
    }

    public void setExistingDrug(Drug existingDrug) {
        this.existingDrug = existingDrug;
    }
}
