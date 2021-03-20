# A simple SpringBoot app

### Build and run locally

##@ Prequisites

* Java 8
* Maven
* cf cli v7
* TAS 2.10 (but should work with earlier versions)

```shell
cd ~/course-service
mvn clean package -DskipTests
mvn spring-boot:run
```

###Test app locally

Launch http://localhost:8080/courses

###Deploy app to TAS

```shell
cf push course-service -p ./target/course-service-0.0.1-SNAPSHOT.jar

# or
cf push course-service -p ./target/course-service-0.0.1-SNAPSHOT.jar -b java_buildpack_offline

# View app details
cf app course-service
```