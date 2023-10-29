package demo.msg.javatraining.donationmanager.controller.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import demo.msg.javatraining.donationmanager.controller.dto.UserDTO;
import demo.msg.javatraining.donationmanager.persistence.model.volManager.Volunteer;
import demo.msg.javatraining.donationmanager.service.userService.UserException;
import demo.msg.javatraining.donationmanager.service.userService.VolunteerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteers")
public class VolunteerController {
    @Autowired
    VolunteerService volunteerService;

    @GetMapping("/all")
    public List<Volunteer> getAllVolunteers() {
        return volunteerService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVolunteerById(@PathVariable("id") Long id) {
        ResponseEntity<?> response;
        try {
            UserDTO user = volunteerService.getUserById(id);
            response = new ResponseEntity<>(user, HttpStatusCode.valueOf(200));
        } catch (UserException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @PostMapping("/new")
    public ResponseEntity<?> createVolunteer(@RequestBody Volunteer volunteer) {
        ResponseEntity<?> response;
        try {
            volunteerService.createVolunteer(volunteer);
            System.out.println("succes");
            response = new ResponseEntity<>(volunteer, HttpStatusCode.valueOf(200));
        } catch (UserException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @PutMapping("/toggle/{id}")
    public ResponseEntity<?> toggleUserActive(@PathVariable("id") Long id) {
        ResponseEntity<?> response;
        try {
            volunteerService.toggleUserActive(id);
            response = new ResponseEntity<>(id, HttpStatusCode.valueOf(200));
        } catch (UserException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateVolunteer(@PathVariable("id") Long id, @RequestBody Volunteer newVolunteer, HttpServletRequest request) {
        String usernameFromToken = extractUsernameFromToken(request);

        // Fetch the user by username
        Volunteer userFromToken = volunteerService.findByUsername(usernameFromToken);

        ResponseEntity<?> response;
        try {
            volunteerService.updateUser(userFromToken, id, newVolunteer);
            response = new ResponseEntity<>(newVolunteer, HttpStatusCode.valueOf(200));
        } catch (UserException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    public String extractUsernameFromToken(HttpServletRequest request) {
        // Extract the token from the header
        String token = request.getHeader("Authorization");

        // Decode the JWT
        DecodedJWT jwt = JWT.decode(token);

        // Extract the username
        return jwt.getClaim("sub").asString();
    }
}
