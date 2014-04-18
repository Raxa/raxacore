module ConceptHelper
	def insert_concept!(concept_name, concept_shortname, concept_description, class_id, datatype_id, is_set, synonyms)
		concept_id = insert_concept(concept_name, concept_shortname, concept_description, class_id, datatype_id, is_set, synonyms)
		exit 1 if concept_id == -1
		return concept_id
	end

	def insert_concept(concept_name, concept_shortname, concept_description, class_id, datatype_id, is_set, synonyms)
	  if concept_description
	    concept_description  = "'#{concept_description}'"
	  else
	    concept_description = "null"
	  end
	  if(is_duplicate(concept_name))
	    show_error("Concept with name #{concept_name} already Exists")
	    return -1
	  end

	  @openmrs_conn.query("INSERT INTO concept (datatype_id,description, class_id, is_set, creator, date_created, changed_by, date_changed, uuid)
	    values (#{datatype_id},#{concept_description}, #{class_id}, #{is_set}, 1, now(), 1, now(), uuid());")
	  concept_id = @openmrs_conn.insert_id
	  create_concept_name(concept_shortname, concept_id,"SHORT")
	  create_concept_name(concept_name, concept_id,"FULLY_SPECIFIED")
	  create_concept_synonyms(synonyms,concept_id)
	  return concept_id
	end

	def create_concept_name (concept_name, concept_id,name_type)
	  if concept_name && concept_name.length > 0
	    @openmrs_conn.query("INSERT INTO concept_name (concept_id, name, locale, locale_preferred, creator, date_created, concept_name_type, uuid)
	                        values (#{concept_id}, '#{concept_name}', 'en', 0, 1, now(), '#{name_type}', uuid())")
	    concept_name_id = @openmrs_conn.insert_id
	    create_concept_words(concept_name,concept_name_id,concept_id)
	  end
	end

	def create_concept_synonyms (synonyms, concept_id)
	  if synonyms
	    synonyms.each do |synonym|
	      @openmrs_conn.query("INSERT INTO concept_name (concept_id, name, locale, locale_preferred, creator, date_created, concept_name_type, uuid)
	                          values (#{concept_id}, '#{synonym}', 'en', 0, 1, now(), null, uuid())")
	      concept_name_id = @openmrs_conn.insert_id
	      create_concept_words(synonym,concept_name_id,concept_id)
	    end
	  end
	end

	def create_concept_words (concept_name, concept_name_id, concept_id)
	  concept_name.split(' ').each do |word|
	    @openmrs_conn.query("insert into concept_word (word, locale, weight, concept_id, concept_name_id)
	                  values (UPPER('#{word}'), 'en', 1, #{concept_id}, #{concept_name_id})")
	  end
	end

	def get_concept_datatype_id(name)
	  puts "----concept datatype ------"
	  @openmrs_conn.query("SELECT concept_datatype_id FROM concept_datatype WHERE name = '#{name}'").each do |concept_datatype_id|
	    puts concept_datatype_id
	    return concept_datatype_id[0]
	  end
	end

	def get_concept_class_id(classname)
	  @openmrs_conn.query("SELECT concept_class_id FROM concept_class WHERE name like '#{classname}'").each do |concept_class_id|
	    return concept_class_id[0]
	  end
	  return -1
	end

	def add_to_concept_set(concept_id,concept_set_id)
	  @openmrs_conn.query("INSERT INTO concept_set (concept_id, concept_set,sort_weight,creator,date_created,uuid)
	  values (#{concept_id}, #{concept_set_id},1,1, now(),uuid())")
	end

	def get_concept_source_by_name(source_name)
	  if(!source_name || source_name.length == 0)
	    source_name="org.openmrs.module.emrapi"
	  end
	  @openmrs_conn.query("select concept_source_id from concept_reference_source where name ='#{source_name}'").each do |concept_source_id|
	    return concept_source_id[0]
	  end
	  return -1
	end

	def add_concept_answer(question_id, answer_id, sort_weight)
	  @openmrs_conn.query("INSERT INTO concept_answer (concept_id, answer_concept,sort_weight,creator,date_created,uuid)
	  values (#{question_id},#{answer_id}, #{sort_weight}, 1, now(), uuid())")
	end

	def get_concept_map_type_id_by_name(map_type)
	  @openmrs_conn.query("SELECT concept_map_type_id FROM concept_map_type WHERE name like '#{map_type}'").each do |concept_map_type_id|
	    return concept_map_type_id[0]
	  end
	  return -1
	end

	def is_duplicate(concept_name)
	  @openmrs_conn.query("Select count(*) from concept_name where name='#{concept_name}' AND concept_name_type='FULLY_SPECIFIED'").each do |count|
	    return count[0].to_i > 0
	  end
	end

	def get_concept_by_name (concept_name)
	  @openmrs_conn.query("Select concept_id from concept_name where name='#{concept_name}' AND concept_name_type='FULLY_SPECIFIED'").each do |concept_id|
	    return concept_id[0]
	  end
	  return -1
	end

	def show_error (message)
	  puts "\nERROR : row no(#{@current_row_number}) : #{message}"
	end

	def show_success (message)
	  puts "\nSuccess : row no(#{@current_row_number}) #{message}"
	end
end