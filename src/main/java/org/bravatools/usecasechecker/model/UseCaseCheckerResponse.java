package org.bravatools.usecasechecker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.brapi.usecasechecker.model.checked.CheckedUseCase;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UseCaseCheckerResponse {
    private String appName;
    private String serverUrl;
    private List<CheckedUseCase> checkedUseCases;
}
