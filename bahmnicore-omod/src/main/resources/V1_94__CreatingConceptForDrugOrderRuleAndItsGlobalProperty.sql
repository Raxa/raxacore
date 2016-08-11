set @concept_id = 0;
set @concept_name_short_id = 0;
set @concept_name_full_id = 0;

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id,
'dosing rule', 'dosing rule', 'Text', 'Misc', TRUE);
set @concept_set_id = @concept_id;

call add_concept_description(@concept_id, 'dosing rule to be applied on drug order template when ordering a drug');

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id,
'mg/kg', 'mg/kg', 'Text', 'Misc', FALSE);

call add_concept_set_members (@concept_set_id,@concept_id,1);

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id,
'mg/m2', 'mg/m2', 'Text', 'Misc', FALSE);

call add_concept_set_members (@concept_set_id,@concept_id,1);

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id,
'customrule', 'customrule', 'Text', 'Misc', FALSE);

call add_concept_set_members (@concept_set_id,@concept_id,1);

set @concept_dosing_rules_uuid = 0;

select c.uuid into @concept_dosing_rules_uuid from concept c
  inner join concept_name cn on c.concept_id=cn.concept_id
  where cn.name='dosing rules' and cn.concept_name_type='SHORT';

insert into global_property (`property`, `property_value`, `description`, `uuid`)
  values ('order.drugDosingRuleConceptUuid',
  @concept_dosing_rules_uuid,
  'concept Uuid for maintaining rules for drug order set template from Admin->OrderSet',
  uuid()
);

