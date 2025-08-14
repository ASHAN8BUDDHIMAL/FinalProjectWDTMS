package com.example.demo.ControllerTest;

import com.example.demo.Controller.TaskStatusController;
import com.example.demo.DTO.ShowStatusDTO;
import com.example.demo.DTO.TaskStatusRequest;
import com.example.demo.Service.TaskStatusService;
import com.example.demo.model.TaskStatus;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskStatusControllerTest {

    @Mock
    private TaskStatusService taskStatusService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private TaskStatusController taskStatusController;

    private final Long testUserId = 1L;
    private final Long testWorkerId = 2L;
    private final Long testTaskId = 10L;
    private TaskStatus testStatus;
    private TaskStatusRequest testRequest;
    private ShowStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = new TaskStatus();
        testStatus.setTaskId(testTaskId);
        testStatus.setWorkerId(testWorkerId);
        testStatus.setStatus("PENDING");

        testRequest = new TaskStatusRequest();
        testRequest.setTaskId(testTaskId);
        testRequest.setStatus("ACCEPTED");

        testStatusDTO = new ShowStatusDTO();
        testStatusDTO.setTaskId(testTaskId);
        testStatusDTO.setStatus("PENDING");
    }

    @Test
    void update_Unauthorized() {
        when(session.getAttribute("loggedInUserId")).thenReturn(null);

        ResponseEntity<?> response = taskStatusController.update(testRequest, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not logged in", response.getBody());
        verify(taskStatusService, never()).updateStatus(any());
    }

    @Test
    void update_Success() {
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        when(taskStatusService.updateStatus(any())).thenReturn(testStatus);

        ResponseEntity<?> response = taskStatusController.update(testRequest, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testStatus, response.getBody());
        verify(taskStatusService).updateStatus(any());
    }

    @Test
    void getTasksForLoggedInWorker_Unauthorized() {
        when(session.getAttribute("loggedInUserId")).thenReturn(null);

        ResponseEntity<List<ShowStatusDTO>> response = taskStatusController.getTasksForLoggedInWorker(session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(taskStatusService, never()).getTasksForWorker(anyLong());
    }

    @Test
    void getTasksForLoggedInWorker_Success() {
        when(session.getAttribute("loggedInUserId")).thenReturn(testWorkerId);
        when(taskStatusService.getTasksForWorker(testWorkerId)).thenReturn(List.of(testStatusDTO));

        ResponseEntity<List<ShowStatusDTO>> response = taskStatusController.getTasksForLoggedInWorker(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testTaskId, response.getBody().get(0).getTaskId());
    }

    @Test
    void workerUpdate_Unauthorized() {
        when(session.getAttribute("loggedInUserId")).thenReturn(null);

        ResponseEntity<?> response = taskStatusController.workerUpdate(testRequest, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Worker not logged in", response.getBody());
        verify(taskStatusService, never()).updateTaskStatus(anyLong(), anyLong(), anyString());
    }

    @Test
    void workerUpdate_Success() {
        when(session.getAttribute("loggedInUserId")).thenReturn(testWorkerId);
        when(taskStatusService.updateTaskStatus(testTaskId, testWorkerId, "ACCEPTED")).thenReturn(testStatus);

        ResponseEntity<?> response = taskStatusController.workerUpdate(testRequest, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testStatus, response.getBody());
    }

    @Test
    void getAllClientTasks_Unauthorized() {
        when(session.getAttribute("loggedInUserId")).thenReturn(null);

        ResponseEntity<List<ShowStatusDTO>> response = taskStatusController.getAllClientTasks(session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(taskStatusService, never()).getAllTasksForClient(anyLong());
    }

    @Test
    void getAllClientTasks_Success() {
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        when(taskStatusService.getAllTasksForClient(testUserId)).thenReturn(List.of(testStatusDTO));

        ResponseEntity<List<ShowStatusDTO>> response = taskStatusController.getAllClientTasks(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testTaskId, response.getBody().get(0).getTaskId());
    }

    @Test
    void changeTaskStatus_Unauthorized() {
        when(session.getAttribute("loggedInUserId")).thenReturn(null);

        ResponseEntity<?> response = taskStatusController.changeTaskStatus(
                testTaskId, testWorkerId, Map.of("status", "CONFIRMED"), session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not logged in", response.getBody());
    }

    @Test
    void changeTaskStatus_MissingStatus() {
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);

        ResponseEntity<?> response = taskStatusController.changeTaskStatus(
                testTaskId, testWorkerId, Map.of(), session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Status is required", response.getBody());
    }

    @Test
    void changeTaskStatus_ConfirmTask() {
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        testStatus.setStatus("CONFIRMED");
        when(taskStatusService.confirmTask(testTaskId, testWorkerId)).thenReturn(testStatus);

        ResponseEntity<?> response = taskStatusController.changeTaskStatus(
                testTaskId, testWorkerId, Map.of("status", "CONFIRMED"), session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("success", responseBody.get("status"));
    }

    @Test
    void changeTaskStatus_CompleteTask() {
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        testStatus.setStatus("COMPLETED");
        when(taskStatusService.completeTask(testTaskId, testWorkerId)).thenReturn(testStatus);

        ResponseEntity<?> response = taskStatusController.changeTaskStatus(
                testTaskId, testWorkerId, Map.of("status", "COMPLETED"), session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changeTaskStatus_IncompleteTask() {
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        testStatus.setStatus("INCOMPLETED");
        when(taskStatusService.incompleteTask(testTaskId, testWorkerId)).thenReturn(testStatus);

        ResponseEntity<?> response = taskStatusController.changeTaskStatus(
                testTaskId, testWorkerId, Map.of("status", "INCOMPLETED"), session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changeTaskStatus_InvalidStatus() {
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);

        ResponseEntity<?> response = taskStatusController.changeTaskStatus(
                testTaskId, testWorkerId, Map.of("status", "INVALID"), session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid status value", response.getBody());
    }

    @Test
    void changeTaskStatus_ServiceException() {
        when(session.getAttribute("loggedInUserId")).thenReturn(testUserId);
        when(taskStatusService.confirmTask(testTaskId, testWorkerId))
                .thenThrow(new RuntimeException("Task not found"));

        ResponseEntity<?> response = taskStatusController.changeTaskStatus(
                testTaskId, testWorkerId, Map.of("status", "CONFIRMED"), session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("error", responseBody.get("status"));
    }
}
