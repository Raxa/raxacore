DELETE FROM global_property where property = 'emrapi.sqlGet.wardsListDetails';

INSERT INTO global_property (`property`, `property_value`, `description`, `uuid`)
VALUES ('emrapi.sqlGet.wardsListDetails',
        "SELECT
  b.bed_number                      AS 'Bed number',
  pv.name                           AS 'Patient Name',
  pi.identifier                     AS 'Identifier',
  pv.gender                         AS 'Gender',
  TIMESTAMPDIFF(YEAR,pv.birthdate,CURDATE()) AS 'Age',
  pa.county_district                AS 'County District',
  pa.city_village                   AS 'Village',
  pa.state_province                 AS 'State',
  admission_provider_name.given_name           AS 'Admission Provider',
  cast(ev.encounter_datetime AS CHAR)             AS 'Admission Date Time',
  diagnosis.value_concept_full_name AS 'Diagnosis',
  cast(diagnosis.obs_datetime AS CHAR)  AS 'Diagnosis Date Time',
  disposition_provider_name.given_name AS 'Disposition provider',
  cast(disposition.obs_datetime AS CHAR)  AS 'Disposition Date Time'
FROM bed_location_map blm
  INNER JOIN bed b ON blm.bed_id = b.bed_id
  INNER JOIN bed_patient_assignment_map bpam ON b.bed_id = bpam.bed_id AND date_stopped is NULL
  INNER JOIN patient_view pv ON pv.patient_id = bpam.patient_id
  INNER JOIN patient_identifier pi ON pv.patient_id = pi.patient_id
  INNER JOIN person_address pa ON pa.person_id = pv.patient_id
  INNER JOIN
  (SELECT ev.encounter_type_name , ev.encounter_datetime, ev.patient_id, encounter_id FROM encounter_view ev 
    INNER JOIN (SELECT patient_id,max(encounter_datetime) AS max_encounter_datetime 
                FROM encounter_view 
                WHERE encounter_type_name = 'Admission' 
                GROUP BY  patient_id) maxDate
      ON ev.patient_id = maxDate.patient_id AND ev.encounter_datetime = maxDate.max_encounter_datetime) AS ev ON ev.patient_id = pv.patient_id
  INNER JOIN encounter_provider ep ON ep.encounter_id = ev.encounter_id
  INNER JOIN provider admission_provider ON admission_provider.provider_id = ep.provider_id
  INNER JOIN person_name admission_provider_name ON admission_provider_name.person_id = admission_provider.person_id

  LEFT OUTER JOIN concept_name admit_disposition ON admit_disposition.name='Admit Patient' and admit_disposition.concept_name_type='FULLY_SPECIFIED'
  LEFT OUTER JOIN concept_name disposition_set ON disposition_set.name='Disposition' and disposition_set.concept_name_type='FULLY_SPECIFIED'
  LEFT OUTER JOIN (SELECT * from obs disposition_obs ORDER BY disposition_obs.obs_datetime DESC LIMIT 1) AS disposition
    ON disposition.person_id = bpam.patient_id AND disposition.concept_id = disposition_set.concept_id and disposition.value_coded = admit_disposition.concept_id
  LEFT OUTER JOIN encounter_provider disposition_encounter_provider ON disposition_encounter_provider.encounter_id = disposition.encounter_id
  LEFT OUTER JOIN provider disposition_provider ON disposition_provider.provider_id = disposition_encounter_provider.provider_id
  LEFT OUTER JOIN person_name disposition_provider_name ON disposition_provider_name.person_id = disposition_provider.person_id
  LEFT OUTER JOIN (
                    SELECT
                      cov.obs_id,
                      cov.person_id,
                      cov.value_concept_full_name,
                      cov.encounter_id,
                      cov.obs_datetime
                    FROM coded_obs_view cov INNER JOIN (SELECT
                                                          person_id,
                                                          max(obs_datetime) obsDateTime
                                                        FROM coded_obs_view
                                                        WHERE concept_full_name IN ('Coded Diagnosis')
                                                        GROUP BY person_id) uniqueObs
                        ON cov.person_id = uniqueObs.person_id AND cov.obs_datetime = uniqueObs.obsDateTime
                    WHERE concept_full_name IN ('Coded Diagnosis')
                    ORDER BY cov.obs_id DESC
                    LIMIT 1
                  ) diagnosis ON diagnosis.person_id = pv.patient_id
WHERE b.status = 'OCCUPIED' AND ev.encounter_type_name = 'ADMISSION' AND blm.location_id = (SELECT location_id FROM location
WHERE name =${location_name})",
        'Sql query to get list of wards',
        uuid()
);
