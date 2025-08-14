package com.example.demo.ControllerTest;

import com.example.demo.Controller.CreateTaskController;
import com.example.demo.Service.CreateTaskService;
import com.example.demo.model.CreateTask;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CreateTaskControllerTest {

    @Mock
    private CreateTaskService createTaskService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private CreateTaskController createTaskController;

    private CreateTask testTask;
    private final Long testUserId = 1L;
    private final Long testTaskId = 10L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testTask = new CreateTask();
        testTask.setId(testTaskId);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
    }

    @Test
    void createTask_Success() {
        // Arrange
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        when(createTaskService.createTask(anyLong(), any(CreateTask.class))).thenReturn(testTask);

        // Act
        ResponseEntity<CreateTask> response = createTaskController.createTask(testTask, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTask, response.getBody());
        verify(session).setAttribute("createdTaskId", testTaskId);
        verify(createTaskService).createTask(testUserId, testTask);
    }

    @Test
    void createTask_Unauthorized() {
        // Arrange
        when(session.getAttribute("loggedInUserId")).thenReturn(null);

        // Act
        ResponseEntity<CreateTask> response = createTaskController.createTask(testTask, session);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(createTaskService, never()).createTask(anyLong(), any());
    }

    @Test
    void updateTask_Success() {
        // Arrange
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        when(createTaskService.updateTask(testUserId, testTaskId, testTask))
                .thenReturn(Optional.of(testTask));

        // Act
        ResponseEntity<CreateTask> response = createTaskController.updateTask(testTaskId, testTask, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTask, response.getBody());
    }

    @Test
    void updateTask_NotFound() {
        // Arrange
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        when(createTaskService.updateTask(testUserId, testTaskId, testTask))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<CreateTask> response = createTaskController.updateTask(testTaskId, testTask, session);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateTask_Unauthorized() {
        // Arrange
        when(session.getAttribute("loggedInUserId")).thenReturn(null);

        // Act
        ResponseEntity<CreateTask> response = createTaskController.updateTask(testTaskId, testTask, session);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(createTaskService, never()).updateTask(anyLong(), anyLong(), any());
    }

    @Test
    void listTasks_Success() {
        // Arrange
        List<CreateTask> tasks = Arrays.asList(testTask, new CreateTask());
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        when(createTaskService.getTasks(testUserId)).thenReturn(tasks);

        // Act
        ResponseEntity<List<CreateTask>> response = createTaskController.listTasks(session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(testTask, response.getBody().get(0));
    }

    @Test
    void listTasks_Unauthorized() {
        // Arrange
        when(session.getAttribute("loggedInUserId")).thenReturn(null);

        // Act
        ResponseEntity<List<CreateTask>> response = createTaskController.listTasks(session);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(createTaskService, never()).getTasks(anyLong());
    }

    @Test
    void deleteTask_Success() {
        // Arrange
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        when(createTaskService.deleteTask(testUserId, testTaskId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = createTaskController.deleteTask(testTaskId, session);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteTask_NotFound() {
        // Arrange
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        when(createTaskService.deleteTask(testUserId, testTaskId)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = createTaskController.deleteTask(testTaskId, session);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteTask_Unauthorized() {
        // Arrange
        when(session.getAttribute("loggedInUserId")).thenReturn(null);

        // Act
        ResponseEntity<Void> response = createTaskController.deleteTask(testTaskId, session);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(createTaskService, never()).deleteTask(anyLong(), anyLong());
    }
}
