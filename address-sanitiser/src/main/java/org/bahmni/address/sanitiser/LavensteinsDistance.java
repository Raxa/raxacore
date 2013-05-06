package org.bahmni.address.sanitiser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LavensteinsDistance {
    private List<String> villages;
    private AddressHierarchy addressHierarchy;

    public LavensteinsDistance(AddressHierarchy addressHierarchy) {
        this.addressHierarchy = addressHierarchy;
        villages = addressHierarchy.getAllVillages();
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

    public String getClosestMatch(String s1) {
        Map<String, Integer> distanceMap = new HashMap<String, Integer>();
        for(String village : villages){
            distanceMap.put(village, computeDistance(s1, village));
        }
        String villageSuggestion = villages.get(0);
        int initialDist = distanceMap.get(villageSuggestion);
        for(String village : villages){
            if(distanceMap.get(village) < initialDist){
                villageSuggestion = village;
                initialDist = distanceMap.get(village);
            }
        }
        return villageSuggestion;
    }

}
