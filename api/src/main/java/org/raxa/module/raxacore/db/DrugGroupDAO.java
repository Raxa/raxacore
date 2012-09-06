package org.raxa.module.raxacore.db;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import java.util.List;
import org.openmrs.EncounterType;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.DrugGroup;

/**
 * Interface for accessing raxacore_patient_list
 */
public interface DrugGroupDAO {
	
	/**
	 * Saves a DrugGroup
	 *
	 * @param DrugGroup to be saved
	 * @throws DAOException @should save a patient list
	 */
	public DrugGroup saveDrugGroup(DrugGroup drugGroup) throws DAOException;
	
	/**
	 * Purge a DrugGroup from database.
	 *
	 * @param DrugGroup object to be purged
	 */
	public void deleteDrugGroup(DrugGroup drugGroup) throws DAOException;
	
	/**
	 * Get patientList by internal identifier
	 *
	 * @param patientListId patientList id
	 * @return patientList with given internal identifier
	 * @throws DAOException @should get a patient list
	 */
	public DrugGroup getDrugGroup(Integer patientListId) throws DAOException;
	
	/**
	 * Find {@link PatientList} matching a uuid
	 *
	 * @param uuid
	 * @return {@link PatientList} @should get a patient list by uuid
	 */
	public DrugGroup getDrugGroupByUuid(String uuid);
	
	/**
	 * Find {@link PatientList} matching a name
	 *
	 * @param name
	 * @return List of PatientLists @should get a patient list by name
	 */
	public List<DrugGroup> getDrugGroupByName(String name);
	
	/**
	 * Get all {@link PatientList}
	 *
	 * @return List of PatientLists @should get all patient lists
	 */
	public List<DrugGroup> getAllDrugGroup(boolean includeRetired);
	
	/**
	 * Update PatientList
	 *
	 * @return {@link PatientList} @should update a PatientList
	 */
	DrugGroup updateDrugGroup(DrugGroup drugGroup) throws DAOException;
}
