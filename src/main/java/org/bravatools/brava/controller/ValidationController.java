package org.bravatools.brava.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.bravatools.brava.model.ValidationRequest;
import org.bravatools.brava.model.ValidationResponse;
import org.bravatools.brava.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validator")
@Slf4j
public class ValidationController {

    private ValidationService validationService;

    @Autowired
    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/validateall")
    public ResponseEntity<ValidationResponse> availableBrapps(
            @RequestBody ValidationRequest validationRequest
    ) throws JsonProcessingException {
        ValidationResponse result = validationService.validateAll(validationRequest);

        return ResponseEntity.ok(result);
    }
}
