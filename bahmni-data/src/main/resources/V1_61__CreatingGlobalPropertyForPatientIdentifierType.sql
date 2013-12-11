-- JSS specific. Do not move to registration omod


UPDATE global_property SET property_value = '05a29f94-c0ed-11e2-94be-8c13b969e334' WHERE property='emr.primaryIdentifierType';

UPDATE patient_identifier_type SET uuid='05a29f94-c0ed-11e2-94be-8c13b969e334' WHERE name = 'JSS';