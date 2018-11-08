package org.bahmni.module.bahmnicore.contract.form.helper;

import org.openmrs.Obs;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ObsUtil {

    public static List<Obs> filterFormBuilderObs(List<Obs> observations) {
        return observations != null ? observations.stream().filter(obs -> isNotBlank(obs.getFormFieldPath()))
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}
