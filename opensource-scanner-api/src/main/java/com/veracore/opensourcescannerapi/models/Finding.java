package com.veracore.opensourcescannerapi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Finding {
    private String name;
    private String version;
    private String location;
    private String language;
    private String severity;
    private String recommendation;

    public Finding(String name, String version, String location, String language, String severity, String recommendation) {
        this.name = name;
        this.version = version;
        this.location = location;
        this.language = language;
        this.severity = severity;
        this.recommendation = recommendation;
    }
}
