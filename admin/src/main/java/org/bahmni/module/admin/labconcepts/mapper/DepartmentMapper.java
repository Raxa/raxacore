package org.bahmni.module.admin.labconcepts.mapper;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.admin.csv.models.DepartmentRow;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;

import java.util.Set;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.constructDescription;
import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.getConceptName;

public class DepartmentMapper {

    public Concept map(DepartmentRow departmentRow, Concept existingDepartment) {
        Concept department = new Concept();
        Set<ConceptDescription> descriptions = constructDescription(StringUtils.isEmpty(departmentRow.description) ? departmentRow.name : departmentRow.description);
        if (existingDepartment == null) {
            department.setFullySpecifiedName(getConceptName(departmentRow.name));

        } else {
            department = existingDepartment;
        }
        department.setDescriptions(descriptions);
        return department;
    }
}
