package org.bahmni.module.referencedata.labconcepts.model;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.Drug;

public class DrugMetaData {
    private Concept drugConcept;
    private Concept dosageForm;
    private ConceptClass drugConceptClass;
    private ConceptDatatype naDataType;
    private Drug existingDrug;
    private boolean conceptExists;

    public DrugMetaData() {
    }

    public DrugMetaData(Drug existingDrug, Concept drugConcept, Concept dosageFormConcept, ConceptClass drugConceptClass, ConceptDatatype naDataType) {
        this.existingDrug = existingDrug;
        this.drugConcept = drugConcept;
        this.dosageForm = dosageFormConcept;
        this.drugConceptClass = drugConceptClass;
        this.naDataType = naDataType;
        this.conceptExists = (drugConcept != null);
    }

    public Concept getDrugConcept() {
        if (drugConcept == null) {
            if (existingDrug != null) {
                return existingDrug.getConcept();
            } else {
                Concept drugConcept = new Concept();
                drugConcept.setConceptClass(drugConceptClass);
                drugConcept.setDatatype(naDataType);
                return drugConcept;
            }
        } else {
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

    public boolean isConceptExists() {
        return conceptExists;
    }

    public void setConceptExists(boolean conceptExists) {
        this.conceptExists = conceptExists;
    }
}
