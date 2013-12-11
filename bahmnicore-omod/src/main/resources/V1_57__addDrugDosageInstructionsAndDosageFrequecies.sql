set @concept_id = 0;
set @answer_concept_id = 0;
set @concept_name_short_id = 0;
set @concept_name_full_id = 0;

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'Dosage Frequency', 'dosagefrequency', 'Coded', 'Question', false);
call add_concept_word(@concept_id, @concept_name_short_id, 'DOSAGE', '1');
call add_concept_word(@concept_id, @concept_name_short_id, 'FREQUENCY', '1');
call add_concept_word(@concept_id, @concept_name_full_id, 'DOSAGE', '1');
call add_concept_word(@concept_id, @concept_name_full_id, 'FREQUENCY', '1');

call add_concept(@answer_concept_id, @concept_name_short_id, @concept_name_full_id, 'qD', 'qd', 'Text', 'Misc', false);
call add_concept_word(@answer_concept_id, @concept_name_short_id, 'QD', '1');
call add_concept_word(@answer_concept_id, @concept_name_full_id, 'QD', '1');
call add_concept_answer(@concept_id, @answer_concept_id, 1);
call add_concept_description(@answer_concept_id, 'EVERY DAY');

call add_concept(@answer_concept_id, @concept_name_short_id, @concept_name_full_id, 'BID', 'bid', 'Text', 'Misc', false);
call add_concept_word(@answer_concept_id, @concept_name_short_id, 'BID', '1');
call add_concept_word(@answer_concept_id, @concept_name_full_id, 'BID', '1');
call add_concept_answer(@concept_id, @answer_concept_id, 2);
call add_concept_description(@answer_concept_id, 'TWICE A DAY');

call add_concept(@answer_concept_id, @concept_name_short_id, @concept_name_full_id, 'TID', 'tid', 'Text', 'Misc', false);
call add_concept_word(@answer_concept_id, @concept_name_short_id, 'TID', '1');
call add_concept_word(@answer_concept_id, @concept_name_full_id, 'TID', '1');
call add_concept_answer(@concept_id, @answer_concept_id, 3);
call add_concept_description(@answer_concept_id, 'THREE A DAY');

call add_concept(@answer_concept_id, @concept_name_short_id, @concept_name_full_id, 'QID', 'qid', 'Text', 'Misc', false);
call add_concept_word(@answer_concept_id, @concept_name_short_id, 'QID', '1');
call add_concept_word(@answer_concept_id, @concept_name_full_id, 'QID', '1');
call add_concept_answer(@concept_id, @answer_concept_id, 4);
call add_concept_description(@answer_concept_id, 'FOUR A DAY');

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'Dosage Instructions', 'dosage instructions', 'Coded', 'Question', false);
call add_concept_word(@concept_id, @concept_name_short_id, 'DOSAGE', '1');
call add_concept_word(@concept_id, @concept_name_short_id, 'INSTRUCTIONS', '1');
call add_concept_word(@concept_id, @concept_name_full_id, 'DOSAGE', '1');
call add_concept_word(@concept_id, @concept_name_full_id, 'INSTRUCTIONS', '1');

call add_concept(@answer_concept_id, @concept_name_short_id, @concept_name_full_id, 'AC', 'ac', 'Text', 'Misc', false);
call add_concept_word(@answer_concept_id, @concept_name_short_id, 'AC', '1');
call add_concept_word(@answer_concept_id, @concept_name_full_id, 'AC', '1');
call add_concept_answer(@concept_id, @answer_concept_id, 1);
call add_concept_description(@answer_concept_id, 'BEFORE A MEAL');

call add_concept(@answer_concept_id, @concept_name_short_id, @concept_name_full_id, 'PC', 'pc', 'Text', 'Misc', false);
call add_concept_word(@answer_concept_id, @concept_name_short_id, 'PC', '1');
call add_concept_word(@answer_concept_id, @concept_name_full_id, 'PC', '1');
call add_concept_answer(@concept_id, @answer_concept_id, 2);
call add_concept_description(@answer_concept_id, 'AFTER A MEAL');

call add_concept(@answer_concept_id, @concept_name_short_id, @concept_name_full_id, 'HS', 'hs', 'Text', 'Misc', false);
call add_concept_word(@answer_concept_id, @concept_name_short_id, 'HS', '1');
call add_concept_word(@answer_concept_id, @concept_name_full_id, 'HS', '1');
call add_concept_answer(@concept_id, @answer_concept_id, 3);
call add_concept_description(@answer_concept_id, 'AT BEDTIME');

