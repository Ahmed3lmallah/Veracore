package com.veracore.opensourcescannerapi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.veracore.opensourcescannerapi.models.*;
import com.veracore.opensourcescannerapi.models.mvn.MVNDependencies;
import com.veracore.opensourcescannerapi.utils.feign.UsersClient;
import com.veracore.opensourcescannerapi.utils.feign.VulnerabilitiesClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

@Service
public class ScannerService {

    private UsersClient usersClient;
    private VulnerabilitiesClient vulnerabilitiesClient;
    private final String[] supportedLanguages = { "mvn" };

    @Value("${gitAuthUsername}")
    private String gitAuthUsername;
    @Value("${gitAuthPassword}")
    private String gitAuthPassword;

    @Autowired
    public ScannerService(UsersClient usersClient, VulnerabilitiesClient vulnerabilitiesClient) {
        this.usersClient = usersClient;
        this.vulnerabilitiesClient = vulnerabilitiesClient;
    }

    public ScanResult initiateScan(ScanRequest scanRequest) throws IOException, URISyntaxException {
        ScanResult result = new ScanResult();

        // First, we validate user
        boolean isValidUser = isValidUser(scanRequest.getUserName());

        // Make sure project language is supported
        boolean isSupportedLanguage = Arrays.stream(supportedLanguages).anyMatch(scanRequest.getProjectLanguage()::equals);

        if (!isValidUser || !isSupportedLanguage) {
            result.setValidUser(isValidUser);
            result.setSupportedLanguage(isSupportedLanguage);
            result.setFindings(null);
            return result;
        }

        // Second, we scan the Git Repo for dependencies
        Set<Dependency> projectDependencies = new HashSet<>();
        scanGitForProjectDependencies(
                scanRequest.getGitRepositoryOwner(),
                scanRequest.getGitRepositoryName(),
                scanRequest.getGitRepositoryBranch(),
                "", // We start from the root directory
                scanRequest.getProjectLanguage(),
                projectDependencies);

        // Lastly, we check whether each dependency exist in the vulnerabilities DB
        // And add to the findings list
        Set<Finding> findings = new HashSet<>();
        projectDependencies.forEach(dependency -> {
            System.out.println("project dependency: " + dependency);
            if (dependency.getName() != null && dependency.getVersion() != null) {
                CollectionModel<Vulnerability> vulnerabilities = vulnerabilitiesClient.getVulnerability(dependency.getName(), dependency.getVersion());
                if (vulnerabilities.getContent().size() > 0) {
                    System.out.println("Found Vulnerabilities!!");
                    vulnerabilities.forEach( vulnerability -> {
                        findings.add(new Finding(
                                vulnerability.getDependency(),
                                vulnerability.getVersion(),
                                dependency.getLocation(),
                                vulnerability.getLanguage(),
                                vulnerability.getSeverity(),
                                vulnerability.getRecommendation()

                        ));
                    });
                }
            }
        });

        result.setValidUser(true);
        result.setSupportedLanguage(true);
        result.setFindings(findings);

        return result;
    }

    private boolean isValidUser(String userName) {
        return usersClient.isExistingUser(userName.toLowerCase());
    }

    private void scanGitForProjectDependencies(
            String repoOwner, String repoName, String branch, String path,
            String projectLanguage,
            Set<Dependency> projectDependencies) throws IOException, URISyntaxException {

        /*
         * Call GitHub REST API - https://docs.github.com/en/rest/reference/repos#get-repository-content
         */
        // TODO: USE OAUTH2
        HttpHeaders headers = new HttpHeaders() {{
            String auth = gitAuthUsername + ":" + gitAuthPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;
            set( "Authorization", authHeader );
        }};
        HttpEntity entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> contentList = restTemplate.exchange(
                "https://api.github.com/repos/{owner}/{repo}/contents/{path}?ref={branch}",
                HttpMethod.GET, entity, List.class, repoOwner, repoName, path, branch);

        // To print response JSON, using GSON. Any other JSON parser can be used here.
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("<JSON RESPONSE START>\n" + gson.toJson(contentList.getBody()) + "\n<JSON RESPONSE END>\n");

        // Iterate through list of file metadata from response.
        for (Map content : (List<Map>) contentList.getBody()) {

            // Get file name & raw file download URL from response.
            String fileName = (String) content.get("name");
            String downloadUrl = (String) content.get("download_url");
            String contentType = (String) content.get("type");
            String contentPath = (String) content.get("path");
            String contentLocation = (String) content.get("html_url");

            // Only supporting MVN now
            if (projectLanguage.equals("mvn")) {
                if (contentType.equals("dir")){    
                    // Using Recursion to scan nested directories
                    scanGitForProjectDependencies(repoOwner, repoName, branch, contentPath, projectLanguage, projectDependencies);
                    // TODO: DEEP NESTED REPOS FAIL with 403 rate limit exceeded without Authentication
                    // Potential Solution: Clone Repo to a tmp folder and process
                } else if (contentType.equals("file") && fileName != null && fileName.contains("pom.xml")) {
                    /*
                     * Get file content as string
                     */
                    String fileContent = IOUtils.toString(new URI(downloadUrl), Charset.defaultCharset());
                    System.out.println("\n<FILE CONTENT START>\n" + fileContent + "\n<FILE CONTENT END>\n");

                    /*
                     * Extracting Dependencies using regex or substringBetween
                     */
                    String dependenciesXML = "<dependencies>\n" +
                            StringUtils.substringBetween(fileContent, "<dependencies>", "</dependencies>") +
                            "\n</dependencies>";
                    System.out.println("\n<Dependencies CONTENT START>\n" + dependenciesXML + "\n<Dependencies CONTENT END>\n");

                    // Convert to POJO
                    ObjectMapper objectMapper = new XmlMapper();
                    MVNDependencies mvnDependencies = objectMapper.readValue(dependenciesXML, MVNDependencies.class);
                    System.out.println(mvnDependencies);

                    Arrays.stream(mvnDependencies.getDependency()).forEach( dependency -> {
                        projectDependencies.add(new Dependency(
                                dependency.getGroupId() + ":" + dependency.getArtifactId(),
                                dependency.getVersion(),
                                contentLocation,
                                "mvn"
                        ));
                    });
                }
            }
        }
    }
}
