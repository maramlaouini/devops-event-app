/*package tn.fst.eventsproject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventsProjectApplicationTests {

    @Test
    void contextLoads() {
    }

}*/
package tn.fst.eventsproject;

import tn.fst.eventsproject.entities.Event;
import tn.fst.eventsproject.entities.Logistics;
import tn.fst.eventsproject.entities.Participant;
import tn.fst.eventsproject.repositories.ParticipantRepository;
import tn.fst.eventsproject.repositories.EventRepository;
import tn.fst.eventsproject.repositories.LogisticsRepository;
import tn.fst.eventsproject.services.EventServicesImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServicesImplTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @InjectMocks
    private EventServicesImpl eventServices;

    @Test
    void testAddParticipant() {
        Participant participant = new Participant();
        participant.setNom("Nour");

        when(participantRepository.save(participant)).thenReturn(participant);

        Participant saved = eventServices.addParticipant(participant);

        assertNotNull(saved);
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void testAddAffectEvenParticipant_WithId() {
        Participant p = new Participant();
        p.setIdPart(1);
        p.setEvents(new java.util.HashSet<>());

        Event event = new Event();
        event.setDescription("Test Event");

        when(participantRepository.findById(1)).thenReturn(java.util.Optional.of(p));
        when(eventRepository.save(event)).thenReturn(event);

        Event saved = eventServices.addAffectEvenParticipant(event, 1);

        assertNotNull(saved);
        verify(participantRepository).findById(1);
        verify(eventRepository).save(event);
    }
    @Test
    void testAddAffectEvenParticipant_WithoutId() {
        Event event = new Event();

        Participant p1 = new Participant();
        p1.setIdPart(10);
        Participant p2 = new Participant();
        p2.setIdPart(20);

        event.setParticipants(Set.of(p1, p2));

        when(participantRepository.findById(anyInt())).thenReturn(
                java.util.Optional.of(new Participant())
        );
        when(eventRepository.save(event)).thenReturn(event);

        Event saved = eventServices.addAffectEvenParticipant(event);

        assertNotNull(saved);
        verify(eventRepository, times(1)).save(event);
    }
    @Test
    void testAddAffectLog() {
        Logistics logistics = new Logistics();
        logistics.setReserve(true);

        Event event = new Event();
        event.setLogistics(new java.util.HashSet<>());

        when(eventRepository.findByDescription("event1")).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        Logistics saved = eventServices.addAffectLog(logistics, "event1");

        assertNotNull(saved);
        verify(logisticsRepository).save(logistics);
    }
    @Test
    void testGetLogisticsDates() {
        Event event = new Event();
        Logistics l = new Logistics();
        l.setReserve(true);

        event.setLogistics(Set.of(l));

        when(eventRepository.findByDateDebutBetween(any(), any()))
                .thenReturn(List.of(event));

        List<Logistics> result = eventServices.getLogisticsDates(
                java.time.LocalDate.now(),
                java.time.LocalDate.now().plusDays(5)
        );

        assertEquals(1, result.size());
    }
    @Test
    void testCalculCout() {
        Event event = new Event();
        Logistics l = new Logistics();
        l.setReserve(true);
        l.setPrixUnit(10);
        l.setQuantite(2);

        event.setLogistics(Set.of(l));
        event.setDescription("Test Event");

        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache(
                anyString(), anyString(), any()))
                .thenReturn(List.of(event));

        eventServices.calculCout();

        verify(eventRepository, times(1))
                .findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache(
                        anyString(), anyString(), any()
                );
        verify(eventRepository, times(1)).save(event);
    }
}
