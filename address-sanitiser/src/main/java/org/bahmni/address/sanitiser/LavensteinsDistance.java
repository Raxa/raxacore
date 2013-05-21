package org.bahmni.address.sanitiser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LavensteinsDistance {
    public LavensteinMatch<String> getClosestMatch(String query, List<String> possibleValues) {
        Map<String, Integer> distanceMap = new HashMap<String, Integer>();
        for(String village : possibleValues){
            distanceMap.put(village, computeDistance(query, village));
        }
        return getEntryWithClosestMatch(possibleValues, distanceMap);
    }

    public SanitizerPersonAddress getClosestMatch(String query, List<SanitizerPersonAddress> personAddressSanitisers, AddressField field) {
        Map<SanitizerPersonAddress, Integer> distanceMap = new HashMap<SanitizerPersonAddress, Integer>();
        for(SanitizerPersonAddress personAddressSanitiser : personAddressSanitisers){
            distanceMap.put(personAddressSanitiser, computeDistance(query, getFieldFrom(personAddressSanitiser, field)));
        }
        return getEntryWithClosestMatch(personAddressSanitisers, distanceMap).matchValue();
    }

    private <T> LavensteinMatch<T> getEntryWithClosestMatch(List<T> suggestions, Map<T, Integer> distanceMap) {
        T bestSuggestion = suggestions.get(0);
        int distance = distanceMap.get(bestSuggestion);
        for(T suggestion : suggestions){
            if(distanceMap.get(suggestion) < distance){
                bestSuggestion = suggestion;
                distance = distanceMap.get(suggestion);
            }
        }
        return new LavensteinMatch<T>(bestSuggestion, distance);
    }

    private String getFieldFrom(SanitizerPersonAddress personAddressSanitiser, AddressField field) {
        if(field.equals(AddressField.TEHSIL))
            return personAddressSanitiser.getTehsil();
        if(field.equals(AddressField.DISTRICT))
            return personAddressSanitiser.getDistrict();
        if(field.equals(AddressField.STATE))
            return personAddressSanitiser.getState();
        return null;
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

}
