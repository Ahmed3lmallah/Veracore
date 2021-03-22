package com.veracore.opensourcescannerapi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Dependency {

    private String name;
    private String version;
    private String location;
    private String language;

    public Dependency(String name, String version, String location, String language) {
        this.name = name;
        this.version = version;
        this.location = location;
        this.language = language;
    }
}
