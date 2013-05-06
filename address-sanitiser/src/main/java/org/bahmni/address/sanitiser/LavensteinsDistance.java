package org.bahmni.address.sanitiser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LavensteinsDistance {
    private AddressHierarchy addressHierarchy;

    public LavensteinsDistance(AddressHierarchy addressHierarchy) {
        this.addressHierarchy = addressHierarchy;
    }

    public String getClosestMatch(String query) {
        List<String> villages = addressHierarchy.getAllVillages();
        Map<String, Integer> distanceMap = new HashMap<String, Integer>();
        for(String village : villages){
            distanceMap.put(village, computeDistance(query, village));
        }
        return getEntryWithClosestMatch(villages, distanceMap);
    }

    public PersonAddress getClosestMatch(String query, List<PersonAddress> personAddresses, AddressField field) {
        Map<PersonAddress, Integer> distanceMap = new HashMap<PersonAddress, Integer>();
        for(PersonAddress personAddress : personAddresses){
            distanceMap.put(personAddress, computeDistance(query, getFieldFrom(personAddress, field)));
        }
        return getEntryWithClosestMatch(personAddresses, distanceMap);
    }

    private int computeDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    private <T> T getEntryWithClosestMatch(List<T> suggestions, Map<T, Integer> distanceMap) {
        T bestSuggestion = suggestions.get(0);
        int initialDist = distanceMap.get(bestSuggestion);
        for(T suggestion : suggestions){
            if(distanceMap.get(suggestion) < initialDist){
                bestSuggestion = suggestion;
                initialDist = distanceMap.get(suggestion);
            }
        }
        return bestSuggestion;
    }

    private String getFieldFrom(PersonAddress personAddress, AddressField field) {
        if(field.equals(AddressField.TEHSIL))
            return personAddress.getTehsil();
        if(field.equals(AddressField.DISTRICT))
            return personAddress.getDistrict();
        if(field.equals(AddressField.STATE))
            return personAddress.getState();
        return null;
    }

}
