# Veracore

Veracore (not to be confused with Veracode) is a demo tool that can be used to scan Git repositories for opensource vulnerabilities.

## Architecture

Veracore design is based on modern cloud-native architecture, and it includes the following microservices:

![Project Architecture](./images/cloud-native-design.png)

1. **CONFIG-SERVER:** A Configuration Server to host all configuration files
	
	![CONFIG-SERVER](./images/config.png)
1. **REGISTRY-SERVICE:** Eureaka Service Registry for server-side load balancing

	![REGISTRY-SERVICE](./images/eureka.png)
1. Two backend micoservices that serve as the Data layer and used for fetching data from a PostgreSQL DB
	1. **USER-SERVICE:** Fetches and validates user data - Initialized with the following users:
	
		![USER-SERVICE](./images/u-service.png)
	1. **VULNERABILITIES-SERVICE:** Fetches vulnerabilities from DB - Initialized with the following vulnerability:
	
		![VULNERABILITIES-SERVICE](./images/v-service.png)
1. **OPENSOURCE-SCANNER-SERVICE:** An edge micoservice, that collects data from differnt backend services, scan and fetch dependencies for a provided Github repo, and present the results to the user.
	* Request Example: POST http://localhost:8191/scan
			{
				"userName":"ahmed",
				"gitRepositoryOwner":"Ahmed3lmallah",
				"gitRepositoryName":"test",
				"gitRepositoryBranch":"master",
				"projectLanguage":"mvn"
			}
	* Result Example:
			{
				"validUser": true,
				"supportedLanguage": true,
				"findings": [
					{
						"name": "com.fasterxml.jackson.core:jackson-databind",
						"version": "2.9.9",
						"location": "https://github.com/Ahmed3lmallah/test2/blob/master/queue-service/pom.xml",
						"language": "mvn",
						"severity": "Severe",
						"recommendation": "Upgrade com.fasterxml.jackson.core:jackson-databind to version 2.9.10.7 or later"
					}
				]
			}
			
![Request Example](./images/1.png)
	
## Limitations

* The tool currently only works for `mvn` projects, but it can be extended to include other project types
* For larger Git repos with many files and nested folders, the tool will likely experience throttling from Github Content API
	* An alternative approach would be to clone the Repo and process it server-side
* I am sure the tools is filled with bugs, but it's just a demo project. I hope you like it :)

## Areas for improvement

If I had more time, I would have added the following:

* Use Oauth2 for authentication, instead of the current dummy **USER-SERVICE**
* Better General Exception handling, using `@RestControllerAdvice`
* A Caching solution, instead of always fetching vulnerabilities from backend
* Unit test coverage
