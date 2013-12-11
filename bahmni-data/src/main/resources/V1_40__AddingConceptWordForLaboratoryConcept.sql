select concept_name_id, concept_id from concept_name  where name = 'Laboratory' into @concept_name_id, @concept_id;

INSERT INTO concept_word (concept_id,word,locale,concept_name_id,weight) VALUES (@concept_id, 'LABORATORY', 'en', @concept_name_id, 9.402777777777779);