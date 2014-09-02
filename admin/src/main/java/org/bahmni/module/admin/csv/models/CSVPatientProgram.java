package org.bahmni.module.admin.csv.models;

import java.util.Date;

public class CSVPatientProgram {
    public final String programName;
    public final Date encounterDate;

    public CSVPatientProgram(String programName, Date encounterDate) {
        this.programName = programName;
        this.encounterDate = encounterDate;
    }
}
