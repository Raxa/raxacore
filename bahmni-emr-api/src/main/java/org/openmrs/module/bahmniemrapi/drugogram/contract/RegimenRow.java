package org.openmrs.module.bahmniemrapi.drugogram.contract;

import java.util.*;


public class RegimenRow{

    public static class RegimenComparator implements Comparator<RegimenRow>{
        @Override
        public int compare(RegimenRow o1, RegimenRow o2) {
            if (o1.date.after(o2.date)) return 1;
            if (o1.date.before(o2.date)) return -1;
            return o1.drugs.equals(o2.drugs) ? 0 : 1;
        }
    }
    private String month;
    private Date date;
    private Map<String, String> drugs = new HashMap<>();

    public RegimenRow() {
    }

    public RegimenRow(Date date, Map<String, String> drugs) {
        this.date = date;
        this.drugs = drugs;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String, String> getDrugs() {
        return drugs;
    }

    public void setDrugs(Map<String, String> drugs) {
        this.drugs = drugs;
    }

    public void addDrugs(String name, String dose) {
        drugs.put(name, dose);
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

}
