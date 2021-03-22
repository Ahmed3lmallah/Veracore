package com.veracore.opensourcescannerapi.models.mvn;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;


@JacksonXmlRootElement(localName = "dependencies")
@Data
@NoArgsConstructor
public class MVNDependencies {
    @JacksonXmlElementWrapper(localName = "dependency", useWrapping = false)
    private MVNDependency[] dependency;
}
