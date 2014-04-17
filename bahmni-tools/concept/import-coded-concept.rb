# Required Gems : ruby-mysql, csv

#!/usr/bin/ruby
require 'mysql'
require 'csv'

@host_name = ARGV[0]
@csv_file = ARGV[1]
@synonym_separator = '|'

def openmrs_conn()
  return Mysql.connect(@host_name, 'openmrs-user', 'password', 'openmrs')
end

@openmrs_conn = openmrs_conn()

def import_from_csv
  coded_datatype_id= get_concept_datatype_id("Coded")
  na_datatype_id= get_concept_datatype_id("N/A")
  question_concept_class_id = get_concept_class_id("Question")
  misc_concept_class_id = get_concept_class_id("Misc")

  rows = CSV.read(@csv_file)
  question_concept_name = rows.shift[0]
  question_concept_id = insert_concept(question_concept_name, question_concept_name, nil, question_concept_class_id, coded_datatype_id, false, nil)
  rows.each_with_index do |row, index|
    answer_concept_name = row[0]
    answer_concept_id = insert_concept(answer_concept_name, answer_concept_name, nil, misc_concept_class_id, na_datatype_id, false, nil)
    add_concept_answer(question_concept_id, answer_concept_id, index - 1)
  end
end

def insert_concept(concept_name, concept_shortname, concept_description, class_id, datatype_id, is_set, synonyms)
  puts "----insert concept ------"
  if concept_description
    concept_description  = "'#{concept_description}'"
  else
    concept_description = "null"
  end

  @openmrs_conn.query("INSERT INTO concept (datatype_id,description, class_id, is_set, creator, date_created, changed_by, date_changed, uuid)
    values (#{datatype_id},#{concept_description}, #{class_id}, #{is_set}, 1, now(), 1, now(), uuid());")
  puts 'concept_id'
  concept_id = @openmrs_conn.insert_id
  puts concept_id
  create_concept_name(concept_shortname, concept_id,"SHORT")
  create_concept_name(concept_name, concept_id,"FULLY_SPECIFIED")
  create_concept_synonyms(synonyms,concept_id)
  return concept_id
end

def create_concept_name (concept_name, concept_id, name_type)
  if concept_name && concept_name.length > 0
    @openmrs_conn.query("INSERT INTO concept_name (concept_id, name, locale, locale_preferred, creator, date_created, concept_name_type, uuid)
                        values (#{concept_id}, '#{concept_name}', 'en', 0, 1, now(), '#{name_type}', uuid())")
    concept_name_id = @openmrs_conn.insert_id
    puts "concept Name : #{concept_name}, ID: #{concept_name_id}"
    create_concept_words(concept_name,concept_name_id,concept_id)
  end
end

def create_concept_synonyms (synonyms, concept_id)
  if synonyms and synonyms.length>0
    synonyms.split(@synonym_separator).each do |synonym|
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
  puts "----concept class ------"
  puts classname
  @openmrs_conn.query("SELECT concept_class_id FROM concept_class WHERE name like '#{classname}'").each do |concept_class_id|
    puts concept_class_id
    return concept_class_id[0]
  end
end

def add_concept_answer(question_id, answer_id, sort_weight)
  @openmrs_conn.query("INSERT INTO concept_answer (concept_id, answer_concept,sort_weight,creator,date_created,uuid)
  values (#{question_id},#{answer_id}, #{sort_weight}, 1, now(), uuid())")
end

import_from_csv