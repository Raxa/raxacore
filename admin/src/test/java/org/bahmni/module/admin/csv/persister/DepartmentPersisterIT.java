package org.bahmni.module.admin.csv.persister;

import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.DepartmentRow;
import org.bahmni.module.referencedata.labconcepts.contract.Department;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:webModuleApplicationContext.xml","classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class DepartmentPersisterIT extends BaseContextSensitiveTest {
    @Autowired
    private DepartmentPersister departmentPersister;

    @Autowired
    private ConceptService conceptService;
    private UserContext userContext;

    @Before
    public void setUp() throws Exception {
        Context.authenticate("admin", "test");
        executeDataSet("labConcepts.xml");
        userContext = Context.getUserContext();
        departmentPersister.init(userContext);
    }

    @Test
    public void should_fail_validation_for_no_department_name() throws Exception {
        DepartmentRow departmentRow = new DepartmentRow();
        RowResult<DepartmentRow> departmentRowRowResult = departmentPersister.validate(departmentRow);
        assertFalse(departmentRowRowResult.getErrorMessage().isEmpty());
    }

    @Test
    public void should_pass_validation_if_department_name_is_present() throws Exception {
        DepartmentRow departmentRow = new DepartmentRow();
        departmentRow.name = "Department Name";
        RowResult<DepartmentRow> departmentRowResult = departmentPersister.validate(departmentRow);
        assertTrue(departmentRowResult.getErrorMessage().isEmpty());
    }

    @Test
    public void should_persist_new_department_with_name_input_only() throws Exception {
        DepartmentRow departmentRow = new DepartmentRow();
        departmentRow.name = "New Department";
        RowResult<DepartmentRow> departmentRowResult = departmentPersister.persist(departmentRow);
        assertNull(departmentRowResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept departmentConcept = conceptService.getConceptByName(departmentRow.name);
        assertNotNull(departmentConcept);
        assertEquals(departmentRow.name, departmentConcept.getName(Context.getLocale()).getName());
        assertEquals(Department.DEPARTMENT_CONCEPT_CLASS, departmentConcept.getConceptClass().getName());
        List<ConceptSet> labDepartments = conceptService.getSetsContainingConcept(departmentConcept);
        Concept labDepartment = labDepartments.get(0).getConceptSet();
        assertEquals(Department.DEPARTMENT_PARENT_CONCEPT_NAME, labDepartment.getName(Context.getLocale()).getName());
        Context.flushSession();
        Context.closeSession();
    }

    @Test
    public void should_persist_new_department_with_name_and_description_input_only() throws Exception {
        DepartmentRow departmentRow = new DepartmentRow();
        departmentRow.name = "New Department";
        departmentRow.description = "New Description";
        RowResult<DepartmentRow> departmentRowResult = departmentPersister.persist(departmentRow);
        assertNull(departmentRowResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept departmentConcept = conceptService.getConceptByName(departmentRow.name);
        assertNotNull(departmentConcept);
        assertEquals(departmentRow.name, departmentConcept.getName(Context.getLocale()).getName());
        assertEquals(departmentRow.description, departmentConcept.getDescription(Context.getLocale()).getDescription());
        assertEquals(Department.DEPARTMENT_CONCEPT_CLASS, departmentConcept.getConceptClass().getName());
        List<ConceptSet> labDepartments = conceptService.getSetsContainingConcept(departmentConcept);
        Concept labDepartment = labDepartments.get(0).getConceptSet();
        assertEquals(Department.DEPARTMENT_PARENT_CONCEPT_NAME, labDepartment.getName(Context.getLocale()).getName());
        Context.flushSession();
        Context.closeSession();
    }

    @Test
    public void should_persist_new_department_with_name_input_and_set_description_as_name() throws Exception {
        DepartmentRow departmentRow = new DepartmentRow();
        departmentRow.name = "New Department";
        RowResult<DepartmentRow> departmentRowResult = departmentPersister.persist(departmentRow);
        assertNull(departmentRowResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept departmentConcept = conceptService.getConceptByName(departmentRow.name);
        assertNotNull(departmentConcept);
        assertEquals(departmentRow.name, departmentConcept.getName(Context.getLocale()).getName());
        assertEquals(departmentRow.name, departmentConcept.getDescription(Context.getLocale()).getDescription());
        assertEquals(Department.DEPARTMENT_CONCEPT_CLASS, departmentConcept.getConceptClass().getName());
        List<ConceptSet> labDepartments = conceptService.getSetsContainingConcept(departmentConcept);
        Concept labDepartment = labDepartments.get(0).getConceptSet();
        assertEquals(Department.DEPARTMENT_PARENT_CONCEPT_NAME, labDepartment.getName(Context.getLocale()).getName());
        Context.flushSession();
        Context.closeSession();
    }

    @Test
    public void should_update_description_on_existing_departments() throws Exception {
        DepartmentRow departmentRow = new DepartmentRow();
        departmentRow.name = "New Department";
        RowResult<DepartmentRow> departmentRowResult = departmentPersister.persist(departmentRow);
        assertNull(departmentRowResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept departmentConcept = conceptService.getConceptByName(departmentRow.name);
        assertNotNull(departmentConcept);
        assertEquals(departmentRow.name, departmentConcept.getName(Context.getLocale()).getName());
        assertEquals(departmentRow.name, departmentConcept.getDescription(Context.getLocale()).getDescription());
        assertEquals(Department.DEPARTMENT_CONCEPT_CLASS, departmentConcept.getConceptClass().getName());
        List<ConceptSet> labDepartments = conceptService.getSetsContainingConcept(departmentConcept);
        Concept labDepartment = labDepartments.get(0).getConceptSet();
        assertEquals(Department.DEPARTMENT_PARENT_CONCEPT_NAME, labDepartment.getName(Context.getLocale()).getName());
        Context.flushSession();
        Context.closeSession();
    }
}
