DELETE FROM global_property
WHERE property IN (
  'emrapi.sqlSearch.activePatients',
  'emrapi.sqlSearch.activePatientsByProvider'
);

INSERT INTO global_property (`property`, `property_value`, `description`, `uuid`)
VALUES ('emrapi.sqlSearch.activePatients',
        'select distinct
          concat(pn.given_name,\' \', pn.family_name) as name,
          pi.identifier as identifier,
          concat("",p.uuid) as uuid,
          concat("",v.uuid) as activeVisitUuid,
          IF(va.value_reference = "Admitted", "true", "false") as hasBeenAdmitted
        from visit v
        join person_name pn on v.patient_id = pn.person_id and pn.voided = 0
        join patient_identifier pi on v.patient_id = pi.patient_id
        join person p on p.person_id = v.patient_id
        left outer join visit_attribute va on va.visit_id = v.visit_id and va.attribute_type_id = (
          select visit_attribute_type_id from visit_attribute_type where name="Admission Status"
        ) and va.voided = 0
        where v.date_stopped is null AND v.voided = 0',
        'Sql query to get list of active patients',
        uuid()
);

insert into global_property (`property`, `property_value`, `description`, `uuid`)
values ('emrapi.sqlSearch.activePatientsByProvider','
  select distinct concat(pn.given_name," ", pn.family_name) as name,
  pi.identifier as identifier,
  concat("",p.uuid) as uuid,
  concat("",v.uuid) as activeVisitUuid,
  IF(va.value_reference = "Admitted", "true", "false") as hasBeenAdmitted
  from
    visit v join person_name pn on v.patient_id = pn.person_id and pn.voided = 0 and v.voided=0
    join patient_identifier pi on v.patient_id = pi.patient_id and pi.voided=0
    join person p on p.person_id = v.patient_id  and p.voided=0
    join encounter en on en.visit_id = v.visit_id and en.voided=0
    join encounter_provider ep on ep.encounter_id = en.encounter_id  and ep.voided=0
    join provider pr on ep.provider_id=pr.provider_id and pr.retired=0
    join person per on pr.person_id=per.person_id and per.voided=0
    left outer join visit_attribute va on va.visit_id = v.visit_id and va.attribute_type_id = (
				select visit_attribute_type_id from visit_attribute_type where name="Admission Status"
			) and va.voided = 0
  where
    v.date_stopped is null and
    pr.uuid=${provider_uuid}
    order by en.encounter_datetime desc',
    'Sql query to get list of active patients by provider uuid',
  uuid()
);