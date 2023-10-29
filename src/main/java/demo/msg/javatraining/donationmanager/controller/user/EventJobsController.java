package demo.msg.javatraining.donationmanager.controller.user;

import demo.msg.javatraining.donationmanager.persistence.model.volManager.Event;
import demo.msg.javatraining.donationmanager.persistence.model.volManager.EventJobs;
import demo.msg.javatraining.donationmanager.service.EventJobService;
import demo.msg.javatraining.donationmanager.service.userService.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventJobs")
public class EventJobsController {
    @Autowired
    private EventJobService eventJobService;

    @GetMapping("/event/{id}")
    public List<EventJobs> getAllEventJobs(@PathVariable("id") Long id){
        return eventJobService.getAllEventJobs(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventJobById(@PathVariable Long id){
        ResponseEntity<?> response;
        try{
            EventJobs event = eventJobService.findById(id);
            response = new ResponseEntity<>(event, HttpStatusCode.valueOf(200));

        }catch (Exception exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;

    }

    @PostMapping("/new/{id}")
    public ResponseEntity<?> createEventJob(@PathVariable Long id,@RequestBody EventJobs event){
        ResponseEntity<?> response;
        try{
            eventJobService.saveEventJobs(event,id);
            response = new ResponseEntity<>(event, HttpStatusCode.valueOf(200));
        }catch(Exception exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @PutMapping("/update/{id}") //eventId is not needed
    public ResponseEntity<?> updateEventJob(@PathVariable("id") Long id, @RequestBody EventJobs event){
        ResponseEntity<?> response;
        try{
            eventJobService.updateEventJobs(id,event);
            response = new ResponseEntity<>(event, HttpStatusCode.valueOf(200));

        }catch(Exception exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;



    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEventJob(@PathVariable("id") Long id)
    {
        ResponseEntity<?> response;
        try{
            eventJobService.deleteEvent(id);
            response = new ResponseEntity<>(eventJobService.findById(id), HttpStatusCode.valueOf(200));

        }catch(Exception exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }
}
