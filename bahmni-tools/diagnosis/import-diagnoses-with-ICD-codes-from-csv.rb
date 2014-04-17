#!/usr/bin/ruby
require 'mysql'
require 'csv'

# Requires ruby-mysql gem. Run :
# gem install ruby-mysql

# usage : ruby import-diagnoses-with-ICD-codes-from-csv.rb <host-name> </path/to/csv>

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
    'icd10name' => 7,
    'parent_concept_set'=>8
}

def openmrs_conn()
  return Mysql.connect(@host_name, 'root', 'password', 'openmrs')
end

@openmrs_conn = openmrs_conn()
@current_row_number = 1

def import_from_csv
  na_datatype_id= get_concept_datatype_id()
  conv_concept_class_id = get_concept_class_id("ConvSet")
  diagnosis_concept_class_id = get_concept_class_id("Diagnosis")
  diag_concept_set_id = create_diagnosis_concept_set(conv_concept_class_id, na_datatype_id)

  if(!prerequisites_valid(na_datatype_id,conv_concept_class_id,diagnosis_concept_class_id,diag_concept_set_id))
    return
  end

  CSV.foreach(@csv_file, {:headers => true}) do |row|
    @current_row_number = @current_row_number +1
    diagnosis_details = CSV.parse_line(row.to_s)
    diag_name =get_col(diagnosis_details, "diagnosis_name")
    diag_short_name =get_col(diagnosis_details, "short_name")
    diag_desc =get_col(diagnosis_details, "description")
    synonyms =get_col(diagnosis_details, "synonym")
    source =get_col(diagnosis_details, "source")
    map_type =get_col(diagnosis_details, "map_type")
    icd_code =get_col(diagnosis_details, "icd10code")
    icd_code_name =get_col(diagnosis_details, "icd10name")
    parent_concept = get_col(diagnosis_details,"parent_concept_set")

    if(is_valid_row(diagnosis_details))

      parent_concept_id = get_concept_by_name(parent_concept)
      if parent_concept_id ==-1
        parent_concept_id = insert_concept(parent_concept,nil,nil,conv_concept_class_id,na_datatype_id,true,nil)
      end

      concept_id = insert_concept(diag_name, diag_short_name, diag_desc, diagnosis_concept_class_id, na_datatype_id, false, synonyms)

      if concept_id != -1
        add_to_concept_set(concept_id,parent_concept_id)
        mappings_created = create_icd_code_mappings(source,map_type,icd_code,icd_code_name,concept_id)

        if mappings_created
          show_success ("inserted : #{diagnosis_details.to_s}")
        end
      end

    end
  end
  update_global_property(diag_concept_set_id)
end


def get_col (row, col_name)
  return row[@col_to_index_mapping[col_name]]
end

def create_diagnosis_concept_set (conv_concept_class_id, na_datatype_id)
  diagnosis_set_of_sets = "Diagnosis Set of Sets"
  diagnosis_concept_set_id  = get_concept_by_name(diagnosis_set_of_sets)

  if diagnosis_concept_set_id != -1
    return diagnosis_concept_set_id
  end
  return insert_concept(diagnosis_set_of_sets, nil, nil, conv_concept_class_id, na_datatype_id, true, nil)
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
  @openmrs_conn.query("SELECT concept_datatype_id FROM concept_datatype WHERE name = 'N/A'").each do |concept_datatype_id|
    return concept_datatype_id[0]
  end
  return -1
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

def get_concept_map_type_id_by_name(map_type)
  @openmrs_conn.query("SELECT concept_map_type_id FROM concept_map_type WHERE name like '#{map_type}'").each do |concept_map_type_id|
    return concept_map_type_id[0]
  end
  return -1
end

def create_icd_code_mappings(source,map_type,icd_code,icd_code_name,concept_id)
  if icd_code_name
    icd_code_name  = "'#{icd_code_name}'"
  else
    icd_code_name = "null"
  end

  source_id = get_concept_source_by_name(source)
  map_type_id = get_concept_map_type_id_by_name(map_type)

  if source_id == -1
    show_error("Concept reference source #{source} doesn't exist")
    return false
  end

  if map_type_id == -1
    show_error("Concept reference term mapping type #{map_type} doesn't exist")
    return false
  end

  @openmrs_conn.query("INSERT INTO concept_reference_term (concept_source_id,code,name,creator,date_created,uuid)
                      VALUES (#{source_id},'#{icd_code}',#{icd_code_name},1,now(),uuid())")
  map_term_id = @openmrs_conn.insert_id

  @openmrs_conn.query("INSERT INTO concept_reference_map(concept_reference_term_id,concept_map_type_id,creator,date_created,concept_id,uuid)
                      VALUES(#{map_term_id}, #{map_type_id}, 1, now(), #{concept_id}, uuid())")

  return true
end

def update_global_property(concept_set_id)
  diagnosis_set_uuid = ''
  @openmrs_conn.query("Select uuid from concept where concept_id=#{concept_set_id}").each do |concept_id|
    diagnosis_set_uuid= concept_id[0]
  end

  @openmrs_conn.query("update global_property set property_value='#{diagnosis_set_uuid}' where property ='emr.concept.diagnosisSetOfSets'")
end

def is_valid_row(diagnosis_details)
  diag_name =get_col(diagnosis_details, "diagnosis_name")
  source =get_col(diagnosis_details, "source")
  map_type =get_col(diagnosis_details, "map_type")
  icd_code =get_col(diagnosis_details, "icd10code")
  parent_concept = get_col(diagnosis_details,"parent_concept_set")

  if !diag_name || diag_name.empty?
    show_error("Diagnosis Name cannot be empty \n\t #{diagnosis_details.to_s}")
    return false
  end

  if !source || source.empty?
    show_error("Concept reference Source cannot be empty \n\t #{diagnosis_details.to_s}")
    return false
  end

  if !map_type || map_type.empty?
    show_error("Concept mapping type cannot be empty \n\t #{diagnosis_details.to_s}")
    return false
  end
  if !icd_code || icd_code.empty?
    show_error("ICD code cannot be empty \n\t #{diagnosis_details.to_s}")
    return false
  end

  if !parent_concept || parent_concept.empty?
    show_error("Parent concept set cannot be empty \n\t #{diagnosis_details.to_s}")
    return false
  end

  return true
end

def prerequisites_valid(datatype_id,conv_concept_class_id,diag_concept_class_id,diag_concept_set_of_sets_id)

  if !datatype_id || datatype_id ==-1
    show_error("N/A concept datatype id not found")
    return false
  end

  if !conv_concept_class_id || conv_concept_class_id == -1
    show_error("ConvSet concept class id not found")
    return false
  end

  if !diag_concept_class_id || diag_concept_class_id == -1
    show_error("Diagnosis concept class id not found ")
    return false
  end
  if !diag_concept_set_of_sets_id || diag_concept_set_of_sets_id == -1
    show_error("Diagnosis Set of Sets concept not found  : #{diag_concept_set_of_sets_id}")
    return false
  end

  return true
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
import_from_csv