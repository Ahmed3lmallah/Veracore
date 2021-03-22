package com.veracore.opensourcescannerapi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class ScanRequest {
    @NotBlank private String userName;
    @NotBlank private String gitRepositoryOwner;
    @NotBlank private String gitRepositoryName;
    @NotBlank private String gitRepositoryBranch;
    @NotBlank private String projectLanguage;
}
