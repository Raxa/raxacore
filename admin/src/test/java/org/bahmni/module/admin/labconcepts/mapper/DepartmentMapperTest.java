package org.bahmni.module.admin.labconcepts.mapper;

import org.bahmni.module.admin.csv.models.DepartmentRow;
import org.bahmni.module.bahmnicore.mapper.builder.ConceptBuilder;
import org.bahmni.module.referencedata.labconcepts.contract.Department;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Locale;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class DepartmentMapperTest {

    private DepartmentMapper departmentMapper;

    @Before
    public void setUp() throws Exception {
        Locale defaultLocale = new Locale("en", "GB");
        PowerMockito.mockStatic(Context.class);
        when(Context.getLocale()).thenReturn(defaultLocale);
        departmentMapper = new DepartmentMapper();
    }

    @Test
    public void should_map_department_row_name_to_concept_name() throws Exception {
        DepartmentRow departmentRow = new DepartmentRow();
        departmentRow.name = "New Department";
        Concept department = departmentMapper.map(departmentRow, null);
        assertEquals(departmentRow.name, department.getName(Context.getLocale()).getName());
    }

    @Test
    public void should_map_department_row_name_to_concept_description() throws Exception {
        DepartmentRow departmentRow = new DepartmentRow();
        departmentRow.name = "New Department";
        Concept department = departmentMapper.map(departmentRow, null);
        assertEquals(departmentRow.name, department.getDescription(Context.getLocale()).getDescription());
    }

    @Test
    public void should_set_is_retired_as_false_by_default() throws Exception {
        DepartmentRow departmentRow = new DepartmentRow();
        departmentRow.name = "New Department";
        Concept department = departmentMapper.map(departmentRow, null);
        assertEquals(departmentRow.name, department.getName(Context.getLocale()).getName());
        assertFalse(department.isRetired());
    }

    @Test
    public void should_update_description_if_concept_already_exists() throws Exception {
        DepartmentRow departmentRow = new DepartmentRow();
        departmentRow.name = "New Department";
        departmentRow.description = "New Description";
        Concept existingDepartment = new ConceptBuilder().withName(departmentRow.name).withClass(Department.DEPARTMENT_CONCEPT_CLASS).withDescription("Some Description").build();
        Concept department = departmentMapper.map(departmentRow, existingDepartment);
        assertEquals(departmentRow.description, department.getDescription(Context.getLocale()).getDescription());
    }

    @Test
    public void should_set_description_if_description_does_not_exist_in_existing_concept() throws Exception {
        DepartmentRow departmentRow = new DepartmentRow();
        departmentRow.name = "New Department";
        Concept existingDepartment = new ConceptBuilder().withName(departmentRow.name).withClass(Department.DEPARTMENT_CONCEPT_CLASS).build();
        Concept department = departmentMapper.map(departmentRow, existingDepartment);
        assertEquals(departmentRow.name, department.getDescription(Context.getLocale()).getDescription());
    }
}