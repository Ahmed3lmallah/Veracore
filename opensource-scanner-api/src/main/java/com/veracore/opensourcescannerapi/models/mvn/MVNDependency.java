package com.veracore.opensourcescannerapi.models.mvn;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MVNDependency {
    @JacksonXmlProperty(localName = "groupId")
    private String groupId;
    @JacksonXmlProperty(localName = "artifactId")
    private String artifactId;
    @JacksonXmlProperty(localName = "version")
    private String version;
    @Ignore
    @JacksonXmlProperty(localName = "scope")
    private String scope;
}
