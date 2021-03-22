package com.veracore.opensourcescannerapi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class ScanResult {
    private boolean validUser;
    private boolean supportedLanguage;
    private Set<Finding> findings;
}
