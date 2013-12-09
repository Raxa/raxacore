set @concept_id = 0;
set @concept_name_short_id = 0;
set @concept_name_full_id = 0;

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'DISCHARGE', 'DISCHARGE', 'N/A', 'Misc', true);
call add_concept_word(@concept_id, @concept_name_short_id, 'DISCHARGE', 1);
