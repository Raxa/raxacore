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
end
options = parser.process!

if ARGV.size < 1
  puts parser.instance_variable_get(:@optionparser)
  exit 1
end

@csv_file = ARGV.shift
@openmrs_conn = Mysql.connect(options[:host], options[:user], options[:password], 'openmrs')

def import_from_csv
  rows = CSV.read(@csv_file)
  coded_datatype_id= get_concept_datatype_id("Coded")
  na_datatype_id= get_concept_datatype_id("N/A")
  question_concept_class_id = get_concept_class_id("Question")
  misc_concept_class_id = get_concept_class_id("Misc")
  question_concept_name = rows.shift[0]
  @current_row_number = 0
  question_concept_id = insert_concept!(question_concept_name, question_concept_name, nil, question_concept_class_id, coded_datatype_id, false, nil)
  rows.each_with_index do |row, index|
    @current_row_number = index + 1
    answer_concept_name = row[0]
    answer_concept_id = insert_concept!(answer_concept_name, answer_concept_name, nil, misc_concept_class_id, na_datatype_id, false, nil)
    add_concept_answer(question_concept_id, answer_concept_id, index - 1)
  end
end

import_from_csv