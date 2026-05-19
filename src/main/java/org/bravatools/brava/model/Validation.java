package org.bravatools.brava.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Validation {
    private String message;
    private ValidationSuccessLevel validationSuccessLevel =  ValidationSuccessLevel.WARNING;

}
