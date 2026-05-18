package org.bravatools.brava.model.spec;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Version {
    private String name;
    private List<String> moduleUrls;
}
