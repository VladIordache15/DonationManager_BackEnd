package demo.msg.javatraining.donationmanager.controller.user;

import demo.msg.javatraining.donationmanager.persistence.model.volManager.Event;
import demo.msg.javatraining.donationmanager.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/all")
    public List<Event> getAllEvents(){
        return eventService.getAll();
    }

    @PostMapping("/new")
    public ResponseEntity<?> createEvent(@RequestBody Event event){
        ResponseEntity<?> response;
        try{
            eventService.saveEvent(event);
            response = new ResponseEntity<>(event, HttpStatusCode.valueOf(200));
        }catch(Exception exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable("id") Long id, @RequestBody Event event){
        ResponseEntity<?> response;
        try{
            eventService.updateEvent(id,event);
            response = new ResponseEntity<>(event, HttpStatusCode.valueOf(200));

        }catch(Exception exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;



    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable("id") Long id)
    {
        ResponseEntity<?> response;
        try{
            eventService.deleteEvent(id);
            response = new ResponseEntity<>(eventService.findById(id), HttpStatusCode.valueOf(200));

        }catch(Exception exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

}
