package org.openmrs.module.bahmniemrapi.drugogram.contract;

public class BaseTableExtension<T> implements TableExtension<T> {

    @Override
    public void update(T table) {
    }

    @Override
    public void update(T table, String patientUuid) {
    }
}
