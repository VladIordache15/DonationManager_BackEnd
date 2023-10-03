package de.msg.javatraining.donationmanager.service.userService;

import de.msg.javatraining.donationmanager.controller.dto.UserDTO;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.emailRequest.EmailRequest;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationParameter;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationType;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.LogService;
import de.msg.javatraining.donationmanager.service.NotificationService;
import de.msg.javatraining.donationmanager.service.emailService.EmailService;
import de.msg.javatraining.donationmanager.service.permissionService.PermissionService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

import static de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationParameter.deepCopyList;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    LogService logService;

    /**
     * @return all the users from the database
     */
    public List<UserDTO> getAllUsers() {
        List<User> usersFromDB = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();

        usersFromDB.forEach(
                u -> {
                    UserDTO user = new UserDTO();
                    user.setId(u.getId());
                    user.setFirstName(u.getFirstName());
                    user.setLastName(u.getLastName());
                    user.setMobileNumber(u.getMobileNumber());
                    user.setUsername(u.getUsername());
                    user.setEmail(u.getEmail());
                    user.setPassword(u.getPassword());
                    user.setRoles(u.getRoles());
                    user.setCampaigns(u.getCampaigns());
                    user.setActive(u.isActive());
                    user.setFirstLogin(u.isFirstLogin());
                    user.setRetryCount(u.getRetryCount());

                    userDTOs.add(user);
                }
        );

        return userDTOs;
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
     * @param user user object
     * @throws UserException a custom exception which returns a message depending on the error
     */
    public void createUser(User user) throws UserException {

        if (user.getMobileNumber() != null) {
            if (!user.getMobileNumber().matches("^(?:\\+?40|0)?7\\d{8}$")) {
                logService.logOperation("ERROR", "Mobile number invalid", user.getUsername());
                throw new UserException("Mobile number is not valid.", "MOBILE_NUMBER_NOT_VALID");

            }
        }
        if (user.getEmail() != null) {
            if (!user.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                logService.logOperation("ERROR", "Email invalid", user.getUsername());
                throw new UserException("Email is not valid.", "EMAIL_NOT_VALID");
            }
        }

        //Username generation
        String tempUsername;
        String lastName = user.getLastName().toLowerCase();
        String firstName = user.getFirstName().toLowerCase();
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
        user.setUsername(tempUsername);

        //passwordGeneration
        String generatedPassword = UUID.randomUUID().toString();


        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setDestination(user.getEmail());
        emailRequest.setSubject("User account created");
        emailRequest.setMessage(
                "User account created successfully.\n" +
                        "Login information: \n" +
                        "Username: " + user.getUsername() + "\n" +
                        "Password: " + generatedPassword + "\n" +
                        "This a randomly generated password that will need to be changed on your first login."
        );
        emailService.sendSimpleMessage(emailRequest);
        user.setPassword(passwordEncoder.encode(generatedPassword));
        logService.logOperation("INSERT", "New user created", user.getUsername());
        userRepository.save(user);

        List<NotificationParameter> parameters = new ArrayList<>(Arrays.asList(
                new NotificationParameter(user.getFirstName()),
                new NotificationParameter(user.getLastName()),
                new NotificationParameter(user.getMobileNumber()),
                new NotificationParameter(user.getEmail()),
                new NotificationParameter(user.getUsername())
        ));
        notificationService.saveNotification(user, parameters, NotificationType.WELCOME_NEW_USER);
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
     * @param userFromToken the user which made the change
     * @param id the id of the user that is modified
     * @param newUser the updated user which fields will be used to change the existing user
     * @throws UserException a custom exception which returns a message depending on the error
     */
    public void updateUser(User userFromToken, Long id, User newUser) throws UserException {
        boolean onlyActiveNotNull = true;
        if (userRepository.findById(id).isEmpty()) {
            throw new UserException("User with id: " + id + " does not exist.", "USER_DOES_NOT_EXIST");
        }
        User user = userRepository.findById(id).get();
        User oldUser = userRepository.findById(id).get();

        userValidations(newUser);

        if (newUser.getFirstName() != null) {
            user.setFirstName(newUser.getFirstName());
            onlyActiveNotNull = false;
        }
        if (newUser.getLastName() != null) {
            user.setLastName(newUser.getLastName());
            onlyActiveNotNull = false;
        }
        if (newUser.getMobileNumber() != null) {
            if (!user.getMobileNumber().matches("^(?:\\+?40|0)?7\\d{8}$")) {
                throw new UserException("Mobile number is not valid.", "MOBILE_NUMBER_NOT_VALID");
            }
            user.setMobileNumber(newUser.getMobileNumber());
            onlyActiveNotNull = false;
        }
        if (newUser.getEmail() != null) {
            if (!user.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                throw new UserException("Email is not valid.", "EMAIL_NOT_VALID");
            }
            user.setEmail(newUser.getEmail());
            onlyActiveNotNull = false;
        }
        if (newUser.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            onlyActiveNotNull = false;
        }
        if (!newUser.getRoles().isEmpty()) {
            user.setRoles(newUser.getRoles());
            onlyActiveNotNull = false;
        }
        if (!newUser.getCampaigns().isEmpty()) {
            user.setCampaigns(newUser.getCampaigns());
            onlyActiveNotNull = false;
        }
        if (!newUser.isFirstLogin())
            user.setFirstLogin(false);

        if (newUser.isActive() != user.isActive()) {
            user.setActive(newUser.isActive());
            if (user.isActive()) {
                user.setRetryCount(0);
            } else {
                List<NotificationParameter> parameters = new ArrayList<>(Arrays.asList(
                        new NotificationParameter(user.getFirstName()),
                        new NotificationParameter(user.getLastName()),
                        new NotificationParameter(user.getMobileNumber()),
                        new NotificationParameter(user.getEmail()),
                        new NotificationParameter(user.getUsername())
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

        userRepository.save(user);

        if (!Objects.equals(userFromToken.getId(), user.getId()) || onlyActiveNotNull){
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

        notificationService.saveNotification(user, parameters, NotificationType.USER_UPDATED);
        notificationService.saveNotification(userFromToken, copiedParameters, NotificationType.USER_UPDATED);
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
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

}
