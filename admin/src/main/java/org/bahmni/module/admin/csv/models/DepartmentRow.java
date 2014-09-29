package org.bahmni.module.admin.csv.models;

import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.annotation.CSVHeader;

public class DepartmentRow extends CSVEntity{
    @CSVHeader(name = "name")
    public String name;

    @CSVHeader(name = "description")
    public String description;
}
