package com.example.demo.ControllerTest;


import com.example.demo.Controller.UserController;
import com.example.demo.Service.UserService;
import com.example.demo.model.UserRegistration;
import com.example.demo.model.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        session = new MockHttpSession();
    }

    @Test
    void registerUser() {
        UserRegistration user = new UserRegistration();
        user.setEmail("test@example.com");

        when(userService.create(any(UserRegistration.class))).thenReturn(user);

        UserRegistration result = userController.refuser(user);

        assertEquals("test@example.com", result.getEmail());
        verify(userService, times(1)).create(user);
    }

    @Test
    void getAllUsers() {
        UserRegistration user1 = new UserRegistration();
        UserRegistration user2 = new UserRegistration();
        List<UserRegistration> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        List<UserRegistration> result = userController.getUsers();

        assertEquals(2, result.size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void loginSuccess() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "test@example.com");
        credentials.put("password", "password");

        UserRegistration user = new UserRegistration();
        user.setEmail("test@example.com");
        user.setUserType("customer");
        user.setFirstName("John");

        when(userService.findByEmailAndPassword(anyString(), anyString())).thenReturn(user);

        ResponseEntity<?> response = userController.login(credentials, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(session.getAttribute("loggedInUserId"));
    }

    @Test
    void loginFailure() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "wrong@example.com");
        credentials.put("password", "wrong");

        when(userService.findByEmailAndPassword(anyString(), anyString())).thenReturn(null);

        ResponseEntity<?> response = userController.login(credentials, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void getProfileAuthenticated() {
        Long userId = 1L;
        session.setAttribute("loggedInUserId", userId);

        UserRegistration user = new UserRegistration();
        user.setId(userId);

        when(userService.findById(userId)).thenReturn(java.util.Optional.of(user));

        ResponseEntity<?> response = userController.getProfile(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void getProfileUnauthenticated() {
        ResponseEntity<?> response = userController.getProfile(session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not logged in", response.getBody());
    }

    @Test
    void uploadProfilePicSuccess() throws Exception {
        Long userId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        doNothing().when(userService).uploadProfilePicture(anyLong(), any());

        ResponseEntity<String> response = userController.uploadProfilePic(userId, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Profile picture uploaded successfully.", response.getBody());
    }

    @Test
    void saveWorkerDetails() {
        Long userId = 1L;
        session.setAttribute("loggedInUserId", userId);
        Worker worker = new Worker();

        when(userService.saveWorkerDetails(userId, worker)).thenReturn("Worker details saved");

        ResponseEntity<?> response = userController.saveWorker(worker, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Worker details saved", response.getBody());
    }

    @Test
    void getWorkerDetails() {
        Long userId = 1L;
        session.setAttribute("loggedInUserId", userId);
        Worker worker = new Worker();

        when(userService.getWorkerDetails(userId)).thenReturn(worker);

        ResponseEntity<?> response = userController.getWorker(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(worker, response.getBody());
    }

    @Test
    void testGetProfilePic() throws Exception {
        byte[] imageBytes = "test image".getBytes();
        String email = "test@example.com";

        when(userService.getProfilePictureByEmail(email)).thenReturn(imageBytes);

        mockMvc.perform(get("/api/profilePic")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageBytes));
    }

}
