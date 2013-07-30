package org.bahmni.module.bahmnicore.model;

import org.apache.commons.lang.StringUtils;

public class NameSearchParameter {
    private String part1;
    private String part2;

    private NameSearchParameter(String part1, String part2) {
        this.part1 = part1;
        this.part2 = part2;
    }

    public static NameSearchParameter create(String value) {
        value = value == null ? "" : value.trim() ;
        String[] split = value.split(" ");
        String part1 = "";
        String part2 = "";
        if(split.length > 1) {
            for (int i = 0 ; i < split.length -1 ; i++){
                part1 += split[i] + " ";
            }
            part2 = split[split.length - 1];
        } else {
            part1 = split[0];
        }
        return new NameSearchParameter(part1.trim(), part2.trim());
    }

    public String getPart1() {
        return part1;
    }

    public String getPart2() {
        return part2;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(part1);
    }

    public boolean hasMultipleParts() {
        return StringUtils.isNotEmpty(part2);
    }

}
