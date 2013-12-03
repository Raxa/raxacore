set @concept_id = 0;
set @answer_concept_id = 0;
set @concept_name_short_id = 0;
set @concept_name_full_id = 0;
set @concept_source_id = 0;
set @concept_map_type_id = 0;

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'VITALS_CONCEPT', 'VITALS_CONCEPT', 'N/A', 'Misc', true);
call add_concept_word(@concept_id, @concept_name_short_id, 'VITALS', 1);
set @set_concept_id = @concept_id;
