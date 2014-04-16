#!/usr/bin/ruby
require 'mysql'
require 'csv'

@host_name = ARGV[0]
@csv_file = ARGV[1]
@synonym_separator = '|'

@col_to_index_mapping = {
    "diagnosis_name" => 0,
    'synonym' => 1,
    'short_name' => 2,
    'description' => 3,
    'source' => 4,
    'map_type' => 5,
    'icd10code' => 6,
    'icd10name' => 7
}

def openmrs_conn()
  return Mysql.connect(@host_name, 'root', 'password', 'openmrs')
end

@openmrs_conn = openmrs_conn()


def import_from_csv
  na_datatype_id= get_concept_datatype_id()
  conv_concept_class_id = get_concept_class_id("ConvSet")
  diagnosis_concept_class_id = get_concept_class_id("Diagnosis")
  diag_concept_set_id = create_diagnosis_concept_set(conv_concept_class_id, na_datatype_id)

  CSV.foreach(@csv_file, {:headers => true}) do |row|
    diagnosis_details = CSV.parse_line(row.to_s)
    diag_name =get_col(diagnosis_details, "diagnosis_name")
    diag_short_name =get_col(diagnosis_details, "short_name")
    diag_desc =get_col(diagnosis_details, "description")
    synonyms =get_col(diagnosis_details, "synonym")
    source =get_col(diagnosis_details, "source")
    map_type =get_col(diagnosis_details, "map_type")
    icd_code =get_col(diagnosis_details, "icd10code")
    icd_code_name =get_col(diagnosis_details, "icd10name")

    concept_id = insert_concept(diag_name, diag_short_name, diag_desc, diagnosis_concept_class_id, na_datatype_id, false, synonyms)
    add_to_concept_set(concept_id,diag_concept_set_id)
    create_icd_code_mappings(source,map_type,icd_code,icd_code_name,concept_id)
  end
  update_global_property(diag_concept_set_id)
end

def get_col (row, col_name)
  return row[@col_to_index_mapping[col_name]]
end

def create_diagnosis_concept_set (conv_concept_class_id, na_datatype_id)
  return insert_concept("Diagnosis Set", nil, nil, conv_concept_class_id, na_datatype_id, true, nil)
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

def create_concept_name (concept_name, concept_id,name_type)
  if concept_name && concept_name.length > 0
    @openmrs_conn.query("INSERT INTO concept_name (concept_id, name, locale, locale_preferred, creator, date_created, concept_name_type, uuid)
                        values (#{concept_id}, '#{concept_name}', 'en', 0, 1, now(), '#{name_type}', uuid())")
    concept_name_id = @openmrs_conn.insert_id
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


def get_concept_datatype_id
  puts "----concept datatype ------"
  @openmrs_conn.query("SELECT concept_datatype_id FROM concept_datatype WHERE name = 'N/A'").each do |concept_datatype_id|
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

def add_to_concept_set(concept_id,concept_set_id)
  @openmrs_conn.query("INSERT INTO concept_set (concept_id, concept_set,sort_weight,creator,date_created,uuid)
  values (#{concept_id}, #{concept_set_id},1,1, now(),uuid())")
end

def get_concept_source_by_name(source_name)
  puts "----concept source ------"
  if(!source_name || source_name.length == 0)
    source_name="org.openmrs.module.emrapi"
  end
  puts source_name
  @openmrs_conn.query("select concept_source_id from concept_reference_source where name ='#{source_name}'").each do |concept_source_id|
    puts concept_source_id
    return concept_source_id[0]
  end
end

def get_concept_map_type_id_by_name(map_type)
  puts "----concept map type ------"
  puts map_type
  @openmrs_conn.query("SELECT concept_map_type_id FROM concept_map_type WHERE name like '#{map_type}'").each do |concept_map_type_id|
    puts concept_map_type_id
    return concept_map_type_id[0]
  end
end

def create_icd_code_mappings(source,map_type,icd_code,icd_code_name,concept_id)
  puts "---- icd code ------"
  if icd_code_name
    icd_code_name  = "'#{icd_code_name}'"
  else
    icd_code_name = "null"
  end

  puts icd_code

  source_id = get_concept_source_by_name(source)
  map_type_id = get_concept_map_type_id_by_name(map_type)

  @openmrs_conn.query("INSERT INTO concept_reference_term (concept_source_id,code,name,creator,date_created,uuid)
                      VALUES (#{source_id},'#{icd_code}',#{icd_code_name},1,now(),uuid())")
  map_term_id = @openmrs_conn.insert_id

  @openmrs_conn.query("INSERT INTO concept_reference_map(concept_reference_term_id,concept_map_type_id,creator,date_created,concept_id,uuid)
                      VALUES(#{map_term_id}, #{map_type_id}, 1, now(), #{concept_id}, uuid())")

end


def update_global_property(concept_set_id)
  diagnosis_set_uuid = '';
  @openmrs_conn.query("Select uuid from concept where concept_id=#{concept_set_id}").each do |concept_id|
    diagnosis_set_uuid= concept_id[0]
  end

  @openmrs_conn.query("update global_property set property_value='#{diagnosis_set_uuid}' where property ='emr.concept.diagnosisSetOfSets'")
end

import_from_csv