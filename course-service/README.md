# A simple SpringBoot app with MySQL dependency deploy to TAS 

### Bumping Java to version 11
### Connection application to MySQL service on TAS

###Prequisites

* Java 11
* Maven
* cf cli v7
* TAS 2.10 (but should work with earlier versions)


###Update `pom.xml` to use Java 11

```xml
<properties>
    <java.version>11</java.version>
</properties>
```

###Update `pom.xml` to include JPA, DB2 (for local), MySQL dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```

###Code changes

Refer CourseController.java which uses CourseRepository.java (extends JpaRepository) to persist data in MySQL

###Property changes

Included `application-cloud.properties`. `cloud` profile is auto-injected when apps are deployed in Cloud Foundry.

```properties
spring.jpa.hibernate.ddl-auto=update
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

###Build

```shell
cd ~/course-service
mvn clean package -DskipTests
mvn spring-boot:run
```

###Test app locally

Launch http://localhost:8080/courses

###Deploy app to TAS

```shell
cf push course-service -p ./target/course-service-0.0.1-SNAPSHOT.jar \
  -b java_buildpack_offline -m 2G 

# Set environment variable
#cf set-env APP_NAME ENV_VAR_NAME ENV_VAR_VALUE
cf set-env course-service JBP_CONFIG_OPEN_JDK_JRE '{ jre: { version: 11.+}}'

# Use 'cf restage course-service' to ensure your env variable changes take effect.
cf restage course-service

# View app details
cf app course-service
```

###Create and bind MySQL service

```shell
cf create-service p.mysql db-small course-db
watch create-service course-db

name:             course-db
service:          p.mysql
tags:             
plan:             db-small
description:      Dedicated instances of MySQL
dashboard:        
service broker:   dedicated-mysql-broker

Showing status of last operation from service course-db...

status:    create succeeded
message:   Instance provisioning completed


# wait till the service status is "create succeeded"
cf bind-service course-service course-db
cf restart course-service

````

###Test application deployed on TAS

```shell
http --verify=no https://course-service.app-domain/courses

http --verify no https://course-service.app-domain/courses name="Spring Boot" duration=60
```

### Check environment variables set to an app

```shell
cf env course-service
Getting env variables for app course-service in org ... / space ... as ......
System-Provided:
VCAP_SERVICES: {}

VCAP_APPLICATION: {
 "application_id": "f1d3aedd-3d73-487d-8e66-16ea4f0dc958",
 "application_name": "course-service",
 "application_uris": [
  "course-service...."
 ],
 "cf_api": "https://api...",
 "limits": {
  "fds": 16384
 },
 "name": "course-service",
 "organization_id": "6f8411ce-13ad-4ba7-a409-3d4deb3a65fd",
 "organization_name": "...",
 "space_id": "ea388138-afa7-4889-b4e8-5a3676afd545",
 "space_name": "...",
 "uris": [
  "course-service..."
 ],
 "users": null
}

User-Provided:
JBP_CONFIG_OPEN_JDK_JRE: { jre: { version: 11.+}}

No running env variables have been set

No staging env variables have been set
```