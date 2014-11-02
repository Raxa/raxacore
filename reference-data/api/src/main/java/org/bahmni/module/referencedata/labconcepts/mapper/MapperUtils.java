package org.bahmni.module.referencedata.labconcepts.mapper;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.referencedata.labconcepts.contract.Department;
import org.bahmni.module.referencedata.labconcepts.contract.LabTest;
import org.bahmni.module.referencedata.labconcepts.contract.Sample;
import org.openmrs.*;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapperUtils {
    public static String getDescription(Concept concept) {
        ConceptDescription description = concept.getDescription();
        if (description != null) {
            return description.getDescription();
        }
        return null;
    }

    public static Set<ConceptDescription> constructDescriptions(String description) {
        if (StringUtils.isBlank(description)) return null;
        ConceptDescription conceptDescription = new ConceptDescription(description, Context.getLocale());
        Set<ConceptDescription> descriptions = new HashSet<>();
        descriptions.add(conceptDescription);
        return descriptions;
    }

    public static ConceptDescription constructDescription(String description) {
        if (StringUtils.isBlank(description)) return null;
        ConceptDescription conceptDescription = new ConceptDescription(description, Context.getLocale());
        return conceptDescription;
    }

    public static Department getDepartment(Concept concept) {
        List<ConceptSet> parentConcepts = Context.getConceptService().getSetsContainingConcept(concept);
        for (ConceptSet parentConcept : parentConcepts) {
            if (isDepartmentConcept(parentConcept.getConceptSet())) {
                DepartmentMapper departmentMapper = new DepartmentMapper();
                return departmentMapper.map(parentConcept.getConceptSet());
            }
        }
        return null;
    }


    public static Sample getSample(Concept concept) {
        List<ConceptSet> parentConcepts = Context.getConceptService().getSetsContainingConcept(concept);
        if (parentConcepts == null) return null;
        for (ConceptSet parentConcept : parentConcepts) {
            if (isSampleConcept(parentConcept.getConceptSet())) {
                SampleMapper sampleMapper = new SampleMapper();
                return sampleMapper.map(parentConcept.getConceptSet());
            }
        }
        return null;
    }


    public static List<LabTest> getTests(Concept concept) {
        List<LabTest> tests = new ArrayList<>();
        LabTestMapper testMapper = new LabTestMapper();
        List<Concept> setMembers = concept.getSetMembers();
        if (setMembers == null) return tests;
        for (Concept setMember : setMembers) {
            if (isTestConcept(setMember)) {
                tests.add(testMapper.map(setMember));
            }
        }
        return tests;
    }

    public static ConceptName getConceptName(String name) {
        ConceptName conceptName = new ConceptName();
        conceptName.setName(name);
        conceptName.setLocale(Context.getLocale());
        return conceptName;
    }

    public static ConceptName getConceptName(String name, ConceptNameType conceptNameType) {
        ConceptName conceptName = getConceptName(name);
        conceptName.setConceptNameType(conceptNameType);
        return conceptName;
    }

    public static ConceptDatatype getDataTypeByUuid(String dataTypeUuid) {
        ConceptDatatype conceptDatatype = Context.getConceptService().getConceptDatatypeByUuid(dataTypeUuid);
        return conceptDatatype;
    }

    public static ConceptDatatype getDataTypeByName(String dataTypeName) {
        ConceptDatatype conceptDatatype = Context.getConceptService().getConceptDatatypeByName(dataTypeName);
        return conceptDatatype;
    }

    public static ConceptClass getConceptClass(String className) {
        ConceptClass conceptClass = Context.getConceptService().getConceptClassByName(className);
        return conceptClass;
    }

    public static String getUnits(Concept concept) {
        ConceptNumeric conceptNumeric = Context.getConceptService().getConceptNumeric(concept.getConceptId());
        return conceptNumeric == null ? null : conceptNumeric.getUnits();
    }

    public static boolean isTestConcept(Concept concept) {
        return concept.getConceptClass() != null &&
                concept.getConceptClass().getUuid().equals(ConceptClass.TEST_UUID);
    }

    public static boolean isActive(Concept setMember) {
        return !setMember.isRetired();
    }

    public static org.openmrs.Concept addConceptName(org.openmrs.Concept concept, ConceptName conceptName) {
        if (conceptName.getName() == null) return concept;
        for (ConceptName name : concept.getNames()) {
            if (isFullySpecifiedName(conceptName) && isFullySpecifiedName(name) && !name.getName().equals(conceptName.getName())) {
                name.setName(conceptName.getName());
                return concept;
            } else if (isShortName(conceptName) && isShortName(name) && !name.getName().equals(conceptName.getName())) {
                name.setName(conceptName.getName());
                return concept;
            } else if (name.getName().equals(conceptName.getName())) {
                return concept;
            }
        }

        concept.addName(conceptName);
        return concept;
    }

    private static boolean isShortName(ConceptName conceptName) {
        return ObjectUtils.equals(conceptName.getConceptNameType(), ConceptNameType.SHORT);
    }

    private static boolean isFullySpecifiedName(ConceptName conceptName) {
        return ObjectUtils.equals(conceptName.getConceptNameType(), ConceptNameType.FULLY_SPECIFIED);
    }


    public static boolean isSampleConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getName() != null && concept.getConceptClass().getName().equals(Sample.SAMPLE_CONCEPT_CLASS);
    }

    public static boolean isPanelConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getUuid().equals(ConceptClass.LABSET_UUID);
    }

    public static boolean isDepartmentConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getName() != null && concept.getConceptClass().getName().equals(Department.DEPARTMENT_CONCEPT_CLASS);
    }

    public static String getSampleUuid(Concept concept) {
        Sample sampleConcept = getSample(concept);
        if (sampleConcept == null) {
            return null;
        }
        return sampleConcept.getId();
    }
}
