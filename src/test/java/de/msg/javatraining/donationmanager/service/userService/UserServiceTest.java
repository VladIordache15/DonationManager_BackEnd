package de.msg.javatraining.donationmanager.service.userService;

import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LogService logService;

    @Test
    void getAllUsers() {
    }

    @Test
    void toggleUserActive() {
    }

    @Test
    void createUser() {
    }

    @Test
    void resetRetryCount() {
    }

    @Test
    void updateRetryCount() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void existsByUsername() {
    }

    @Test
    void findByUsername() {
    }
}