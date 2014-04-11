package org.bahmni.module.bahmnicore.contract.encounter.request;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class AccessionNote {
    private static final String DATETIME_FORMAT = "dd MMM yy HH:m`m";
    private String text;
    private String providerName;
    private String accessionUuid;
    private String dateTime;

    public AccessionNote() {
    }

    public AccessionNote(String text, String providerName, String accessionUuid, String dateTime) {
        this.text = text;
        this.providerName = providerName;
        this.accessionUuid = accessionUuid;
        this.dateTime = dateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getAccessionUuid() {
        return accessionUuid;
    }

    public void setAccessionUuid(String accessionUuid) {
        this.accessionUuid = accessionUuid;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setDateTime(Date dateTime){
        this.dateTime = DateFormatUtils.format(dateTime,DATETIME_FORMAT);
    }
}
