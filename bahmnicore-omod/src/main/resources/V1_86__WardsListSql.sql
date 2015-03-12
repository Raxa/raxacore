DELETE FROM global_property where property = 'emrapi.sqlGet.wardsListDetails';

INSERT INTO global_property (`property`, `property_value`, `description`, `uuid`)
VALUES ('emrapi.sqlGet.wardsListDetails',
        'SELECT
  b.bed_number                      AS BedNumber,
  pv.name                           AS PatientName,
  pi.identifier                     AS Identifier,
  pv.gender                         AS Gender,
  pv.birthdate                      AS BirthDate,
  pa.county_district                AS CountyDistrict,
  pa.city_village                   AS Village,
  pa.state_province                 AS State,
  ev.encounter_datetime             AS AdmissionDateTime,
  providerName.given_name           AS Provider,
  providerName.date_created         AS ProviderDate,
  diagnosis.value_concept_full_name AS Diagnosis
FROM bed_location_map blm
  INNER JOIN bed b ON blm.bed_id = b.bed_id
  INNER JOIN bed_patient_assignment_map bpam ON b.bed_id = bpam.bed_id
  INNER JOIN patient_view pv ON pv.patient_id = bpam.patient_id
  INNER JOIN patient_identifier pi ON pv.patient_id = pi.patient_id
  INNER JOIN person_address pa ON pa.person_id = pv.patient_id
  INNER JOIN encounter_view ev ON ev.patient_id = pv.patient_id
  INNER JOIN encounter_provider ep ON ep.encounter_id = ev.encounter_id
  INNER JOIN provider p ON p.provider_id = ep.provider_id
  INNER JOIN person_name providerName ON providerName.person_id = p.person_id
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
                                                        WHERE concept_full_name IN (''Coded Diagnosis'')
                                                        GROUP BY person_id) uniqueObs
                        ON cov.person_id = uniqueObs.person_id AND cov.obs_datetime = uniqueObs.obsDateTime
                    WHERE concept_full_name IN (''Coded Diagnosis'')
                    ORDER BY cov.obs_id DESC
                    LIMIT 1
                  ) diagnosis ON diagnosis.person_id = pv.patient_id
WHERE b.status = ''OCCUPIED'' AND ev.encounter_type_name = ''ADMISSION'' and blm.location_id = (select location_id from location
WHERE name =${location_name})
',
        'Sql query to get list of wards',
        uuid()
);