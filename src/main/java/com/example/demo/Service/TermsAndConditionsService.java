package com.example.demo.Service;

import com.example.demo.model.TermsAndConditions;
import com.example.demo.repository.TermsAndConditionsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TermsAndConditionsService {

    @Autowired
    private TermsAndConditionsRepo repository;

    public TermsAndConditions getLatestTerms() {
        return repository.findAll()
                .stream()
                .reduce((first, second) -> second)
                .orElse(null);
    }

    public TermsAndConditions createTerms(String content) {
        TermsAndConditions terms = new TermsAndConditions(content);
        return repository.save(terms);
    }

    public TermsAndConditions updateTerms(Long id, String content) {
        Optional<TermsAndConditions> optional = repository.findById(id);
        if (optional.isPresent()) {
            TermsAndConditions terms = optional.get();
            terms.setContent(content);
            return repository.save(terms);
        } else {
            throw new RuntimeException("Terms not found with id: " + id);
        }
    }
}
