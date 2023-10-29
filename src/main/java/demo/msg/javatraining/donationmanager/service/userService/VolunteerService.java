package demo.msg.javatraining.donationmanager.service.userService;


import demo.msg.javatraining.donationmanager.controller.dto.UserDTO;
import demo.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import demo.msg.javatraining.donationmanager.persistence.model.emailRequest.EmailRequest;
import demo.msg.javatraining.donationmanager.persistence.model.user.User;
import demo.msg.javatraining.donationmanager.persistence.model.volManager.Volunteer;
import demo.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationParameter;
import demo.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationType;
import demo.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import demo.msg.javatraining.donationmanager.persistence.repository.VolunteerRepository;
import demo.msg.javatraining.donationmanager.service.LogService;
import demo.msg.javatraining.donationmanager.service.NotificationService;
import demo.msg.javatraining.donationmanager.service.emailService.EmailService;
import demo.msg.javatraining.donationmanager.service.permissionService.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static demo.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationParameter.deepCopyList;

@Service
public class VolunteerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    VolunteerRepository volunteerRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    LogService logService;

    /**
     * @return all the volunteers from the database
     */
    public List<Volunteer> getAllUsers() {
        return  volunteerRepository.findAll();

    }

    /**
     * @param id the id of the user that need to be activated or deactivated
     * @throws UserException custom exception which contains the message thrown in the method
     */
    public void toggleUserActive(Long id) throws UserException {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserException("User with user id: " + id + " does not exist", "USER_ID_NOT_PRESENT");
        } else {
            User user = userRepository.findById(id).get();
            user.setActive(!user.isActive());
            if (user.isActive()) {
                resetRetryCount(user.getUsername());
            }
            userRepository.save(user);
        }
    }

    /**
     * A function which generates a username from the first and last name of a user and generates
     * a random password which needs to be changed after the first login
     * Also sends an email with the generated password and username creates a notification to be sent when first logging in
     * @param volunteer user object
     * @throws UserException a custom exception which returns a message depending on the error
     */
    public void createVolunteer(Volunteer volunteer) throws UserException {

        if (volunteer.getMobileNumber() != null) {
            if (!volunteer.getMobileNumber().matches("^(?:\\+?40|0)?7\\d{8}$")) {
                logService.logOperation("ERROR", "Mobile number invalid", volunteer.getUsername());
                throw new UserException("Mobile number is not valid.", "MOBILE_NUMBER_NOT_VALID");

            }
        }
        if (volunteer.getEmail() != null) {
            if (!volunteer.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                logService.logOperation("ERROR", "Email invalid", volunteer.getUsername());
                throw new UserException("Email is not valid.", "EMAIL_NOT_VALID");
            }
        }

        //Username generation
        String tempUsername;
        String lastName = volunteer.getLastName().toLowerCase();
        String firstName = volunteer.getFirstName().toLowerCase();
        if (lastName.length() < 5) {
            tempUsername = lastName + firstName.substring(0, 2);
        } else {
            tempUsername = lastName.substring(0, 5).toLowerCase() + firstName.charAt(0);
        }
        int originalLength = tempUsername.length();

        int i = 1;
        while (userRepository.existsByUsername(tempUsername)) {
            tempUsername = tempUsername.substring(0, originalLength);
            tempUsername = tempUsername.concat(String.format("%d", i++));
        }
        volunteer.setUsername(tempUsername);

        //passwordGeneration
        String generatedPassword = UUID.randomUUID().toString();


        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setDestination(volunteer.getEmail());
        emailRequest.setSubject("User account created");
        emailRequest.setMessage(
                "User account created successfully.\n" +
                        "Login information: \n" +
                        "Username: " + volunteer.getUsername() + "\n" +
                        "Password: " + generatedPassword + "\n" +
                        "This a randomly generated password that will need to be changed on your first login."
        );
        emailService.sendSimpleMessage(emailRequest);
        volunteer.setPassword(passwordEncoder.encode(generatedPassword));
        logService.logOperation("INSERT", "New user created", volunteer.getUsername());
        userRepository.save(volunteer);

        List<NotificationParameter> parameters = new ArrayList<>(Arrays.asList(
                new NotificationParameter(volunteer.getFirstName()),
                new NotificationParameter(volunteer.getLastName()),
                new NotificationParameter(volunteer.getMobileNumber()),
                new NotificationParameter(volunteer.getEmail()),
                new NotificationParameter(volunteer.getUsername())
        ));
        notificationService.saveNotification(volunteer, parameters, NotificationType.WELCOME_NEW_USER);
    }

    public void resetRetryCount(String username) throws UserException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(
                        "User with username: " + username + " does not exist.", "USER_ID_NOT_PRESENT"
                ));
        user.setRetryCount(0);

        userRepository.save(user);
    }

    /**
     * Increments the retry count by one with every call until it reaches 5 tries, after which sets the user to deactivated
     * @param username the username which is used to find the user that needs to be modified
     */
    public void updateRetryCount(String username) {
        try {
            if (userRepository.findByUsername(username).isEmpty()) {
                throw new UserException("User with username: " + username + " does not exist.", "USER_DOES_NOT_EXIST");
            }
            User user = userRepository.findByUsername(username).get();

            user.setRetryCount(user.getRetryCount() + 1);
            if (user.getRetryCount() == 5) {
                user.setActive(false);

                List<NotificationParameter> parameters = new ArrayList<>(Arrays.asList(
                        new NotificationParameter(user.getFirstName()),
                        new NotificationParameter(user.getLastName()),
                        new NotificationParameter(user.getMobileNumber()),
                        new NotificationParameter(user.getEmail()),
                        new NotificationParameter(user.getUsername())
                ));

                List<User> usersToNotify = permissionService.getUsersWithPermission(PermissionEnum.USER_MANAGEMENT);
                notificationService.saveNotification(usersToNotify.get(0), parameters, NotificationType.USER_DEACTIVATED_INCORRECT_PASSWORD);

                for (int i = 1; i < usersToNotify.size(); i++) {
                    User userToNotify = usersToNotify.get(i);
                    List<NotificationParameter> copiedParameters = deepCopyList(parameters);
                    notificationService.saveNotification(userToNotify, copiedParameters, NotificationType.USER_DEACTIVATED_INCORRECT_PASSWORD);
                }


            }
            userRepository.save(user);
        } catch (UserException e) {
            new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            return;
        }

        new ResponseEntity<>("Retry count updated", HttpStatus.OK);
    }


    /**
     * @param volunteerFromToken the user which made the change
     * @param id the id of the user that is modified
     * @param newVolunteer the updated user which fields will be used to change the existing user
     * @throws UserException a custom exception which returns a message depending on the error
     */
    public void updateUser(Volunteer volunteerFromToken, Long id, Volunteer newVolunteer) throws UserException {
        boolean onlyActiveNotNull = true;
        if (userRepository.findById(id).isEmpty()) {
            throw new UserException("User with id: " + id + " does not exist.", "USER_DOES_NOT_EXIST");
        }
        User user = userRepository.findById(id).get();
        Volunteer ToBEModifiedVolunteer  = volunteerRepository.findById(id).get();
        User oldUser = userRepository.findById(id).get();

        userValidations(newVolunteer);

        if (newVolunteer.getFirstName() != null) {
            ToBEModifiedVolunteer.setFirstName(newVolunteer.getFirstName());
            onlyActiveNotNull = false;
        }
        if (newVolunteer.getLastName() != null) {
            ToBEModifiedVolunteer.setLastName(newVolunteer.getLastName());
            onlyActiveNotNull = false;
        }
        if (newVolunteer.getMobileNumber() != null) {
            if (!ToBEModifiedVolunteer.getMobileNumber().matches("^(?:\\+?40|0)?7\\d{8}$")) {
                throw new UserException("Mobile number is not valid.", "MOBILE_NUMBER_NOT_VALID");
            }
            ToBEModifiedVolunteer.setMobileNumber(newVolunteer.getMobileNumber());
            onlyActiveNotNull = false;
        }
        if (newVolunteer.getEmail() != null) {
            if (!ToBEModifiedVolunteer.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                throw new UserException("Email is not valid.", "EMAIL_NOT_VALID");
            }
            ToBEModifiedVolunteer.setEmail(newVolunteer.getEmail());
            onlyActiveNotNull = false;
        }
        if (newVolunteer.getPassword() != null) {
            ToBEModifiedVolunteer.setPassword(passwordEncoder.encode(newVolunteer.getPassword()));
            onlyActiveNotNull = false;
        }
        if (!newVolunteer.getRoles().isEmpty()) {
            ToBEModifiedVolunteer.setRoles(newVolunteer.getRoles());
            onlyActiveNotNull = false;
        }
        if (!newVolunteer.getCampaigns().isEmpty()) {
            ToBEModifiedVolunteer.setCampaigns(newVolunteer.getCampaigns());
            onlyActiveNotNull = false;
        }
        if(!newVolunteer.getAdress().isEmpty()){
            ToBEModifiedVolunteer.setAdress(newVolunteer.getAdress());

        }

        if (!newVolunteer.isFirstLogin())
            ToBEModifiedVolunteer.setFirstLogin(false);

        if (newVolunteer.isActive() != ToBEModifiedVolunteer.isActive()) {
            ToBEModifiedVolunteer.setActive(newVolunteer.isActive());

            if (ToBEModifiedVolunteer.isActive()) {
                ToBEModifiedVolunteer.setRetryCount(0);
            } else {
                List<NotificationParameter> parameters = new ArrayList<>(Arrays.asList(
                        new NotificationParameter(ToBEModifiedVolunteer.getFirstName()),
                        new NotificationParameter(ToBEModifiedVolunteer.getLastName()),
                        new NotificationParameter(ToBEModifiedVolunteer.getMobileNumber()),
                        new NotificationParameter(ToBEModifiedVolunteer.getEmail()),
                        new NotificationParameter(ToBEModifiedVolunteer.getUsername())
                ));

                List<User> usersToNotify = permissionService.getUsersWithPermission(PermissionEnum.USER_MANAGEMENT);
                notificationService.saveNotification(usersToNotify.get(0), parameters, NotificationType.USER_DEACTIVATED_MANUAL);

                for (int i = 1; i < usersToNotify.size(); i++) {
                    User userToNotify = usersToNotify.get(i);
                    List<NotificationParameter> copiedParameters = deepCopyList(parameters);
                    notificationService.saveNotification(userToNotify, copiedParameters, NotificationType.USER_DEACTIVATED_MANUAL);
                }

            }
        }

        volunteerRepository.save(ToBEModifiedVolunteer);

        if (!Objects.equals(volunteerFromToken.getId(), user.getId()) || onlyActiveNotNull){
            List<NotificationParameter> parameters = new ArrayList<>(Arrays.asList(
                    new NotificationParameter(oldUser.getFirstName()),
                    new NotificationParameter(oldUser.getLastName()),
                    new NotificationParameter(oldUser.getMobileNumber()),
                    new NotificationParameter(oldUser.getEmail()),
                    new NotificationParameter(oldUser.getUsername()),
                    new NotificationParameter(user.getFirstName()),
                    new NotificationParameter(user.getLastName()),
                    new NotificationParameter(user.getMobileNumber()),
                    new NotificationParameter(user.getEmail()),
                    new NotificationParameter(user.getUsername())
            ));
            List<NotificationParameter> copiedParameters = deepCopyList(parameters);

//            notificationService.saveNotification(user, parameters, NotificationType.USER_UPDATED);
            notificationService.saveNotification(volunteerFromToken, copiedParameters, NotificationType.USER_UPDATED);
        }}

    private void userValidations(User user) throws UserException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserException("Email already in use.", "EMAIL_IN_USE");
        }

        if (userRepository.existsByMobileNumber(user.getMobileNumber())) {
            throw new UserException("Mobile number already in use.", "MOBILE_NUMBER_IN_USE");
        }
    }

    public UserDTO getUserById(Long id) throws UserException {
        UserDTO userDTO = new UserDTO();
        if (userRepository.findById(id).isEmpty()) {
            throw new UserException("User with id: " + id + " does not exist.", "USER_DOES_NOT_EXIST");
        }

        User userFromDB = userRepository.findById(id).get();


        userDTO.setId(userFromDB.getId());
        userDTO.setFirstName(userFromDB.getFirstName());
        userDTO.setLastName(userFromDB.getLastName());
        userDTO.setMobileNumber(userFromDB.getMobileNumber());
        userDTO.setUsername(userFromDB.getUsername());
        userDTO.setEmail(userFromDB.getEmail());
        userDTO.setPassword(userFromDB.getPassword());
        userDTO.setRoles(userFromDB.getRoles());
        userDTO.setCampaigns(userFromDB.getCampaigns());
        userDTO.setActive(userFromDB.isActive());
        userDTO.setFirstLogin(userFromDB.isFirstLogin());
        userDTO.setRetryCount(userFromDB.getRetryCount());

        return userDTO;
    }

    /**
     * @param username the username of the user which is searched for
     * @return a boolean confirming or denying the presence of the searched user
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * @param username the username of the user which is searched for
     * @return a user
     */
    public Volunteer findByUsername(String username) {
        return volunteerRepository.findByUsername(username).orElse(null);
    }

}