package org.bahmni.module.referencedata.addresshierarchy;

import org.ict4h.atomfeed.server.service.EventService;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({Context.class, AddressHierarchyEntryEventInterceptor.class})
@RunWith(PowerMockRunner.class)
public class AddressHierarchyEntryEventInterceptorTest {
    @Mock
    private AtomFeedSpringTransactionManager atomFeedSpringTransactionManager;
    @Mock
    private EventService eventService;

    private AddressHierarchyEntryEventInterceptor publishedFeed;
    private AddressHierarchyEntry addressHierarchyEntry;

    @Before
    public void setUp() throws Exception {
        addressHierarchyEntry = new AddressHierarchyEntry();
        addressHierarchyEntry.setUuid("uuid");
        addressHierarchyEntry.setUserGeneratedId("707070");
        PowerMockito.mockStatic(Context.class);

        ArrayList<PlatformTransactionManager> platformTransactionManagers = new ArrayList<>();
        platformTransactionManagers.add(new HibernateTransactionManager());
        when(Context.getRegisteredComponents(PlatformTransactionManager.class)).thenReturn(platformTransactionManagers);
        PowerMockito.whenNew(AtomFeedSpringTransactionManager.class).withAnyArguments().thenReturn(atomFeedSpringTransactionManager);
        publishedFeed = new AddressHierarchyEntryEventInterceptor();

    }

    @Test
    public void shouldPublishUpdateEventToFeedAfterUpdateConceptOperation() throws Throwable {
        Method method = AddressHierarchyService.class.getMethod("saveAddressHierarchyEntry", AddressHierarchyEntry.class);
        Object[] objects = new Object[]{addressHierarchyEntry};

        publishedFeed.afterReturning(addressHierarchyEntry, method, objects, null);
        verify(atomFeedSpringTransactionManager).executeWithTransaction(any(AFTransactionWorkWithoutResult.class));
    }

}