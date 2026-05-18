package org.bravatools.brava.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ValidationResponse {
    List<BrAPIEntity> entities;
}
