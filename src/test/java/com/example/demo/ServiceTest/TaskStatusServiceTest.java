package com.example.demo.ServiceTest;

import com.example.demo.DTO.ShowStatusDTO;
import com.example.demo.DTO.TaskStatusRequest;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.TaskStatusService;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskStatusServiceTest {

    @Mock
    private TaskStatusRepo taskStatusRepo;

    @Mock
    private CreateTaskRepo createTaskRepo;

    @Mock
    private RegUser regUser;

    @Mock
    private EmailService emailService;

    @Mock
    private TaskLocationRepo taskLocationRepo;

    @Mock
    private BusySlotRepo busySlotRepo;

    @InjectMocks
    private TaskStatusService taskStatusService;

    private TaskStatusRequest taskStatusRequest;
    private CreateTask createTask;
    private UserRegistration worker;
    private UserRegistration client;
    private TaskStatus taskStatus;

    @BeforeEach
    void setUp() {
        // Common test data
        client = new UserRegistration();
        client.setId(1L);
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setEmail("client@example.com");

        worker = new UserRegistration();
        worker.setId(2L);
        worker.setFirstName("Jane");
        worker.setLastName("Smith");
        worker.setEmail("worker@example.com");

        createTask = new CreateTask();
        createTask.setId(1L);
        createTask.setUserId(client.getId());
        createTask.setTitle("Test Task");
        createTask.setDescription("Test Description");
        createTask.setScheduledDate(LocalDateTime.now().plusDays(1));
        createTask.setAllocatedTime(2);


        createTask.setAllocatedAmount(100.0);

        taskStatusRequest = new TaskStatusRequest();
        taskStatusRequest.setTaskId(createTask.getId());
        taskStatusRequest.setWorkerId(worker.getId());
        taskStatusRequest.setUserId(client.getId());
        taskStatusRequest.setStatus("ASSIGNED");

        taskStatus = new TaskStatus();
        taskStatus.setTaskId(createTask.getId());
        taskStatus.setWorkerId(worker.getId());
        taskStatus.setUserId(client.getId());
        taskStatus.setStatus("ASSIGNED");
    }

    @Test
    void updateStatus_NewStatus_CreatesNewRecord() {
        when(taskStatusRepo.findByTaskIdAndWorkerId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(taskStatusRepo.save(any(TaskStatus.class))).thenReturn(taskStatus);
        when(regUser.findById(anyLong())).thenReturn(Optional.of(worker));
        when(createTaskRepo.findById(anyLong())).thenReturn(Optional.of(createTask));

        TaskStatus result = taskStatusService.updateStatus(taskStatusRequest);

        assertNotNull(result);
        assertEquals("ASSIGNED", result.getStatus());
        verify(emailService, times(1)).sendTaskAssignmentEmail(
                anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void updateStatus_ExistingStatus_UpdatesRecord() {
        when(taskStatusRepo.findByTaskIdAndWorkerId(anyLong(), anyLong())).thenReturn(Optional.of(taskStatus));
        when(taskStatusRepo.save(any(TaskStatus.class))).thenReturn(taskStatus);

        taskStatusRequest.setStatus("ACCEPTED");
        TaskStatus result = taskStatusService.updateStatus(taskStatusRequest);

        assertNotNull(result);
        assertEquals("ACCEPTED", result.getStatus());
    }

    @Test
    void getTasksForWorker_ReturnsTasksWithClientInfo() {
        List<TaskStatus> statuses = Collections.singletonList(taskStatus);
        when(taskStatusRepo.findByWorkerId(anyLong())).thenReturn(statuses);
        when(createTaskRepo.findById(anyLong())).thenReturn(Optional.of(createTask));
        when(regUser.findById(anyLong())).thenReturn(Optional.of(client));

        List<ShowStatusDTO> results = taskStatusService.getTasksForWorker(worker.getId());

        assertFalse(results.isEmpty());
        ShowStatusDTO dto = results.get(0);
        assertEquals(createTask.getTitle(), dto.getTitle());
        assertEquals(client.getFirstName(), dto.getFirstName());
        assertEquals(client.getLastName(), dto.getLastName());
    }

    @Test
    void updateTaskStatus_ValidTask_UpdatesStatus() {
        when(taskStatusRepo.findByTaskIdAndWorkerId(anyLong(), anyLong())).thenReturn(Optional.of(taskStatus));
        when(taskStatusRepo.save(any(TaskStatus.class))).thenReturn(taskStatus);

        TaskStatus result = taskStatusService.updateTaskStatus(createTask.getId(), worker.getId(), "ACCEPTED");

        assertNotNull(result);
        assertEquals("ACCEPTED", result.getStatus());
    }

    @Test
    void updateTaskStatus_TaskNotFound_ThrowsException() {
        when(taskStatusRepo.findByTaskIdAndWorkerId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                taskStatusService.updateTaskStatus(createTask.getId(), worker.getId(), "ACCEPTED"));
    }

    @Test
    void getAllTasksForClient_ReturnsAllTasksWithWorkerInfo() {
        List<CreateTask> clientTasks = Collections.singletonList(createTask);
        when(createTaskRepo.findByUserId(anyLong())).thenReturn(clientTasks);

        List<Long> taskIds = Collections.singletonList(createTask.getId());
        when(taskStatusRepo.findByTaskIdIn(anyList())).thenReturn(Collections.singletonList(taskStatus));

        when(regUser.findAllById(anySet())).thenReturn(Arrays.asList(client, worker));

        List<ShowStatusDTO> results = taskStatusService.getAllTasksForClient(client.getId());

        assertFalse(results.isEmpty());
        ShowStatusDTO dto = results.get(0);
        assertEquals(createTask.getTitle(), dto.getTitle());
        assertEquals(worker.getFirstName(), dto.getWorkerFirstName());
        assertEquals(client.getFirstName(), dto.getFirstName());
    }

    @Test
    void confirmTask_ValidAcceptedTask_ConfirmsAndCreatesBusySlot() {
        taskStatus.setStatus("ACCEPTED");
        when(taskStatusRepo.findByTaskIdAndWorkerIdAndStatus(anyLong(), anyLong(), anyString()))
                .thenReturn(Optional.of(taskStatus));
        when(taskStatusRepo.save(any(TaskStatus.class))).thenReturn(taskStatus);

        TaskStatus result = taskStatusService.confirmTask(createTask.getId(), worker.getId());

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        verify(busySlotRepo, atLeastOnce()).save(any(BusySlot.class));
    }

    @Test
    void confirmTask_AlreadyConfirmed_ReturnsWithoutChanges() {
        taskStatus.setStatus("CONFIRMED");
        when(taskStatusRepo.findByTaskIdAndWorkerIdAndStatus(anyLong(), anyLong(), anyString()))
                .thenReturn(Optional.of(taskStatus));

        TaskStatus result = taskStatusService.confirmTask(createTask.getId(), worker.getId());

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        verify(busySlotRepo, never()).save(any(BusySlot.class));
    }

    @Test
    void confirmTask_NotAccepted_ThrowsException() {
        taskStatus.setStatus("ASSIGNED");
        when(taskStatusRepo.findByTaskIdAndWorkerIdAndStatus(anyLong(), anyLong(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(TaskStatusService.TaskConfirmationException.class, () ->
                taskStatusService.confirmTask(createTask.getId(), worker.getId()));
    }

    @Test
    void completeTask_ValidConfirmedTask_CompletesTask() {
        taskStatus.setStatus("CONFIRMED");
        when(taskStatusRepo.findByTaskIdAndWorkerIdAndStatus(anyLong(), anyLong(), anyString()))
                .thenReturn(Optional.of(taskStatus));
        when(taskStatusRepo.save(any(TaskStatus.class))).thenReturn(taskStatus);

        TaskStatus result = taskStatusService.completeTask(createTask.getId(), worker.getId());

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void incompleteTask_ValidConfirmedTask_MarksIncomplete() {
        taskStatus.setStatus("CONFIRMED");
        when(taskStatusRepo.findByTaskIdAndWorkerIdAndStatus(anyLong(), anyLong(), anyString()))
                .thenReturn(Optional.of(taskStatus));
        when(taskStatusRepo.save(any(TaskStatus.class))).thenReturn(taskStatus);

        TaskStatus result = taskStatusService.incompleteTask(createTask.getId(), worker.getId());

        assertNotNull(result);
        assertEquals("INCOMPLETED", result.getStatus());
    }

    @Test
    void saveBusySlotsFromConfirmedTasks_CreatesSlotsForConfirmedTasks() {
        taskStatus.setStatus("CONFIRMED");
        when(taskStatusRepo.findByStatus(anyString())).thenReturn(Collections.singletonList(taskStatus));
        when(createTaskRepo.findById(anyLong())).thenReturn(Optional.of(createTask));
        when(regUser.findById(anyLong())).thenReturn(Optional.of(client));

        taskStatusService.saveBusySlotsFromConfirmedTasks();

        verify(busySlotRepo, times(1)).save(any(BusySlot.class));
    }
}
