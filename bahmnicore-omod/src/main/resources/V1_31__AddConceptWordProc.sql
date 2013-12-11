CREATE PROCEDURE add_concept_word (concept_id INT,
                              concept_name_id INT,
                              word VARCHAR(50),
                              weight DOUBLE)
BEGIN
	INSERT INTO concept_word (word, locale, weight, concept_id, concept_name_id) values (UPPER(word), 'en', weight, concept_id, concept_name_id);
END;