package com.example.demo.repository;
import com.example.demo.model.UserRegistration;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegUser extends JpaRepository<UserRegistration, Long> {
    Optional<UserRegistration> findByEmailAndPassword(String email, String password);
    UserRegistration findById(long id);
    Optional<UserRegistration> findByEmail(String email);

    List<RegUser> findByIdIn(List<Long> ids);
}



