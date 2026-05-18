package org.bravatools.brava.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BrAPIEntity {
    private String name;
    private List<TestedEndpoint> testedEndpoints;
    private ValidationSuccessLevel validationSuccessLevel = ValidationSuccessLevel.SUCCESS;
}
