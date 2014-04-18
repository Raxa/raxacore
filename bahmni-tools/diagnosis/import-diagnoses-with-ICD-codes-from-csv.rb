#encoding: utf-8
#!/usr/bin/ruby
require 'mysql'
require 'csv'
require 'micro-optparse'
Dir.glob(File.join(File.dirname(File.absolute_path(__FILE__)), '..', 'common', '*')) {|file| require file}
include ConceptHelper

# Required Gems : ruby-mysql, micro-optparse
parser = Parser.new do |p|
   p.banner = "Usage: ruby #{__FILE__} csv_file [options]"
   p.option :host, "Host name or IP", :default => "127.0.0.1", :short => 'H'
   p.option :user, "Mysql user", :default => "openmrs-user"
   p.option :password, "Mysql password", :default => "password"
   p.option :verbose, "Verbose mode", :default => false
end
options = parser.process!

if ARGV.size < 1
  puts parser.instance_variable_get(:@optionparser)
  exit 1
end

@csv_file = ARGV.shift
@synonym_separator = '|'
@verbose = options[:verbose]

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

@openmrs_conn = Mysql.connect(options[:host], options[:user], options[:password], 'openmrs')
@current_row_number = 1

def import_from_csv
  na_datatype_id= get_concept_datatype_id('N/A')
  conv_concept_class_id = get_concept_class_id("ConvSet")
  diagnosis_concept_class_id = get_concept_class_id("Diagnosis")
  diag_concept_set_id = create_diagnosis_concept_set(conv_concept_class_id, na_datatype_id)

  if(!prerequisites_valid(na_datatype_id,conv_concept_class_id,diagnosis_concept_class_id,diag_concept_set_id))
    return
  end

  CSV.foreach(@csv_file, {:headers => true, :encoding => 'utf-8'}) do |row|
    @current_row_number = @current_row_number +1
    diagnosis_details = CSV.parse_line(row.to_s)
    diag_name =get_col(diagnosis_details, "diagnosis_name")
    diag_short_name =get_col(diagnosis_details, "short_name")
    diag_desc =get_col(diagnosis_details, "description")
    synonyms_string =get_col(diagnosis_details, "synonym")
    source =get_col(diagnosis_details, "source")
    map_type =get_col(diagnosis_details, "map_type")
    icd_code =get_col(diagnosis_details, "icd10code")
    icd_code_name =get_col(diagnosis_details, "icd10name")
    parent_concept = get_col(diagnosis_details,"parent_concept_set")
    synonyms = synonyms_string ? synonyms_string.split(@synonym_separator) : []

    if(is_valid_row(diagnosis_details))

      parent_concept_id = get_concept_by_name(parent_concept)
      if parent_concept_id ==-1
        parent_concept_id = insert_concept_without_duplicate(parent_concept,nil,nil,conv_concept_class_id,na_datatype_id,true,nil)
      end

      concept_id = insert_concept_without_duplicate(diag_name, diag_short_name, diag_desc, diagnosis_concept_class_id, na_datatype_id, false, synonyms)

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
  return insert_concept_without_duplicate(diagnosis_set_of_sets, nil, nil, conv_concept_class_id, na_datatype_id, true, nil)
end

def create_icd_code_mappings(source,map_type,icd_code,icd_code_name,concept_id)
  if icd_code_name
    icd_code_name  = "'#{icd_code_name}'"
  else
    icd_code_name = "null"
  end

  source_id = get_concept_source_by_name(source)
  map_type_id = get_concept_map_type_id_by_name(map_type)

  if source_id
    show_error("Concept reference source #{source} doesn't exist")
    return false
  end

  if map_type_id
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

import_from_csv