package org.bahmni.module.bahmnicore.contract.form.mapper;

import org.bahmni.module.bahmnicore.contract.form.data.FormDetails;
import org.bahmni.module.bahmnicore.contract.form.data.Provider;
import org.bahmni.module.bahmnicore.contract.form.helper.FormType;
import org.bahmni.module.bahmnicore.contract.form.helper.FormUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.VerificationModeFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.Visit;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@PrepareForTest({FormUtil.class, FormType.class})
@RunWith(PowerMockRunner.class)
public class FormDetailsMapperTest {

    private String obsFormFieldPath = "FormName.2/1-0";
    private String encounterUuid = "encounter-Uuid";
    private String visitUuid = "visitUuid";
    private String providerName = "Super Man";
    private String providerUuid = "providerName-uuid";
    private String formName = "formName";
    private int formVersion = 2;
    private Date encounterDateTime = new Date();
    private Date visitStartDateTime = new Date();

    private Obs obs = mock(Obs.class);
    private Encounter encounter = mock(Encounter.class);
    private Visit visit = mock(Visit.class);
    private User creator = mock(User.class);
    private PersonName personName = mock(PersonName.class);

    @Before
    public void setUp() {

        mockStatic(FormUtil.class);

        when(obs.getEncounter()).thenReturn(encounter);
        when(obs.getCreator()).thenReturn(creator);
        when(obs.getFormFieldPath()).thenReturn(obsFormFieldPath);
        when(encounter.getVisit()).thenReturn(visit);
        when(FormUtil.getFormNameFromFieldPath(obsFormFieldPath)).thenReturn(formName);
        when(FormUtil.getFormVersionFromFieldPath(obsFormFieldPath)).thenReturn(formVersion);

        when(encounter.getUuid()).thenReturn(encounterUuid);
        when(encounter.getEncounterDatetime()).thenReturn(encounterDateTime);

        when(visit.getUuid()).thenReturn(visitUuid);
        when(visit.getStartDatetime()).thenReturn(visitStartDateTime);

        when(creator.getPersonName()).thenReturn(personName);
        when(personName.getFullName()).thenReturn(providerName);
        when(creator.getUuid()).thenReturn(providerUuid);
    }

    @Test
    public void shouldReturnFormDetailsFromGivenObsAndFormTypeOfFormBuilder() {

        FormType formType = mock(FormType.class);
        Whitebox.setInternalState(FormType.class, "FORM_BUILDER_FORMS", formType);
        when(formType.get()).thenReturn("v2");

        FormDetails formDetails = FormDetailsMapper.map(obs, formType);

        assertEquals("v2", formDetails.getFormType());
        assertEquals(formName, formDetails.getFormName());
        assertEquals(formVersion, formDetails.getFormVersion());
        verifyCommonData(formDetails);

        verify(obs, times(2)).getFormFieldPath();
        verifyStatic(VerificationModeFactory.times(1));
        FormUtil.getFormNameFromFieldPath(obsFormFieldPath);
        verifyStatic(VerificationModeFactory.times(1));
        FormUtil.getFormVersionFromFieldPath(obsFormFieldPath);
        verify(formType, times(1)).get();
        verifyCommonMockCalls();

    }

    @Test
    public void shouldReturnFormDetailsFromGivenObsAndFormTypeOfAllObservationTemplates() {

        FormType formType = mock(FormType.class);
        Whitebox.setInternalState(FormType.class, "ALL_OBSERVATION_TEMPLATE_FORMS", formType);
        when(formType.get()).thenReturn("v1");

        FormDetails formDetails = FormDetailsMapper.map(obs, formType);

        assertEquals("v1", formDetails.getFormType());
        assertNull(formDetails.getFormName());
        assertEquals(0, formDetails.getFormVersion());
        verifyCommonData(formDetails);

        verify(formType, times(1)).get();
        verifyCommonMockCalls();

    }

    private void verifyCommonData(FormDetails formDetails) {
        assertEquals(visitStartDateTime, formDetails.getVisitStartDateTime());
        assertEquals(visitUuid, formDetails.getVisitUuid());
        assertEquals(encounterDateTime, formDetails.getEncounterDateTime());
        assertEquals(encounterUuid, formDetails.getEncounterUuid());

        assertEquals(1, formDetails.getProviders().size());
        Provider provider = formDetails.getProviders().iterator().next();
        assertEquals(providerName, provider.getProviderName());
        assertEquals(providerUuid, provider.getUuid());
    }

    private void verifyCommonMockCalls() {
        verify(obs, times(1)).getEncounter();
        verify(obs, times(1)).getCreator();
        verify(encounter, times(1)).getVisit();
        verify(encounter, times(1)).getUuid();
        verify(encounter, times(1)).getEncounterDatetime();
        verify(visit, times(1)).getUuid();
        verify(visit, times(1)).getStartDatetime();
        verify(creator, times(1)).getPersonName();
        verify(creator, times(1)).getUuid();
        verify(personName, times(1)).getFullName();
    }
}