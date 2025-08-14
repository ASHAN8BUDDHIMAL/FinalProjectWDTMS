package com.example.demo.ServiceTest;

import com.example.demo.Service.CreateTaskService;
import com.example.demo.model.CreateTask;
import com.example.demo.repository.CreateTaskRepo;
import com.example.demo.repository.RegUser;
import com.example.demo.repository.TaskStatusRepo;
import com.example.demo.repository.WorkerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class CreateTaskServiceTest {

    @Mock
    private CreateTaskRepo createTaskRepo;

    @Mock
    private TaskStatusRepo taskStatusRepo;

    @Mock
    private WorkerRepo workerRepo;

    @Mock
    private RegUser regUser;

    @InjectMocks
    private CreateTaskService createTaskService;

    private CreateTask testTask;
    private final Long testUserId = 1L;
    private final Long testTaskId = 10L;

    @BeforeEach
    void setUp() {
        testTask = new CreateTask();
        testTask.setId(testTaskId);
        testTask.setUserId(testUserId);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus("COMPILED");
    }

    @Test
    void createTask_Success() {
        // Arrange
        when(createTaskRepo.save(any(CreateTask.class))).thenReturn(testTask);

        // Act
        CreateTask result = createTaskService.createTask(testUserId, testTask);

        // Assert
        assertNotNull(result);
        assertEquals(testTaskId, result.getId());
        assertEquals(testUserId, result.getUserId());
        assertEquals("COMPILED", result.getStatus());
        verify(createTaskRepo, times(1)).save(any(CreateTask.class));
    }

    @Test
    void updateTask_Success() {
        // Arrange
        CreateTask updates = new CreateTask();
        updates.setTitle("Updated Title");
        updates.setDescription("Updated Description");
        updates.setStatus("IN_PROGRESS");

        when(createTaskRepo.findById(testTaskId)).thenReturn(Optional.of(testTask));
        when(createTaskRepo.save(any(CreateTask.class))).thenReturn(testTask);

        // Act
        Optional<CreateTask> result = createTaskService.updateTask(testUserId, testTaskId, updates);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Updated Title", result.get().getTitle());
        assertEquals("Updated Description", result.get().getDescription());
        assertEquals("IN_PROGRESS", result.get().getStatus());
        verify(createTaskRepo, times(1)).findById(testTaskId);
        verify(createTaskRepo, times(1)).save(testTask);
    }

    @Test
    void updateTask_NotFound() {
        // Arrange
        when(createTaskRepo.findById(testTaskId)).thenReturn(Optional.empty());

        // Act
        Optional<CreateTask> result = createTaskService.updateTask(testUserId, testTaskId, new CreateTask());

        // Assert
        assertFalse(result.isPresent());
        verify(createTaskRepo, times(1)).findById(testTaskId);
        verify(createTaskRepo, never()).save(any());
    }

    @Test
    void updateTask_WrongUser() {
        // Arrange
        Long differentUserId = 2L;
        when(createTaskRepo.findById(testTaskId)).thenReturn(Optional.of(testTask));

        // Act
        Optional<CreateTask> result = createTaskService.updateTask(differentUserId, testTaskId, new CreateTask());

        // Assert
        assertFalse(result.isPresent());
        verify(createTaskRepo, times(1)).findById(testTaskId);
        verify(createTaskRepo, never()).save(any());
    }

    @Test
    void deleteTask_Success() {
        // Arrange
        when(createTaskRepo.findById(testTaskId)).thenReturn(Optional.of(testTask));
        doNothing().when(createTaskRepo).deleteById(testTaskId);

        // Act
        boolean result = createTaskService.deleteTask(testUserId, testTaskId);

        // Assert
        assertTrue(result);
        verify(createTaskRepo, times(1)).findById(testTaskId);
        verify(createTaskRepo, times(1)).deleteById(testTaskId);
    }

    @Test
    void deleteTask_NotFound() {
        // Arrange
        when(createTaskRepo.findById(testTaskId)).thenReturn(Optional.empty());

        // Act
        boolean result = createTaskService.deleteTask(testUserId, testTaskId);

        // Assert
        assertFalse(result);
        verify(createTaskRepo, times(1)).findById(testTaskId);
        verify(createTaskRepo, never()).deleteById(any());
    }

    @Test
    void deleteTask_WrongUser() {
        // Arrange
        Long differentUserId = 2L;
        when(createTaskRepo.findById(testTaskId)).thenReturn(Optional.of(testTask));

        // Act
        boolean result = createTaskService.deleteTask(differentUserId, testTaskId);

        // Assert
        assertFalse(result);
        verify(createTaskRepo, times(1)).findById(testTaskId);
        verify(createTaskRepo, never()).deleteById(any());
    }

    @Test
    void getTasks_WithCompletedTasks() {
        // Arrange
        CreateTask task1 = new CreateTask();
        task1.setId(1L);
        CreateTask task2 = new CreateTask();
        task2.setId(2L);
        List<CreateTask> allTasks = Arrays.asList(task1, task2);

        List<Long> completedTaskIds = Arrays.asList(2L);

        when(createTaskRepo.findByUserId(testUserId)).thenReturn(allTasks);
        when(taskStatusRepo.findTaskIdsWithCompletedStatus()).thenReturn(completedTaskIds);

        // Act
        List<CreateTask> result = createTaskService.getTasks(testUserId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(createTaskRepo, times(1)).findByUserId(testUserId);
        verify(taskStatusRepo, times(1)).findTaskIdsWithCompletedStatus();
    }

    @Test
    void getTasks_NoCompletedTasks() {
        // Arrange
        CreateTask task1 = new CreateTask();
        task1.setId(1L);
        CreateTask task2 = new CreateTask();
        task2.setId(2L);
        List<CreateTask> allTasks = Arrays.asList(task1, task2);

        when(createTaskRepo.findByUserId(testUserId)).thenReturn(allTasks);
        when(taskStatusRepo.findTaskIdsWithCompletedStatus()).thenReturn(new ArrayList<>());

        // Act
        List<CreateTask> result = createTaskService.getTasks(testUserId);

        // Assert
        assertEquals(2, result.size());
        verify(createTaskRepo, times(1)).findByUserId(testUserId);
        verify(taskStatusRepo, times(1)).findTaskIdsWithCompletedStatus();
    }

    @Test
    void getTasks_EmptyList() {
        // Arrange
        when(createTaskRepo.findByUserId(testUserId)).thenReturn(new ArrayList<>());
        when(taskStatusRepo.findTaskIdsWithCompletedStatus()).thenReturn(new ArrayList<>());

        // Act
        List<CreateTask> result = createTaskService.getTasks(testUserId);

        // Assert
        assertTrue(result.isEmpty());
        verify(createTaskRepo, times(1)).findByUserId(testUserId);
        verify(taskStatusRepo, times(1)).findTaskIdsWithCompletedStatus();
    }
}
