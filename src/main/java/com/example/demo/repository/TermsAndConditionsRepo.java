package com.example.demo.repository;

import com.example.demo.model.TermsAndConditions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsAndConditionsRepo extends JpaRepository<TermsAndConditions, Long> {
}
