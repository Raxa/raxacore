package org.bahmni.module.referencedata.labconcepts.mapper;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.referencedata.labconcepts.contract.Department;
import org.bahmni.module.referencedata.labconcepts.contract.Sample;
import org.bahmni.module.referencedata.labconcepts.contract.Test;
import org.openmrs.*;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;

import java.util.*;

public class MapperUtils {
    public static String getDescription(Concept concept) {
        ConceptDescription description = concept.getDescription();
        if (description != null) {
            return description.getDescription();
        }
        return null;
    }

    public static Set<ConceptDescription> constructDescription(String description) {
        if (StringUtils.isBlank(description)) return null;
        ConceptDescription conceptDescription = new ConceptDescription(description, Locale.ENGLISH);
        Set<ConceptDescription> descriptions = new HashSet<>();
        descriptions.add(conceptDescription);
        return descriptions;
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


    public static List<Test> getTests(Concept concept) {
        List<Test> tests = new ArrayList<>();
        TestMapper testMapper = new TestMapper();
        List<Concept> setMembers = concept.getSetMembers();
        if (setMembers == null) return tests;
        for (Concept setMember : setMembers) {
            if (isTestConcept(setMember)) {
                tests.add(testMapper.map(setMember));
            }
        }
        return tests;
    }

    public static ConceptName getConceptName(String name){
        ConceptName conceptName = new ConceptName();
        conceptName.setName(name);
        conceptName.setLocale(Context.getLocale());
        return conceptName;
    }

    public static ConceptName getConceptName(String name, ConceptNameType conceptNameType){
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

    public static ConceptClass getConceptClass(String className){
        ConceptClass conceptClass = Context.getConceptService().getConceptClassByName(className);
        return conceptClass;
    }

    public static String getUnits(Concept concept) {
        ConceptNumeric conceptNumeric = Context.getConceptService().getConceptNumeric(concept.getConceptId());
        return conceptNumeric == null ? null : conceptNumeric.getUnits();
    }

    private static boolean isTestConcept(Concept concept) {
        return concept.getConceptClass() != null &&
                concept.getConceptClass().getUuid().equals(ConceptClass.TEST_UUID);
    }

    public static boolean isSampleConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getName() != null && concept.getConceptClass().getName().equals(Sample.SAMPLE_CONCEPT_CLASS);
    }

    public static boolean isDepartmentConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getName() != null && concept.getConceptClass().getName().equals(Department.DEPARTMENT_CONCEPT_CLASS);
    }
}
