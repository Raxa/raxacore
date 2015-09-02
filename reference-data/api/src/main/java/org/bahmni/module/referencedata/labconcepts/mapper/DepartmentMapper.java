package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.Department;
import org.bahmni.module.referencedata.labconcepts.contract.LabTest;
import org.openmrs.Concept;

import static org.bahmni.module.referencedata.labconcepts.mapper.ConceptExtension.getResourceReferencesOfConceptClass;

public class DepartmentMapper extends ResourceMapper {

    public DepartmentMapper() {
        super(Department.DEPARTMENT_PARENT_CONCEPT_NAME);
    }

    @Override
    public Department map(Concept departmentConcept) {
        Department department = new Department();
        department = mapResource(department, departmentConcept);
        department.setDescription(ConceptExtension.getDescriptionOrName(departmentConcept));
        department.setTests(getResourceReferencesOfConceptClass(departmentConcept.getSetMembers(), LabTest.LAB_TEST_CONCEPT_CLASS));
        return department;
    }
}
