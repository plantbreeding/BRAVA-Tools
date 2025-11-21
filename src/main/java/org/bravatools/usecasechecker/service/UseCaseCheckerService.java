package org.bravatools.usecasechecker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.brapi.usecasechecker.UseCaseChecker;
import org.brapi.usecasechecker.UseCaseCheckerFactory;
import org.brapi.usecasechecker.exceptions.UseCaseCheckerException;
import org.brapi.usecasechecker.model.checked.CheckedUseCase;
import org.brapi.usecasechecker.model.useCases.App;
import org.brapi.usecasechecker.model.useCases.UseCase;
import org.brapi.usecasechecker.model.useCases.UseCases;
import org.bravatools.exceptions.BravaToolsServerError;
import org.bravatools.usecasechecker.model.UseCaseCheckerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UseCaseCheckerService {

    private static final Logger log = LoggerFactory.getLogger(UseCaseCheckerService.class);

    private final ObjectMapper objectMapper;
    private final UseCaseCheckerFactory useCaseCheckerFactory;
    private final UseCases loadedUseCases;

    @Autowired
    public UseCaseCheckerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.useCaseCheckerFactory = UseCaseCheckerFactory.getInstance();
        this.loadedUseCases = useCaseCheckerFactory.getLoadedApps();
    }

    public List<String> getAvailableApps() {
        return loadedUseCases.getApps()
                .stream()
                .map(App::getAppName)
                .toList();
    }

    public List<String> getAppUseCasesByAppName(String appName) {
        App app = loadedUseCases.getApps()
                .stream()
                .filter(a -> a.getAppName().equalsIgnoreCase(appName))
                .findFirst()
                .orElse(null);

        if (app != null) {
            return app.getUseCases().stream().map(UseCase::getUseCaseName).toList();
        }

        log.error("No use cases found for app with name [{}]", appName);
        return Collections.emptyList();
    }

    public UseCaseCheckerResponse checkUseCasesForApp(String appName,
                                                      String useCaseName,
                                                      boolean allUseCases,
                                                      String url) {
        UseCaseChecker useCaseChecker = useCaseCheckerFactory.getUseCaseChecker(url);

        if (useCaseName == null || useCaseName.isEmpty()) {
            allUseCases = true;
        }

        UseCaseCheckerResponse response;

        try {
            if (!allUseCases) {
                response = new UseCaseCheckerResponse(appName, url, List.of(useCaseChecker.isUseCaseCompliant(appName, useCaseName)));
            } else {
                response = new UseCaseCheckerResponse(appName, url, useCaseChecker.allUseCasesCompliant(appName));
            }

        } catch (UseCaseCheckerException e) {
            String message = String.format("Use Case Checking for app failed for app [%s] use case [%s] url [%s]:", appName, useCaseChecker, url);
            log.error(message);
            log.error("[{}]", e.getMessage());
            throw new BravaToolsServerError(message, e);
        }

        return response;
    }
}
