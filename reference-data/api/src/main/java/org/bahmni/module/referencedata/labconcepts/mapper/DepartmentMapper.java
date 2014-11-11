package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.Department;
import org.openmrs.Concept;

public class DepartmentMapper extends ResourceMapper {

    public DepartmentMapper() {
        super(Department.DEPARTMENT_PARENT_CONCEPT_NAME);
    }

    @Override
    public Department map(Concept departmentConcept) {
        Department department = new Department();
        department = mapResource(department, departmentConcept);
        department.setDescription(MapperUtils.getDescriptionOrName(departmentConcept));
        department.setTestsAndPanels(new TestAndPanelMapper().map(departmentConcept));
        return department;
    }
}
