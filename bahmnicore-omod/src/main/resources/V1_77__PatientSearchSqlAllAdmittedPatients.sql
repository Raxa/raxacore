delete from global_property where property = 'emrapi.sqlSearch.admittedPatients';

insert into global_property (`property`, `property_value`, `description`, `uuid`)
values ('emrapi.sqlSearch.admittedPatients', 
			'select distinct concat(pn.given_name,\' \', pn.family_name) as name, pi.identifier as identifier, concat("",p.uuid) as uuid, concat("",v.uuid) as activeVisitUuid from encounter e join visit v on e.visit_id = v.visit_id join person_name pn on v.patient_id = pn.person_id and pn.voided = 0 join patient_identifier pi on v.patient_id = pi.patient_id join person p on v.patient_id = p.person_id join encounter_type et on et.encounter_type_id = e.encounter_type where v.date_stopped is null and et.name = \'ADMISSION\' and e.patient_id not in (select distinct enc.patient_id from encounter enc join encounter_type ent on enc.encounter_type = ent.encounter_type_id where ent.name = \'DISCHARGE\' and enc.patient_id = v.patient_id)', 
			'Sql query to get list of admitted patients',
			uuid()
		);	