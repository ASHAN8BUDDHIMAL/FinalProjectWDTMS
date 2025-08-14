package com.example.demo.ServiceTest;

import com.example.demo.Service.UserService;
import com.example.demo.model.UserRegistration;
import com.example.demo.model.Worker;
import com.example.demo.repository.ClientRepo;
import com.example.demo.repository.RegUser;
import com.example.demo.repository.WorkerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private RegUser regUser;

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private WorkerRepo workerRepo;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        try {
            UserRegistration user = new UserRegistration();
            user.setEmail("test@example.com");

            when(regUser.save(any(UserRegistration.class))).thenReturn(user);

            UserRegistration result = userService.create(user);

            assertEquals("test@example.com", result.getEmail());
            verify(regUser, times(1)).save(user);
        } catch (Exception e) {
            System.err.println("Error in testCreateUser: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testGetAllUsers() {
        try {
            UserRegistration user1 = new UserRegistration();
            UserRegistration user2 = new UserRegistration();
            List<UserRegistration> users = Arrays.asList(user1, user2);

            when(regUser.findAll()).thenReturn(users);

            List<UserRegistration> result = userService.getAllUsers();

            assertEquals(2, result.size());
            verify(regUser, times(1)).findAll();
        } catch (Exception e) {
            System.err.println("Error in testGetAllUsers: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testFindByEmailAndPassword() {
        try {
            UserRegistration user = new UserRegistration();
            user.setEmail("test@example.com");
            user.setPassword("password");

            when(regUser.findByEmailAndPassword("test@example.com", "password"))
                    .thenReturn(Optional.of(user));

            UserRegistration result = userService.findByEmailAndPassword("test@example.com", "password");

            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());

            // Test not found case
            when(regUser.findByEmailAndPassword("wrong@example.com", "wrong"))
                    .thenReturn(Optional.empty());

            assertNull(userService.findByEmailAndPassword("wrong@example.com", "wrong"));
        } catch (Exception e) {
            System.err.println("Error in testFindByEmailAndPassword: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testFindById() {
        try {
            UserRegistration user = new UserRegistration();
            user.setId(1L);

            when(regUser.findById(1L)).thenReturn(Optional.of(user));

            Optional<UserRegistration> result = userService.findById(1L);

            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getId());

            // Test not found case
            when(regUser.findById(999L)).thenReturn(Optional.empty());
            assertTrue(userService.findById(999L).isEmpty());
        } catch (Exception e) {
            System.err.println("Error in testFindById: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testUploadProfilePicture() throws IOException {
        try {
            UserRegistration user = new UserRegistration();
            user.setId(1L);

            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", "test image".getBytes());

            when(regUser.findById(1L)).thenReturn(Optional.of(user));
            when(regUser.save(any(UserRegistration.class))).thenReturn(user);

            userService.uploadProfilePicture(1L, file);

            assertNotNull(user.getProfilePicture());
            verify(regUser, times(1)).save(user);
        } catch (Exception e) {
            System.err.println("Error in testUploadProfilePicture: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testGetProfilePictureByEmail() {
        try {
            UserRegistration user = new UserRegistration();
            user.setEmail("test@example.com");
            user.setProfilePicture("test image".getBytes());

            when(regUser.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            byte[] result = userService.getProfilePictureByEmail("test@example.com");

            assertNotNull(result);
            assertEquals("test image", new String(result));
        } catch (Exception e) {
            System.err.println("Error in testGetProfilePictureByEmail: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testSaveWorkerDetails() {
        try {
            Worker worker = new Worker();
            worker.setSkills("Plumbing");
            worker.setChargePerHour(25.0);

            when(workerRepo.save(any(Worker.class))).thenReturn(worker);

            String result = userService.saveWorkerDetails(1L, worker);

            assertEquals("Worker details saved successfully.", result);
            assertEquals(1L, worker.getUserId());
            verify(workerRepo, times(1)).save(worker);
        } catch (Exception e) {
            System.err.println("Error in testSaveWorkerDetails: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testUpdateWorkerDetails() {
        try {
            Worker existingWorker = new Worker();
            existingWorker.setUserId(1L);
            existingWorker.setSkills("Cleaning");
            existingWorker.setChargePerHour(20.0);

            Worker updatedWorker = new Worker();
            updatedWorker.setSkills("Deep Cleaning");
            updatedWorker.setChargePerHour(30.0);

            when(workerRepo.findByUserId(1L)).thenReturn(Optional.of(existingWorker));
            when(workerRepo.save(any(Worker.class))).thenReturn(existingWorker);

            String result = userService.updateWorkerDetails(1L, updatedWorker);

            assertEquals("Worker details updated successfully.", result);
            assertEquals("Deep Cleaning", existingWorker.getSkills());
            assertEquals(30.0, existingWorker.getChargePerHour());
        } catch (Exception e) {
            System.err.println("Error in testUpdateWorkerDetails: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testGetWorkerDetails() {
        try {
            Worker worker = new Worker();
            worker.setUserId(1L);

            when(workerRepo.findByUserId(1L)).thenReturn(Optional.of(worker));

            Worker result = userService.getWorkerDetails(1L);

            assertNotNull(result);
            assertEquals(1L, result.getUserId());

            // Test not found case
            when(workerRepo.findByUserId(999L)).thenReturn(Optional.empty());
            assertNull(userService.getWorkerDetails(999L));
        } catch (Exception e) {
            System.err.println("Error in testGetWorkerDetails: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testSearchUsersByName() {
        try {
            UserRegistration user1 = new UserRegistration();
            user1.setFirstName("John");
            UserRegistration user2 = new UserRegistration();
            user2.setFirstName("Johnson");

            List<UserRegistration> users = Arrays.asList(user1, user2);

            when(regUser.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("john", "john"))
                    .thenReturn(users);

            List<UserRegistration> result = userService.searchUsersByName("john");

            assertEquals(2, result.size());
            verify(regUser, times(1))
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("john", "john");
        } catch (Exception e) {
            System.err.println("Error in testSearchUsersByName: " + e.getMessage());
            throw e;
        }
    }
}
