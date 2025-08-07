package com.example.demo.Service;
import com.example.demo.model.UserRegistration;
import com.example.demo.model.Worker;
import com.example.demo.repository.ClientRepo;
import com.example.demo.repository.RegUser;
import com.example.demo.repository.WorkerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private RegUser regUser;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private WorkerRepo workerRepo;

    public UserRegistration create(UserRegistration userReg) {
        return regUser.save(userReg);
    }

    public List<UserRegistration> getAllUsers() {
        return regUser.findAll();
    }

    public UserRegistration findByEmailAndPassword(String email, String password) {
        return regUser.findByEmailAndPassword(email, password).orElse(null);
    }


    public Optional<UserRegistration> findById(Long id) {
        return regUser.findById(id); // This is correct usage
    }


    public void uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        UserRegistration user = regUser.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfilePicture(file.getBytes());
        regUser.save(user);
    }

    public byte[] getProfilePictureByEmail(String email) {
        UserRegistration user = regUser.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getProfilePicture();
    }

    public String saveWorkerDetails(Long userId, Worker worker) {
        // Set user ID manually (from session)
        worker.setUserId(userId);
        workerRepo.save(worker);
        return "Worker details saved successfully.";
    }

    public String updateWorkerDetails(Long userId, Worker worker) {
        Optional<Worker> existing = workerRepo.findByUserId(userId);
        if (existing.isPresent()) {
            Worker existingWorker = existing.get();
            existingWorker.setSkills(worker.getSkills());
            existingWorker.setChargePerHour(worker.getChargePerHour());
            workerRepo.save(existingWorker);
            return "Worker details updated successfully.";
        }
        return "Worker details not found.";
    }

    public Worker getWorkerDetails(Long userId) {
        return workerRepo.findByUserId(userId).orElse(null);
    }

    public List<UserRegistration> searchUsersByName(String name) {
        return regUser.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

}
