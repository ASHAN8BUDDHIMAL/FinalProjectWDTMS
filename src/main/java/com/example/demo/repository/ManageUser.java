package com.example.demo.repository;

import com.example.demo.model.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
@Repository
public interface ManageUser extends JpaRepository<UserRegistration, Long>{
    // 🔍 Search users by first name, last name, or email (case-insensitive)
    @Query("SELECT u FROM UserRegistration u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<UserRegistration> searchUsers(@Param("keyword") String keyword);

    // ✅ Find all active users
    List<UserRegistration> findByActiveTrue();

    // ✅ Find all inactive users
    List<UserRegistration> findByActiveFalse();

    // ✅ Find users by userType
    List<UserRegistration> findByUserType(String userType);

    // ✅ Find active users by userType
    List<UserRegistration> findByUserTypeAndActiveTrue(String userType);
}
