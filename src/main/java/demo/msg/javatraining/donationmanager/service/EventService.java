package demo.msg.javatraining.donationmanager.service;

import demo.msg.javatraining.donationmanager.persistence.model.volManager.Event;
import demo.msg.javatraining.donationmanager.persistence.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAll(){
        return eventRepository.findAll();
    }

    public Event findById(Long id){
        return eventRepository.findEventById(id);
    }

    public Event saveEvent(Event event){
        eventRepository.save(event);
        return event;
    }

    public Event updateEvent(Long id,Event newEvent){
        Event eventToBeModified = eventRepository.findEventById(id);
        eventToBeModified.setEventName(newEvent.getEventName());
        eventToBeModified.setEventStartDate(newEvent.getEventStartDate());
        eventToBeModified.setEventEndDate(newEvent.getEventEndDate());
        eventToBeModified.setDescription(newEvent.getDescription());
        eventToBeModified.setOpenJobs(newEvent.getOpenJobs());
        eventToBeModified.setVolsRequired(newEvent.getVolsRequired());
        eventToBeModified.setVolsRegistered(newEvent.getVolsRegistered());
        eventToBeModified.setEventStatus(newEvent.getEventStatus());
        eventToBeModified.setNotes(newEvent.getNotes());
//        eventToBeModified.setEventJobs(newEvent.getEventJobs());
        eventRepository.save(eventToBeModified);
        return eventToBeModified;


    }

    public Event deleteEvent(Long id){
        Event eventToBeRemoved = eventRepository.findEventById(id);
        eventRepository.deleteById(id);
        return  eventToBeRemoved;
    }



}


