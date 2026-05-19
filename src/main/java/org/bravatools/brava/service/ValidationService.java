package org.bravatools.brava.service;

import com.atlassian.oai.validator.report.ValidationReport;
import io.swagger.v3.oas.models.OpenAPI;
import org.brapi.schematools.analyse.AnalysisOptions;
import org.brapi.schematools.analyse.AnalysisReport;
import org.brapi.schematools.analyse.BrAPISpecificationAnalyserFactory;
import org.brapi.schematools.core.authorization.NoAuthorizationProvider;
import org.brapi.schematools.core.response.Response;
import org.bravatools.brava.model.*;
import org.bravatools.brava.model.spec.SupportedSpecs;
import org.bravatools.brava.model.spec.Version;
import org.bravatools.config.BrAPISchemaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
public class ValidationService {

    private final SupportedSpecs supportedSpecs;
    private final HttpClient httpClient;


    @Autowired
    public ValidationService(BrAPISchemaProperties brAPISchemaProperties) {
        this.supportedSpecs = brAPISchemaProperties.getSupportedSpecs();
        this.httpClient = HttpClient.newHttpClient();
    }

    public ValidationResponse validateAll(ValidationRequest validationRequest) {
        List<String> moduleSchemaUrls = schemaUrlsForVersion(validationRequest.getVersion());

        List<BrAPISpecificationAnalyserFactory.Analyser> analysers
                = produceBrAPIAnalysersForEachSchemaModule(moduleSchemaUrls, validationRequest.getServerUrl(), validationRequest.getToken());

        // TODO: Optimize checks by only running against server-info.  For now, just check all endpoints in the schemas.

        Map<String, List<AnalysisReport>> analysisReportsByEntityTag = getAnalysisReportsByEntityTag(analysers);

        ValidationResponse validationResponse = new ValidationResponse();

        List<BrAPIEntity> brapiEntities = new ArrayList<>();

        for (Map.Entry<String, List<AnalysisReport>> entry : analysisReportsByEntityTag.entrySet()) {
            BrAPIEntity brAPIEntity = new BrAPIEntity();

            brAPIEntity.setName(entry.getKey());

            List<TestedEndpoint>  testedEndpoints = buildTestedEndpoints(entry.getValue());

            brAPIEntity.setTestedEndpoints(testedEndpoints);

            if (brAPIEntity.getTestedEndpoints().stream().anyMatch(te -> te.getValidationSuccessLevel().equals(ValidationSuccessLevel.ERROR))) {
                brAPIEntity.setValidationSuccessLevel(ValidationSuccessLevel.ERROR);
            } else if (brAPIEntity.getTestedEndpoints().stream().anyMatch(te -> te.getValidationSuccessLevel().equals(ValidationSuccessLevel.WARNING))) {
                brAPIEntity.setValidationSuccessLevel(ValidationSuccessLevel.WARNING);
            }

            brapiEntities.add(brAPIEntity);
        }

        validationResponse.setEntities(brapiEntities);

        return validationResponse;
    }

    private List<String> schemaUrlsForVersion(String version) {
        List<String> moduleSchemaUrls = supportedSpecs.getVersions()
                .stream()
                .filter(v -> v.getName().equals(version))
                .findFirst()
                .map(Version::getModuleUrls)
                .orElse(List.of());

        if (moduleSchemaUrls.isEmpty()) {
            throw new IllegalStateException("No module urls found for submitted version " + version);
        }

        return moduleSchemaUrls;
    }

