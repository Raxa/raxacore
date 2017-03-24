package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.BahmniPatientProgram;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.PatientProgramAttribute;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.ProgramAttributeType;
import org.openmrs.Encounter;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.ProgramWorkflowService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BahmniProgramWorkflowService extends ProgramWorkflowService {

    @Transactional(readOnly = true)
    @Authorized({"View Program Attribute Types"})
    List<ProgramAttributeType> getAllProgramAttributeTypes();

    @Transactional(readOnly = true)
    @Authorized({"View Program Attribute Types"})
    ProgramAttributeType getProgramAttributeType(Integer var1);

    @Transactional(readOnly = true)
    @Authorized({"View Program Attribute Types"})
    ProgramAttributeType getProgramAttributeTypeByUuid(String var1);

    @Authorized({"Manage Program Attribute Types"})
    ProgramAttributeType saveProgramAttributeType(ProgramAttributeType var1);

    @Authorized({"Purge Program Attribute Types"})
    void purgeProgramAttributeType(ProgramAttributeType var1);

    @Transactional(readOnly = true)
    @Authorized({"View Patient Programs"})
    PatientProgramAttribute getPatientProgramAttributeByUuid(String var1);

    @Transactional(readOnly = true)
    @Authorized({"View Patient Programs"})
    Collection<Encounter> getEncountersByPatientProgramUuid(String patientProgramUuid);

    Map<Object, Object> getPatientProgramAttributeByAttributeName(List<Integer> patients, String attributeName);

    @Transactional(readOnly = true)
    @Authorized({"View Patient Programs"})
    List<BahmniPatientProgram> getPatientProgramByAttributeNameAndValue(String attributeName, String attributeValue);
}
