package com.example.demo.repository;
import com.example.demo.model.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegUser extends JpaRepository<UserRegistration, Long> {

    }