    private List<BrAPISpecificationAnalyserFactory.Analyser> produceBrAPIAnalysersForEachSchemaModule(List<String> moduleSchemaUrls,
                                                                                                      String serverBaseUrl,
                                                                                                      String token) {
        List<BrAPISpecificationAnalyserFactory.Analyser> analysers = new ArrayList<>();

        String spec = null;

        for (String moduleSchemaUrl : moduleSchemaUrls) {
            try {
                HttpRequest request = HttpRequest.newBuilder(URI.create(moduleSchemaUrl)).build();

                spec = httpClient
                        .send(request, HttpResponse.BodyHandlers.ofString())
                        .body();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            var analyser = new BrAPISpecificationAnalyserFactory(serverBaseUrl,
                    httpClient,
                    // TODO: Handle token use case
                    new NoAuthorizationProvider(),
                    // TODO: Once more documentation on configuration, load in custom analysis-options files depending on user input.  Implement programmatic configuration as well
                    AnalysisOptions.load()
            ).analyser(spec);

            analysers.add(analyser);
        }
        return analysers;
    }

    private Map<String, String> getPathToTagMap(OpenAPI openAPI) {
        Map<String, String> pathToTagMap = new HashMap<>();

        openAPI.getPaths()
                .forEach((path, pathItem) -> {

                    var tag = pathItem.readOperations()
                                    .getFirst()
                                    .getTags()
                                    .getFirst();

                    pathToTagMap.put(path, tag);
                });
        return pathToTagMap;
    }

    private Map<String, List<AnalysisReport>> getAnalysisReportsByEntityTag(List<BrAPISpecificationAnalyserFactory.Analyser> analysers) {
        Map<String, List<AnalysisReport>> analysisReportsByEntityTag = new HashMap<>();

        for (BrAPISpecificationAnalyserFactory.Analyser analyser : analysers) {
            Response<List<AnalysisReport>> analyserResponse = analyser.analyseAll();
            List<AnalysisReport> analysisReports = analyserResponse.getResult();

            Map<String, String> entityTagByPath = getPathToTagMap(analyser.getOpenAPI());

            for (AnalysisReport analysisReport : analysisReports) {

                var reportPath = analysisReport.getRequest().getValidatorRequest().getPath();
                var entityTag = entityTagByPath.get(reportPath);

                if (analysisReportsByEntityTag.containsKey(entityTag)) {
                    analysisReportsByEntityTag.get(entityTag).add(analysisReport);
                } else {
                    analysisReportsByEntityTag.put(entityTag, new ArrayList<>(List.of(analysisReport)));
                }
            }
        }

        return analysisReportsByEntityTag;
    }

    private List<TestedEndpoint> buildTestedEndpoints(List<AnalysisReport> analysisReports) {

        List<TestedEndpoint> testedEndpoints = new ArrayList<>();

        for (AnalysisReport analysisReport : analysisReports) {
            TestedEndpoint testedEndpoint = new TestedEndpoint();

            testedEndpoint.setPath(analysisReport.getRequest().getValidatorRequest().getPath());
            testedEndpoint.setMethod(analysisReport.getRequest().getValidatorRequest().getMethod());
            testedEndpoint.setResponseTime(analysisReport.getTimeElapsed());


            if (analysisReport.getStatusCode() < 200 || analysisReport.getStatusCode() >= 300) {

                Validation validation = new Validation();

                validation.setValidationSuccessLevel(ValidationSuccessLevel.ERROR);
                testedEndpoint.setValidationSuccessLevel(ValidationSuccessLevel.ERROR);
                validation.setMessage(String.format("Validation Failed with status code: %s", analysisReport.getStatusCode()));

                testedEndpoint.setValidations(List.of(validation));
                testedEndpoints.add(testedEndpoint);
                // No need to look at other validations that failed if status code is not in success range.
                continue;
            }

            testedEndpoint.setStatusCode(analysisReport.getStatusCode());

            // TODO: Add support to include full requests and responses in result.  This can likely be done using analyze tool with cached variables
            // TODO: Should we should show messages if everything is successful? What should we show, and does schema tools support this.
            if (analysisReport.getValidationReport().hasErrors()) {

                List<String> messages = analysisReport.getValidationReport()
                                .getMessages()
                                .stream()
                                .map(ValidationReport.Message::getMessage)
                                .toList();

                testedEndpoint.setValidations(buildValidations(messages));
                testedEndpoint.setValidationSuccessLevel(ValidationSuccessLevel.WARNING);
            }

            testedEndpoints.add(testedEndpoint);
        }
        return testedEndpoints;
    }

    private List<Validation> buildValidations(List<String> validationMessages) {

        List<Validation> validations = new ArrayList<>();

        for (String validationMessage : validationMessages) {
            Validation validation = new Validation();

            validation.setValidationSuccessLevel(ValidationSuccessLevel.WARNING);
            validation.setMessage(validationMessage);

            validations.add(validation);
        }

        return validations;
    }
}
