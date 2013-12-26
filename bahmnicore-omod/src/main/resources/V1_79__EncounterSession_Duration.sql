UPDATE global_property SET property_value = '1' WHERE property='"bahmni.encountersession.duration"';
insert into global_property (`property`, `property_value`, `description`, `uuid`)
values ('bahmni.encountersession.duration',
			'60',
			'Encountersession duration in minutes',
			uuid()
		);


