package org.bravatools.brava.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationRequest {
    private String serverUrl;
    private String version;
    private String token;
}
