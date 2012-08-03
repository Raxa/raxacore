package org.raxa.module.raxacore;

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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.util.PrivilegeConstants;
import org.raxa.module.raxacore.db.DrugInfoDAO;
import org.springframework.transaction.annotation.Transactional;

/*
 * Interface for interacting with the DrugInfo
 */
@Transactional
public interface DrugInfoService extends OpenmrsService {

    public void setDrugInfoDAO(DrugInfoDAO dao);

    // @Authorized({"Add Patient Lists"}) // TODO: Permissions
    public DrugInfo saveDrugInfo(DrugInfo drugInfo);

    /**
     * Gets a DrugInfo by Id
     *
     * @param id
     * @return PatientLists
     */
    // @Authorized({"View Patient Lists"}) // TODO: Permissions
    public DrugInfo getDrugInfo(Integer id);

    /**
     * Gets DrugInfo by uuid
     *
     * @param uuid
     * @return PatientList
     */
    // @Authorized({"View Patient Lists"}) // TODO: Permissions
    public DrugInfo getDrugInfoByUuid(String uuid);

    /**
     * Updates DrugInfo
     *
     * @param patientList
     * @return DrugInfo
     */
    // @Authorized({"Edit Patient Lists"}) // TODO: Permissions
    DrugInfo updateDrugInfo(DrugInfo drugInfo);

    /**
     * Deletes DrugInfo
     *
     * @param DrugInfo
     */
    // @Authorized({"Delete Patient Lists"}) // TODO: Permissions
    public void deleteDrugInfo(DrugInfo drugInfo);
}
