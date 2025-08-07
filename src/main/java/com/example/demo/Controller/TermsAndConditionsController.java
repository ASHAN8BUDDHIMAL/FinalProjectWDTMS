package com.example.demo.Controller;

import com.example.demo.Service.TermsAndConditionsService;
import com.example.demo.model.TermsAndConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/terms")
@CrossOrigin
public class TermsAndConditionsController {

    @Autowired
    private TermsAndConditionsService service;

    @GetMapping
    public TermsAndConditions getTerms() {
        return service.getLatestTerms();
    }

    @PostMapping
    public TermsAndConditions createTerms(@RequestBody String content) {
        return service.createTerms(content);
    }

    @PutMapping("/{id}")
    public TermsAndConditions updateTerms(@PathVariable Long id, @RequestBody String content) {
        return service.updateTerms(id, content);
    }
}
