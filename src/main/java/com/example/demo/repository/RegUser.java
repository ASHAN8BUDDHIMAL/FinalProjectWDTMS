package com.example.demo.repository;
import com.example.demo.model.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegUser extends JpaRepository<UserRegistration, Long> {
    Optional<UserRegistration> findByEmailAndPassword(String email, String password);
    Optional<UserRegistration> findById(Long id);
    Optional<UserRegistration> findByEmail(String email);

    List<RegUser> findByIdIn(List<Long> ids);
//    Optional<UserRegistration> findById(Long id);

    List<UserRegistration> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    @Query("SELECT COUNT(u) FROM UserRegistration u WHERE u.userType = :userType AND YEAR(u.createdAt) = :year AND MONTH(u.createdAt) = :month")
    int countByUserTypeAndCreatedAtMonth(String userType, int year, int month);

    @Query("SELECT u.city, COUNT(u) FROM UserRegistration u GROUP BY u.city")
    List<Object[]> countUsersByCity();

}





