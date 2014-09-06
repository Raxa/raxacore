package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.model.event.DepartmentEvent;
import org.bahmni.module.referencedata.web.contract.Department;
import org.openmrs.Concept;

import static org.bahmni.module.referencedata.web.contract.mapper.MapperUtils.getDescription;

public class DepartmentMapper extends ResourceMapper {

    public DepartmentMapper() {
        super(DepartmentEvent.DEPARTMENT_PARENT_CONCEPT_NAME);
    }

    @Override
    public Department map(Concept departmentConcept) {
        Department department = new Department();
        department = mapResource(department, departmentConcept);
        department.setDescription(getDescription(departmentConcept));
        return department;
    }


}
