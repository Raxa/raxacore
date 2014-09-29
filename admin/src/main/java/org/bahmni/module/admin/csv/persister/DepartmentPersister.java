package org.bahmni.module.admin.csv.persister;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.DepartmentRow;
import org.bahmni.module.admin.labconcepts.mapper.DepartmentMapper;
import org.bahmni.module.referencedata.labconcepts.contract.Department;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.getConceptClass;


@Component
public class DepartmentPersister implements EntityPersister<DepartmentRow> {
    @Autowired
    private ConceptService conceptService;
    private DepartmentMapper departmentMapper;

    private static final org.apache.log4j.Logger log = Logger.getLogger(DepartmentPersister.class);
    private UserContext userContext;

    public void init(UserContext userContext){
        this.userContext = userContext;
        departmentMapper = new DepartmentMapper();
    }

    @Override
    public RowResult<DepartmentRow> validate(DepartmentRow departmentRow) {
        String error = "";
        if (StringUtils.isEmpty(departmentRow.name)) {
            error = "Error";
        }
        return new RowResult<>(new DepartmentRow(), error);
    }

    @Override
    public RowResult<DepartmentRow> persist(DepartmentRow departmentRow) {
        try {
            Context.openSession();
            Context.setUserContext(userContext);

            Concept departmentParentConcept = conceptService.getConceptByName(Department.DEPARTMENT_PARENT_CONCEPT_NAME);
            Concept existingDepartment = conceptService.getConceptByName(StringUtils.trim(departmentRow.name));
            ConceptClass conceptClass = getConceptClass(Department.DEPARTMENT_CONCEPT_CLASS);
            if(!existingDepartment.getConceptClass().getName().equals(Department.DEPARTMENT_CONCEPT_CLASS)){
                existingDepartment = null;
            }
            Concept departmentConcept = departmentMapper.map(departmentRow, existingDepartment);
            departmentConcept.setConceptClass(conceptClass);
            conceptService.saveConcept(departmentConcept);
            departmentParentConcept.addSetMember(departmentConcept);
            conceptService.saveConcept(departmentParentConcept);
            return new RowResult<>(departmentRow);
        } catch (Exception e) {
            log.error(e);
            Context.clearSession();
            return new RowResult<>(departmentRow, e);
        } finally {
            Context.flushSession();
            Context.closeSession();
        }
    }
}
