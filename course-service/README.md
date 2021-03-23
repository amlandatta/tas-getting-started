# A simple SpringBoot app with MySQL dependency deploy to TAS 


* #### Bumping Java to version 11
* #### Connection application to MySQL service on TAS
* #### Working with Custom User Provided Service (CUPS) and securing external credentials using Credhub on TAS

### Prequisites

* Java 11
* Maven
* cf cli v7
* TAS 2.10 (but should work with earlier versions)


### Update `pom.xml` to use Java 11

```xml
<properties>
    <java.version>11</java.version>
</properties>
```

### Update `pom.xml` to include JPA, DB2 (for local), MySQL dependencies

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

### Code changes to use JPA

Refer CourseController.java which uses CourseRepository.java (extends JpaRepository) to persist data in MySQL

### Property changes

Included `application-cloud.properties`. `cloud` profile is auto-injected when apps are deployed in Cloud Foundry.

```properties
spring.jpa.hibernate.ddl-auto=update
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### Code changes to use custom properties bound by TAS

Refer CourseController.java 

```java
@Value("${course-service.uri}")
private String uri;

@Value("${course-service.dbconnection}")
private String dbconnection;


@GetMapping("/custom-config")
public String getCustomConfig(){
    return this.uri;
}

@GetMapping("/secret-config")
public String getSecretConfig(){
    return this.dbconnection;
}
```

### Property changes

Updated `application.properties` to set default for local environment.

```properties
course-service.uri=${vcap.services.test-endpoint.credentials.uri:http://httpbin.org}
course-service.dbconnection=${vcap.services.external-db-service.credentials.dbconnection:secret}
```


### Build

```shell
cd ~/course-service
mvn clean package -DskipTests
mvn spring-boot:run
```

### Test app locally

Launch http://localhost:8080/courses

http://localhost:8080/courses/custom-config

http://localhost:8080/courses/secret-config

### Deploy app to TAS

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

### Create and bind MySQL service

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

### Create and bind custom service

```shell
#create custom user provided service (cups)
cf create-user-provided-service test-endpoint -p "uri"                                        uri> http://httpbin.org/get

cf bind-service course-service test-endpoint
cf restart course-service

#create credhub service
cf cs credhub default external-db-service -c '{"dbconnection":"oracle://oracle:oracle@course-db:1521/course"}'
cf bs course-service external-db-service
cf restart course-service
```

### Test application deployed on TAS

```shell
http --verify=no https://course-service.app-domain/courses

http --verify no https://course-service.app-domain/courses name="Spring Boot" duration=60
```

### Check environment variables set to an app

```shell
cf env course-service
Getting env variables for app course-service in org ... / space ... as ......
System-Provided:
VCAP_SERVICES: {
 "credhub": [
  {
   "binding_name": null,
   "credentials": {
    "credhub-ref": "/credhub-service-broker/credhub/7a11a5ab-6cbd-400f-bb44-fe05c41bfdf3/credentials"
   },
   "instance_name": "external-db-service",
   "label": "credhub",
   "name": "external-db-service",
   "plan": "default",
   "provider": null,
   "syslog_drain_url": null,
   "tags": [
    "credhub"
   ],
   "volume_mounts": []
  }
 ],
 "p.mysql": [
  {
   "binding_name": null,
   "credentials": {
    "hostname": "5a809347-7e2e-4669-9a98-ff0e00829108.mysql.service.internal",
    "jdbcUrl": "jdbc:mysql://5a809347-7e2e-4669-9a98-ff0e00829108.mysql.service.internal:3306/service_instance_db?user=e3ea246280f64d8ca499b0fb35c270cb\u0026password=3hnfy1c1jdqgdcre\u0026useSSL=false",
    "name": "service_instance_db",
    "password": "3hnfy1c1jdqgdcre",
    "port": 3306,
    "uri": "mysql://e3ea246280f64d8ca499b0fb35c270cb:3hnfy1c1jdqgdcre@5a809347-7e2e-4669-9a98-ff0e00829108.mysql.service.internal:3306/service_instance_db?reconnect=true",
    "username": "e3ea246280f64d8ca499b0fb35c270cb"
   },
   "instance_name": "course-db",
   "label": "p.mysql",
   "name": "course-db",
   "plan": "db-small",
   "provider": null,
   "syslog_drain_url": null,
   "tags": [
    "mysql"
   ],
   "volume_mounts": []
  }
 ],
 "user-provided": [
  {
   "binding_name": null,
   "credentials": {
    "uri": "http://httpbin.org/get"
   },
   "instance_name": "test-endpoint",
   "label": "user-provided",
   "name": "test-endpoint",
   "syslog_drain_url": "",
   "tags": [],
   "volume_mounts": []
  }
 ]
}

VCAP_APPLICATION: {
 "application_id": "f1d3aedd-3d73-487d-8e66-16ea4f0dc958",
 "application_name": "course-service",
 "application_uris": [
  "course-service..app-domain"
 ],
 "cf_api": "https://api.domain",
 "limits": {
  "fds": 16384
 },
 "name": "course-service",
 "organization_id": "6f8411ce-13ad-4ba7-a409-3d4deb3a65fd",
 "organization_name": "...",
 "space_id": "ea388138-afa7-4889-b4e8-5a3676afd545",
 "space_name": "...",
 "uris": [
  "course-service.app-domain"
 ],
 "users": null
}

User-Provided:
JBP_CONFIG_OPEN_JDK_JRE: { jre: { version: 11.+}}

No running env variables have been set

No staging env variables have been set
```


### Deploy using manifest

Refer `manifest.yml` and `vars-cloud.yml`

```
cf push --vars-file=vars-cloud.yml
```

### Scale application

```
cf scale course-service -i 2
```
### Rolling deployment

```
cf push --vars-file=vars-cloud.yml --var app-version="`date`" --strategy rolling
```

Note: replace `date` by application version

Simultaneously hit the application to check if application is always available

```shell
while true; do http http://course-service.cfapps.haas-490.pez.vmware.com/health/version -b --timeout 5 | tr -d '\n'; echo
"  "  `date`; sleep 1s; done
```