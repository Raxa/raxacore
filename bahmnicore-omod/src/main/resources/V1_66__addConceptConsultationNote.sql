set @concept_id = 0;
set @concept_name_short_id = 0;
set @concept_name_full_id = 0;

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'Consultation Note', 'consultation note', 'Text', 'Misc', false);
call add_concept_word(@concept_id, @concept_name_short_id, 'CONSULTATION', '1');
call add_concept_word(@concept_id, @concept_name_short_id, 'NOTE', '1');
call add_concept_word(@concept_id, @concept_name_full_id, 'CONSULTATION', '1');
call add_concept_word(@concept_id, @concept_name_full_id, 'NOTE', '1');

call add_concept_description(@concept_id, 'Consultation Note');