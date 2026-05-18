package org.bravatools.brava.model;


import com.atlassian.oai.validator.model.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestedEndpoint {
    private String path;
    private Request.Method method;
    private String summaryMessage;
    private long responseTime;
    private int statusCode;
    private List<Validation> validations;
    private ValidationSuccessLevel validationSuccessLevel = ValidationSuccessLevel.SUCCESS;
}
