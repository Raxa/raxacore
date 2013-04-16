package org.bahmni.jss.registration;

public interface LookupValueProvider {
    String getLookUpValue(String key);
    String getLookUpValue(String key, int valueIndex);
}
