package demo.msg.javatraining.donationmanager.controller.user;

import demo.msg.javatraining.donationmanager.persistence.model.volManager.Event;
import demo.msg.javatraining.donationmanager.persistence.model.volManager.SignUps;
import demo.msg.javatraining.donationmanager.service.EventService;
import demo.msg.javatraining.donationmanager.service.SignUpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/signups")
public class SignUpsController {
    @Autowired
    private SignUpsService signUpsService;

    @GetMapping("/a")
    public List<SignUps> getAllSignUps(){
        return signUpsService.getAllSignUps();
    }

    @GetMapping("/all/{id}")
    public List<SignUps> getAllSignUpsJobEvent(@PathVariable Long id){
        return signUpsService.getAllSignUpsFromEventJob(id);
    }


    @PostMapping("/new/{id}")
    public ResponseEntity<?> createSignUp(@PathVariable Long id,@RequestBody SignUps signUp){
        ResponseEntity<?> response;
        try{
            signUpsService.saveSignUp(signUp,id);
            response = new ResponseEntity<>(signUp, HttpStatusCode.valueOf(200));
        }catch(Exception exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSignUp(@PathVariable("id") Long id, @RequestBody SignUps event){
        ResponseEntity<?> response;
        try{
            signUpsService.updateSignUp(id,event);
            response = new ResponseEntity<>(event, HttpStatusCode.valueOf(200));

        }catch(Exception exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;



    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSignUp(@PathVariable("id") Long id)
    {
        ResponseEntity<?> response;
        try{
            signUpsService.deleteSignUp(id);
            response = new ResponseEntity<>(signUpsService.findById(id), HttpStatusCode.valueOf(200));

        }catch(Exception exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }
}
