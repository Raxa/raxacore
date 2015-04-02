DELETE FROM global_property where property = 'emrapi.sqlGet.wardsListDetails';

INSERT INTO global_property (`property`, `property_value`, `description`, `uuid`)
VALUES ('emrapi.sqlGet.wardsListDetails',
"SELECT
  b.bed_number                                                                  AS 'Bed',
  concat(pn.given_name,' ',pn.family_name)                                      AS 'Name',
  pv.uuid                                                                       AS 'Patient Uuid',
  pi.identifier                                                                 AS 'Id',
  pv.gender                                                                     AS 'Gender',
  TIMESTAMPDIFF(YEAR, pv.birthdate, CURDATE())                                  AS 'Age',
  pa.county_district                                                            AS 'District',
  pa.city_village                                                               AS 'Village',
  admission_provider_name.given_name                                            AS 'Admission By',
  cast(DATE_FORMAT(ev.encounter_datetime, '%d %b %y %h:%i %p') AS CHAR)         AS 'Admission Time',
  diagnosis.diagnosisConcept                                                    AS 'Diagnosis',
  diagnosis.certainty                                                          AS 'Diagnosis Certainty',
  diagnosis.diagnosisOrder                                                      AS 'Diagnosis Order',
  diagnosis.status                                                              AS 'Diagnosis Status',
  diagnosis.diagnosis_provider                                                  AS 'Diagnosis Provider',
  cast(DATE_FORMAT(diagnosis.diagnosis_datetime, '%d %b %y %h:%i %p') AS CHAR)  AS 'Diagnosis Datetime',
  dispositionInfo.providerName                                                  AS 'Disposition By',
  cast(DATE_FORMAT(dispositionInfo.providerDate, '%d %b %y %h:%i %p') AS CHAR)  AS 'Disposition Time',
  adtNotes.value_text                                                           AS 'ADT Notes',
  v.uuid                                                                        AS 'Visit Uuid'
FROM bed_location_map blm
    INNER JOIN bed b ON blm.bed_id = b.bed_id
    INNER JOIN bed_patient_assignment_map bpam ON b.bed_id = bpam.bed_id AND date_stopped IS NULL
    INNER JOIN person pv ON pv.person_id = bpam.patient_id
    INNER JOIN person_name pn on pn.person_id = pv.person_id
    INNER JOIN patient_identifier pi ON pv.person_id = pi.patient_id
    INNER JOIN person_address pa ON pa.person_id = pv.person_id
    INNER JOIN (SELECT patient_id, max(encounter_datetime) AS max_encounter_datetime FROM encounter_view WHERE encounter_type_name = 'Admission' GROUP BY patient_id) latestAdmissionEncounter ON pv.person_id = latestAdmissionEncounter.patient_id
    INNER JOIN encounter_view ev on ev.patient_id = latestAdmissionEncounter.patient_id and ev.encounter_datetime = latestAdmissionEncounter.max_encounter_datetime
    INNER JOIN visit v on ev.visit_id = v.visit_id
    LEFT OUTER JOIN obs adtNotes on adtNotes.encounter_id = ev.encounter_id and adtNotes.voided = 0 and adtNotes.concept_id = (SELECT concept_id from concept_name where name = 'Adt Notes' and concept_name_type = 'FULLY_SPECIFIED')
    LEFT OUTER JOIN encounter_provider ep ON ep.encounter_id = ev.encounter_id
    LEFT OUTER JOIN provider admission_provider ON admission_provider.provider_id = ep.provider_id
    LEFT OUTER JOIN person_name admission_provider_name ON admission_provider_name.person_id = admission_provider.person_id
    LEFT OUTER JOIN (
        SELECT
            bpam.patient_id as person_id,
            concept_name.name as disposition,
            latestDisposition.obs_datetime as providerDate,
            person_name.given_name as providerName
        FROM bed_patient_assignment_map bpam
            INNER JOIN (SELECT person_id, max(obs_id) obs_id from obs where concept_id = (SELECT concept_id from concept_name where name = 'Disposition' and concept_name_type = 'FULLY_SPECIFIED') GROUP BY person_id) maxObsId on  maxObsId.person_id = bpam.patient_id
            INNER JOIN obs latestDisposition on maxObsId.obs_id = latestDisposition.obs_id and latestDisposition.voided = 0
            INNER JOIN concept_name on latestDisposition.value_coded = concept_name.concept_id and concept_name_type = 'FULLY_SPECIFIED'
            LEFT OUTER JOIN encounter_provider ep ON latestDisposition.encounter_id = ep.encounter_id
            LEFT OUTER JOIN provider disp_provider ON disp_provider.provider_id = ep.provider_id
            LEFT OUTER JOIN person_name ON person_name.person_id = disp_provider.person_id
        where bpam.date_stopped is null
    ) dispositionInfo on pv.person_id = dispositionInfo.person_id
    LEFT OUTER JOIN (
        select
            diagnosis.person_id as person_id,
            diagnosis.obs_id as obs_id,
            diagnosis.obs_datetime as diagnosis_datetime,
            if(diagnosisConceptName.name is not null, diagnosisConceptName.name, diagnosis.value_text) as diagnosisConcept,
            certaintyConceptName.name as certainty,
            diagnosisOrderConceptName.name as diagnosisOrder,
            diagnosisStatusConceptName.name as status,
            person_name.given_name as diagnosis_provider
        from bed_patient_assignment_map bpam
            INNER JOIN (SELECT patient_id, MAX(date_started) date_started FROM visit GROUP BY patient_id) visitStartDate on visitStartDate.patient_id = bpam.patient_id
            INNER JOIN visit latestVisit on latestVisit.patient_id = bpam.patient_id and latestVisit.date_started = visitStartDate.date_started
            INNER JOIN encounter on encounter.visit_id = latestVisit.visit_id
            INNER JOIN obs diagnosis on bpam.patient_id = diagnosis.person_id and diagnosis.voided = 0 and diagnosis.encounter_id = encounter.encounter_id
                LEFT OUTER JOIN concept_name diagnosisConceptName on diagnosis.value_coded is not null and diagnosis.value_coded = diagnosisConceptName.concept_id and diagnosisConceptName.concept_name_type='FULLY_SPECIFIED'
                LEFT OUTER JOIN encounter_provider ep ON diagnosis.encounter_id = ep.encounter_id
                LEFT OUTER JOIN provider diagnosis_provider ON diagnosis_provider.provider_id = ep.provider_id
                LEFT OUTER JOIN person_name ON person_name.person_id = diagnosis_provider.person_id
            INNER JOIN obs certainty on diagnosis.obs_group_id = certainty.obs_group_id and certainty.voided = 0 and certainty.concept_id = (select concept_id from concept_name where name = 'Diagnosis Certainty' and concept_name_type='FULLY_SPECIFIED')
                LEFT OUTER JOIN concept_name certaintyConceptName on certainty.value_coded is not null and certainty.value_coded = certaintyConceptName.concept_id and certaintyConceptName.concept_name_type='FULLY_SPECIFIED'
            INNER JOIN obs diagnosisOrder on diagnosis.obs_group_id = diagnosisOrder.obs_group_id and diagnosisOrder.voided = 0 and diagnosisOrder.concept_id = (select concept_id from concept_name where name = 'Diagnosis order' and concept_name_type='FULLY_SPECIFIED')
                LEFT OUTER JOIN concept_name diagnosisOrderConceptName on diagnosisOrder.value_coded is not null and diagnosisOrder.value_coded = diagnosisOrderConceptName.concept_id and diagnosisOrderConceptName.concept_name_type='FULLY_SPECIFIED'
            INNER JOIN obs diagnosisStatus on diagnosis.obs_group_id = diagnosisStatus.obs_group_id and diagnosisStatus.voided = 0 and diagnosisStatus.concept_id = (select concept_id from concept_name where name = 'Bahmni Diagnosis Status' and concept_name_type='FULLY_SPECIFIED')
                LEFT OUTER JOIN concept_name diagnosisStatusConceptName on diagnosisStatus.value_coded is not null and diagnosisStatus.value_coded = diagnosisStatusConceptName.concept_id and diagnosisStatusConceptName.concept_name_type='FULLY_SPECIFIED'
        where bpam.date_stopped is null and diagnosis.concept_id in (select concept_id from concept_name where name in ('Coded Diagnosis', 'Non-Coded Diagnosis') and concept_name_type='FULLY_SPECIFIED')
    ) diagnosis ON diagnosis.person_id = pv.person_id
WHERE b.status = 'OCCUPIED' AND ev.encounter_type_name = 'ADMISSION' AND blm.location_id = (SELECT location_id FROM location WHERE name =${location_name})",
'Sql query to get list of wards',
uuid()
);
