package org.bravatools.usecasechecker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.bravatools.usecasechecker.model.UseCaseCheckerResponse;
import org.bravatools.usecasechecker.service.UseCaseCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usecasechecker")
@Slf4j
public class UseCaseController {

    private final UseCaseCheckerService useCaseCheckerService;

    @Autowired
    public UseCaseController(UseCaseCheckerService useCaseCheckerService) {
        this.useCaseCheckerService = useCaseCheckerService;
    }

    @GetMapping("/apps")
    public ResponseEntity<List<String>> availableBrapps() throws JsonProcessingException {
        List<String> result = useCaseCheckerService.getAvailableApps();


        return ResponseEntity.ok(result);
    }

    @GetMapping("/appusecases")
    public ResponseEntity<List<String>> availableUseCasesForApp(@RequestParam(name = "app")
                                                                 String app) {
        List<String> result = useCaseCheckerService.getAppUseCasesByAppName(app);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/check")
    public ResponseEntity<UseCaseCheckerResponse> checkUseCasesForApp(@RequestParam(name = "app")
                                                       String app,
                                                                      @RequestParam(name = "useCase", required = false)
                                                       String useCase,
                                                                      @RequestParam(name = "allUseCases", required = false)
                                                       Boolean allUseCases,
                                                                      @RequestParam(name = "url")
                                                       String url) {
        UseCaseCheckerResponse useCaseCheckerResponse = useCaseCheckerService.checkUseCasesForApp(app, useCase, allUseCases, url);


        return ResponseEntity.ok(useCaseCheckerResponse);
    }
}
