package org.bahmni.module.referencedatafeedclient.service;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.referencedatafeedclient.domain.Drug;
import org.bahmni.module.referencedatafeedclient.domain.ReferenceDataConcept;
import org.bahmni.module.referencedatafeedclient.worker.EventWorkerUtility;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

@Component
public class ReferenceDataConceptService {
    private ConceptService conceptService;
    private EventWorkerUtility eventWorkerUtility;
    private Locale locale = Locale.ENGLISH;
    public static final String MISC = "Misc";

    @Autowired
    public ReferenceDataConceptService(ConceptService conceptService, EventWorkerUtility eventWorkerUtility) {
        this.conceptService = conceptService;
        this.eventWorkerUtility = eventWorkerUtility;
    }

    public Concept saveConcept(ReferenceDataConcept referenceDataConcept) {
        Concept concept = conceptService.getConceptByUuid(referenceDataConcept.getUuid());
        if (concept == null) {
            concept = new Concept();
            concept.setUuid(referenceDataConcept.getUuid());
        }
        concept.setDatatype(conceptService.getConceptDatatypeByUuid(referenceDataConcept.getDataTypeUuid()));
        concept.setConceptClass(conceptService.getConceptClassByName(referenceDataConcept.getClassName()));
        addOrUpdateName(concept, referenceDataConcept.getName(), ConceptNameType.FULLY_SPECIFIED);
        addOrUpdateName(concept, referenceDataConcept.getShortName(), ConceptNameType.SHORT);
        if (referenceDataConcept.getDescription() != null) {
            addOrUpdateDescription(concept, referenceDataConcept.getDescription());
        }
        addOrRemoveSetMembers(concept, referenceDataConcept.getSetMemberUuids());
        concept.setRetired(referenceDataConcept.isRetired());
        concept.setSet(referenceDataConcept.isSet());
        return conceptService.saveConcept(concept);
    }

    public void saveSetMembership(Concept parentConcept, Concept childConcept) {
        if (parentConcept.getSetMembers().contains(childConcept)) return;
        parentConcept.addSetMember(childConcept);
        conceptService.saveConcept(parentConcept);
    }

    public void saveNewSetMembership(Concept parentConcept, Concept childConcept, double sortOrder) {
        parentConcept.addSetMember(childConcept);
        saveWithSortOrder(parentConcept, childConcept, sortOrder);
    }

    public void saveExistingSetMembership(Concept parentConcept, Concept childConcept, double sortOrder) {
        saveWithSortOrder(parentConcept, childConcept, sortOrder);
    }

    private void saveWithSortOrder(Concept parentConcept, Concept childConcept, double sortOrder) {
        ConceptSet matchingConceptSet = eventWorkerUtility.getMatchingConceptSet(parentConcept.getConceptSets(), childConcept);
        matchingConceptSet.setSortWeight(sortOrder);
        conceptService.saveConcept(parentConcept);
    }

    public void saveDrug(Drug drug) {
        org.openmrs.Drug conceptDrug = conceptService.getDrugByUuid(drug.getId());
        if (conceptDrug == null) {
            conceptDrug = new org.openmrs.Drug();
            conceptDrug.setUuid(drug.getId());
        }
        conceptDrug.setName(drug.getName());
        Concept dosageForm = conceptService.getConceptByUuid(drug.getForm().getId());
        if (dosageForm == null) {
            throw new RuntimeException(String.format("Could not find dosage form for %s", drug.getForm().getName()));
        }
        conceptDrug.setDosageForm(dosageForm);
        if (drug.getStrength() != null) {
            conceptDrug.setDoseStrength(Double.parseDouble(drug.getStrength()));
        }
        conceptDrug.setUnits(drug.getStrengthUnits());
        conceptDrug.setConcept(getConceptByName(drug.getGenericName()));
        conceptDrug.setRoute(getConceptByName(drug.getRoute()));
        if (!drug.getIsActive()) {
            conceptDrug.setRetired(true);
        }
        conceptService.saveDrug(conceptDrug);
    }

    private Concept getConceptByName(String drugName) {
        if (StringUtils.isBlank(drugName)) return null;
        Concept concept = conceptService.getConceptByName(drugName);
        if (concept == null) {
            concept = saveConcept(new ReferenceDataConcept(null, drugName, MISC, ConceptDatatype.N_A_UUID));
        }
        return concept;
    }

    private void addOrRemoveSetMembers(Concept concept, Set<String> setMemberUuids) {
        for (String uuid : setMemberUuids) {
            Concept childConcept = conceptService.getConceptByUuid(uuid);
            if (!concept.getSetMembers().contains(childConcept))
                concept.addSetMember(childConcept);
        }
        for (ConceptSet conceptSet : new ArrayList<>(concept.getConceptSets())) {
            if (!setMemberUuids.contains(conceptSet.getConcept().getUuid())) {
                concept.getConceptSets().remove(conceptSet);
            }
        }
    }

    private void addOrUpdateDescription(Concept concept, String description) {
        ConceptDescription conceptDescription = concept.getDescription(locale);
        if (conceptDescription != null) {
            conceptDescription.setDescription(description);
        } else {
            concept.addDescription(new ConceptDescription(description, locale));
        }
    }

    private void addOrUpdateName(Concept concept, String name, ConceptNameType type) {
        ConceptName conceptName = concept.getName(locale, type, null);
        if (conceptName != null) {
            if (name == null || StringUtils.isBlank(name)) {
                conceptName.setVoided(true);
            } else {
                conceptName.setName(name);
            }
        } else if (name != null) {
            ConceptName newName = new ConceptName(name, locale);
            newName.setConceptNameType(type);
            concept.addName(newName);
        }
    }
}
