package demo.msg.javatraining.donationmanager.service;

import demo.msg.javatraining.donationmanager.persistence.model.volManager.Event;
import demo.msg.javatraining.donationmanager.persistence.model.volManager.EventJobs;
import demo.msg.javatraining.donationmanager.persistence.repository.EventJobsRepository;
import demo.msg.javatraining.donationmanager.persistence.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventJobService {

    @Autowired
    private EventJobsRepository eventJobsRepository;

    @Autowired
    private EventRepository eventRepository;

    public List<EventJobs> getAll(){
        return eventJobsRepository.findAll();
    }

    public List<EventJobs> getAllEventJobs(Long eventId){
        return eventJobsRepository.findEventJobsByEventId(eventId);
    }

    public EventJobs findById(Long id){
        return eventJobsRepository.findEventJobsById(id);
    }

    public EventJobs saveEventJobs(EventJobs eventJobs,Long id){    //trebe tratat cazul in care id-ul nu exista
        Event ev = eventRepository.findEventById(id);
        eventJobs.setEvent(ev);
        eventJobsRepository.save(eventJobs);
        return eventJobs;
    }

    public EventJobs updateEventJobs(Long id,EventJobs newEventJob){
        EventJobs eventJobsToBeModified = eventJobsRepository.findEventJobsById(id);

        eventJobsToBeModified.setJobTitle(newEventJob.getJobTitle());
        eventJobsToBeModified.setJobDescription(newEventJob.getJobDescription());
        eventJobsToBeModified.setJobStartTime(newEventJob.getJobStartTime());
        eventJobsToBeModified.setJobEndTime(newEventJob.getJobEndTime());
        eventJobsToBeModified.setVolsRequired(newEventJob.getVolsRequired());
        eventJobsToBeModified.setVolsRegistered(newEventJob.getVolsRegistered());
        eventJobsToBeModified.setVolsCheckedIn(newEventJob.getVolsCheckedIn());


        eventJobsRepository.save(eventJobsToBeModified);
        return eventJobsToBeModified;


    }

    public EventJobs deleteEvent(Long id){
        EventJobs eventJobToBeRemoved = eventJobsRepository.findEventJobsById(id);
        eventJobsRepository.deleteById(id);
        return  eventJobToBeRemoved;
    }

}
