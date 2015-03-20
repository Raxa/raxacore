DELETE FROM global_property where property = 'emrapi.sqlGet.wardsListDetails';

INSERT INTO global_property (`property`, `property_value`, `description`, `uuid`)
VALUES ('emrapi.sqlGet.wardsListDetails',
        "SELECT
  b.bed_number                                                          AS 'Bed',
  pv.name                                                               AS 'Name',
  pi.identifier                                                         AS 'Id',
  pv.gender                                                             AS 'Gender',
  TIMESTAMPDIFF(YEAR, pv.birthdate, CURDATE())                          AS 'Age',
  pa.county_district                                                    AS 'County District',
  pa.city_village                                                       AS 'Village',
  admission_provider_name.given_name                                    AS 'Admission By',
  cast(DATE_FORMAT(ev.encounter_datetime, '%d %b %y %h:%i %p') AS CHAR)    AS 'Admission Time',
  diagnosis.diagnosisConcept                                            AS 'Diagnosis',
  diagnosis.providerName                                                AS 'Diagnosis By',
  cast(DATE_FORMAT(diagnosis.providerDate, '%d %b %y %h:%i %p') AS CHAR)   AS 'Diagnosis Time',
  disposition.providerName                                              AS 'Disposition By',
  cast(DATE_FORMAT(disposition.providerDate, '%d %b %y %h:%i %p') AS CHAR) AS 'Disposition Time'
FROM bed_location_map blm
  INNER JOIN bed b ON blm.bed_id = b.bed_id
  INNER JOIN bed_patient_assignment_map bpam ON b.bed_id = bpam.bed_id AND date_stopped IS NULL
  INNER JOIN patient_view pv ON pv.patient_id = bpam.patient_id
  INNER JOIN patient_identifier pi ON pv.patient_id = pi.patient_id
  INNER JOIN person_address pa ON pa.person_id = pv.patient_id
  INNER JOIN
  (SELECT
     ev.encounter_type_name,
     ev.encounter_datetime,
     ev.patient_id,
     encounter_id
   FROM encounter_view ev
     INNER JOIN (SELECT
                   patient_id,
                   max(encounter_datetime) AS max_encounter_datetime
                 FROM encounter_view
                 WHERE encounter_type_name = 'Admission'
                 GROUP BY patient_id) maxDate
       ON ev.patient_id = maxDate.patient_id AND ev.encounter_datetime = maxDate.max_encounter_datetime) AS ev
    ON ev.patient_id = pv.patient_id
  LEFT OUTER JOIN encounter_provider ep ON ep.encounter_id = ev.encounter_id
  LEFT OUTER JOIN provider admission_provider ON admission_provider.provider_id = ep.provider_id
  LEFT OUTER JOIN person_name admission_provider_name ON admission_provider_name.person_id = admission_provider.person_id
  LEFT OUTER JOIN (
                    SELECT
                      obs.person_id,
                      obs.obs_datetime AS providerDate,
                      p.given_name     AS providerName
                    FROM obs
                      INNER JOIN (
                                   SELECT
                                     p.person_id,
                                     max(obs.obs_id) AS latestObsId
                                   FROM obs obs
                                     INNER JOIN person p ON obs.person_id = p.person_id
                                     INNER JOIN concept_name cn
                                       ON obs.concept_id = cn.concept_id AND cn.concept_name_type = 'FULLY_SPECIFIED'
                                     INNER JOIN concept_reference_map crm ON crm.concept_id = cn.concept_id
                                     INNER JOIN concept_reference_term crt
                                       ON crt.concept_reference_term_id = crm.concept_reference_term_id AND
                                          crt.code IN ('Disposition')
                                     INNER JOIN concept_name admitConcept ON obs.value_coded = admitConcept.concept_id
                                                                             AND admitConcept.concept_name_type =
                                                                                 'FULLY_SPECIFIED' AND
                                                                             admitConcept.name = 'Admit Patient'
                                   GROUP BY p.person_id
                                 ) latestDiagnosis
                        ON obs.obs_id = latestDiagnosis.latestObsId AND obs.person_id = latestDiagnosis.person_id
                      LEFT OUTER JOIN encounter_provider ep ON obs.encounter_id = ep.encounter_id
                      LEFT OUTER JOIN provider disp_provider ON disp_provider.provider_id = ep.provider_id
                      LEFT OUTER JOIN person_name p ON p.person_id = disp_provider.person_id
                  ) disposition ON pv.patient_id = disposition.person_id
  LEFT OUTER JOIN (
                    SELECT
                      obs.person_id,
                      obs.obs_id,
                      IF(valueCodedCname.concept_id IS NULL, obs.value_text, valueCodedCname.name) diagnosisConcept,
                      obs.obs_datetime AS                                                          providerDate,
                      p.given_name     AS                                                          providerName
                    FROM obs
                      INNER JOIN (
                                   SELECT
                                     p.person_id,
                                     max(obs.obs_id) AS latestObsId
                                   FROM obs obs
                                     INNER JOIN person p ON obs.person_id = p.person_id
                                     INNER JOIN concept_name cn
                                       ON obs.concept_id = cn.concept_id AND cn.concept_name_type = 'FULLY_SPECIFIED'
                                     INNER JOIN concept_reference_map crm ON crm.concept_id = cn.concept_id
                                     INNER JOIN concept_reference_term crt
                                       ON crt.concept_reference_term_id = crm.concept_reference_term_id
                                   WHERE crt.code IN ('Coded Diagnosis', 'Non-Coded Diagnosis')
                                   GROUP BY p.person_id
                                 ) latestDiagnosis
                        ON obs.obs_id = latestDiagnosis.latestObsId AND obs.person_id = latestDiagnosis.person_id
                      LEFT OUTER JOIN  encounter_provider ep ON obs.encounter_id = ep.encounter_id
                      LEFT OUTER JOIN provider diag_provider ON diag_provider.provider_id = ep.provider_id
                      LEFT OUTER JOIN person_name p ON p.person_id = diag_provider.person_id
                      LEFT OUTER JOIN concept_name valueCodedCname ON obs.value_coded = valueCodedCname.concept_id AND
                                                                      valueCodedCname.concept_name_type =
                                                                      'FULLY_SPECIFIED'
                  ) diagnosis ON diagnosis.person_id = pv.patient_id
WHERE b.status = 'OCCUPIED' AND ev.encounter_type_name = 'ADMISSION' AND blm.location_id = (SELECT location_id FROM location
WHERE name =${location_name})",
        'Sql query to get list of wards',
        uuid()
);
