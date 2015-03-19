DELETE FROM global_property where property = 'emrapi.sqlGet.wardsListDetails';

INSERT INTO global_property (`property`, `property_value`, `description`, `uuid`)
VALUES ('emrapi.sqlGet.wardsListDetails',
        "SELECT
  b.bed_number                      AS 'Bed',
  pv.name                           AS 'Name',
  pi.identifier                     AS 'Id',
  pv.gender                         AS 'Gender',
  TIMESTAMPDIFF(YEAR,pv.birthdate,CURDATE()) AS 'Age',
  pa.county_district                AS 'County District',
  pa.city_village                   AS 'Village',
  pa.state_province                 AS 'State',
  admission_provider_name.given_name           AS 'Admission By',
  cast(DATE_FORMAT(ev.encounter_datetime , '%d-%m-%Y %H:%i') AS CHAR)  AS 'Admission Time',
  diagnosis.diagnosisConcept AS 'Diagnosis',
  diagnosis.providerName AS 'Diagnosis By',
  cast(DATE_FORMAT(diagnosis.providerDate, '%d-%m-%Y %H:%i') AS CHAR)  AS 'Diagnosis Time',
  disposition.providerName AS 'Disposition By',
  cast(DATE_FORMAT(disposition.providerDate, '%d-%m-%Y %H:%i') AS CHAR)  AS 'Disposition Time'
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
  LEFT OUTER JOIN (
    select obs.person_id, obs.obs_datetime as providerDate,p.given_name as providerName
    from obs inner join (
                          select p.person_id,max(obs.obs_id) as latestObsId
                          from obs obs
                            INNER JOIN person p on obs.person_id = p.person_id
                            inner join concept_name cn on obs.concept_id = cn.concept_id and cn.concept_name_type = 'FULLY_SPECIFIED'
                            inner join concept_reference_map crm on crm.concept_id = cn.concept_id
                            inner join concept_reference_term crt on crt.concept_reference_term_id = crm.concept_reference_term_id and crt.code in ('Disposition')
                            INNER JOIN concept_name admitConcept on obs.value_coded = admitConcept.concept_id and admitConcept.concept_name_type = 'FULLY_SPECIFIED' and admitConcept.name = 'Admit Patient'
                          group by p.person_id
                        ) latestDiagnosis on obs.obs_id = latestDiagnosis.latestObsId and obs.person_id = latestDiagnosis.person_id
      INNER JOIN encounter_provider ep on obs.encounter_id = ep.encounter_id
      INNER JOIN person_name p on p.person_id = ep.provider_id
    ) disposition on pv.patient_id = disposition.person_id
  LEFT OUTER JOIN (
                    select obs.person_id, obs.obs_id,IF(valueCodedCname.concept_id is null, obs.value_text , valueCodedCname.name) diagnosisConcept, obs.obs_datetime as providerDate,p.given_name as providerName 
                    from obs inner join (
                                          select p.person_id,max(obs.obs_id) as latestObsId
                                          from obs obs
                                            INNER JOIN person p on obs.person_id = p.person_id
                                            inner join concept_name cn on obs.concept_id = cn.concept_id and cn.concept_name_type = 'FULLY_SPECIFIED'
                                            inner join concept_reference_map crm on crm.concept_id = cn.concept_id
                                            inner join concept_reference_term crt on crt.concept_reference_term_id = crm.concept_reference_term_id
                                          where crt.code in ('Coded Diagnosis','Non-Coded Diagnosis')
                                          group by p.person_id
                                        ) latestDiagnosis on obs.obs_id = latestDiagnosis.latestObsId and obs.person_id = latestDiagnosis.person_id
                      INNER JOIN encounter_provider ep on obs.encounter_id = ep.encounter_id
                      INNER JOIN person_name p on p.person_id = ep.provider_id
                      LEFT OUTER JOIN concept_name valueCodedCname on obs.value_coded = valueCodedCname.concept_id and valueCodedCname.concept_name_type = 'FULLY_SPECIFIED'
                  ) diagnosis ON diagnosis.person_id = pv.patient_id
WHERE b.status = 'OCCUPIED' AND ev.encounter_type_name = 'ADMISSION' AND blm.location_id = (SELECT location_id FROM location
WHERE name =${location_name})",
        'Sql query to get list of wards',
        uuid()
);
