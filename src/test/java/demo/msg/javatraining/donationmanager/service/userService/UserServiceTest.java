package demo.msg.javatraining.donationmanager.service.userService;

import demo.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import demo.msg.javatraining.donationmanager.service.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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