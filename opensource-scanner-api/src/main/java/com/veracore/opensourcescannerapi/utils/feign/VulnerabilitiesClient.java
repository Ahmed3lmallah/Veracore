package com.veracore.opensourcescannerapi.utils.feign;

import com.veracore.opensourcescannerapi.models.Vulnerability;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "vulnerabilities-service")
public interface VulnerabilitiesClient {

    @GetMapping(value = "/vulnerabilities/{id}")
    Vulnerability getVulnerability(int id);

    @GetMapping(value = "/vulnerabilities/search/findByDependencyAndVersion")
    CollectionModel<Vulnerability> getVulnerability(@RequestParam String dependency, @RequestParam String version);

    @GetMapping(value = "/vulnerabilities")
    List<Vulnerability> getAllVulnerabilities();
}
