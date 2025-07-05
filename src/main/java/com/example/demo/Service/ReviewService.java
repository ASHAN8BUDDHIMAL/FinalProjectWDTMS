package com.example.demo.Service;



import com.example.demo.DTO.ReviewResponseDTO;
import com.example.demo.model.CreateTask;
import com.example.demo.model.Review;
import com.example.demo.model.UserRegistration;
import com.example.demo.repository.CreateTaskRepo;
import com.example.demo.repository.RegUser;
import com.example.demo.repository.ReviewRepo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private CreateTaskRepo taskRepo;

    @Autowired
    private RegUser userRepo;

    public Review createReview(Long taskId, Long workerId, Long userId, Integer rating, String text, MultipartFile image, HttpSession session) throws IOException {
        Long sessionUserId = (Long) session.getAttribute("loggedInUserId");
        if (!userId.equals(sessionUserId)) {
            throw new RuntimeException("Unauthorized");
        }

        CreateTask task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Task doesn't belong to user");
        }

        userRepo.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        if (reviewRepo.existsByTaskIdAndUserId(taskId, userId)) {
            throw new RuntimeException("Already reviewed this task");
        }

        // You might need to get the task status for this worker to validate rating requirement
        // If you have a TaskStatus repository or service, check task status here (pseudo-code):
        // String taskStatus = taskStatusRepo.findStatusByTaskIdAndWorkerId(taskId, workerId);
        // if ("COMPLETED".equalsIgnoreCase(taskStatus) && (rating == null || rating < 1 || rating > 5)) {
        //     throw new RuntimeException("Rating must be between 1 and 5 for completed tasks");
        // }
        // For now, assuming rating can be null if not completed.

        Review review = new Review();
        review.setTaskId(taskId);
        review.setWorkerId(workerId);
        review.setUserId(userId);
        review.setText(text);

        if (rating != null) {
            review.setRating(rating);
        }

        if (image != null && !image.isEmpty()) {
            review.setImage(image.getBytes());
        }

        return reviewRepo.save(review);
    }


    public List<ReviewResponseDTO> getReviewsForWorker(Long workerId) {
        List<Review> reviews = reviewRepo.findByWorkerId(workerId);

        return reviews.stream().map(review -> {
            UserRegistration user = userRepo.findById(review.getUserId()).orElse(null);
            String userName = (user != null) ? user.getFirstName() + " " + user.getLastName() : "Unknown";
            String base64Image = null;
            if (review.getImage() != null) {
                base64Image = Base64.getEncoder().encodeToString(review.getImage());
            }

            return new ReviewResponseDTO(
                    review.getId(),
                    review.getUserId(),
                    userName,
                    review.getRating(),
                    review.getText(),
                    base64Image,
                    review.getCreatedAt()
            );
        }).collect(Collectors.toList());
    }


}
