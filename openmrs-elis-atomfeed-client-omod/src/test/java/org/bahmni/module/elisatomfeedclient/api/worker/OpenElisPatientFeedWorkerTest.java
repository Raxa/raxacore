package org.bahmni.module.elisatomfeedclient.api.worker;

import org.bahmni.module.elisatomfeedclient.api.filter.OpenElisPatientFeedPrefetchFilter;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenElisPatientFeedWorkerTest {

    @Mock
    private OpenElisAccessionEventWorker accessionEventWorker;

    @Mock
    private OpenElisPatientEventWorker patientEventWorker;

    @Mock
    OpenElisPatientFeedPrefetchFilter prefetchFilter;

    @Before
    public void before() {
        initMocks(this);
    }

    @Test
    public void shouldNotCallWorkersWhenInterpreterReturnsFalse() {
        OpenElisPatientFeedWorker openElisPatientFeedWorker = new OpenElisPatientFeedWorker(patientEventWorker, accessionEventWorker, prefetchFilter);
        Event event = createEvent();

        when(prefetchFilter.allows(event)).thenReturn(false);
        openElisPatientFeedWorker.process(event);

        verify(patientEventWorker, never()).process(event);
        verify(accessionEventWorker, never()).process(event);
    }

    @Test
    public void shouldCallPatientEventWorkerWhenEventTitleIsPatient() {
        OpenElisPatientFeedWorker openElisPatientFeedWorker = new OpenElisPatientFeedWorker(patientEventWorker, accessionEventWorker, prefetchFilter);
        Event event = createEvent();

        when(prefetchFilter.allows(event)).thenReturn(true);
        openElisPatientFeedWorker.process(event);

        verify(patientEventWorker, times(1)).process(event);
        verify(accessionEventWorker, never()).process(event);
    }

    @Test
    public void shouldCallAccessionEventWorkerWhenEventTitleIsAccession() {
        OpenElisPatientFeedWorker openElisPatientFeedWorker = new OpenElisPatientFeedWorker(patientEventWorker, accessionEventWorker, prefetchFilter);
        Event event = createEvent();
        event.setTitle("accession");

        when(prefetchFilter.allows(event)).thenReturn(true);
        openElisPatientFeedWorker.process(event);

        verify(patientEventWorker, never()).process(event);
        verify(accessionEventWorker, times(1)).process(event);
    }

    @Test
    public void shouldNotFailWhenNoEventWorkerFound() {
        OpenElisPatientFeedWorker openElisPatientFeedWorker = new OpenElisPatientFeedWorker(patientEventWorker, accessionEventWorker, prefetchFilter);
        Event event = createEvent();
        event.setTitle("newAccession");

        when(prefetchFilter.allows(event)).thenReturn(true);
        openElisPatientFeedWorker.process(event);

        verify(patientEventWorker, never()).process(event);
        verify(accessionEventWorker, never()).process(event);
    }




    private Event createEvent() {
        return new Event("tag:atomfeed.ict4h.org:22617bda-71ac-45c0-832b-c945b4881334", "/openelis/ws/rest/accession/4e85095d-b08e-444b-8970-0d5c2210791b","patient", "http://localhost:8080/openelis/ws/feed/patient/recent");
    }
}
