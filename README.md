# VITAL Istanbul Traffic PPI

---

This project contains the code for the VITAL Istanbul Traffic PPI. The module
exposes the underlying HiReply IoT system through a REST interface compliant to
D3.2.2 of the VITAL Project.

## Configuration

In order to change the configuration you need to edit file
"src/main/resources/config.properties" and change the values of the properties.

To change the path where WildFly will deploy the application you have to change
the value of "context-root" in file "src/main/webapp/WEB-INF/jboss-web.xml".

## Build and run

In order to build the application you will need the following tool to be
installed on your machine:

* **Maven** (https://maven.apache.org/)

Then you can build issuing the following command:

```
mvn package
```

You will then find a ".war" file in the "target" folder; you can use it to
deploy the application on WildFly (tested on WildFly 10.0.0.CR5 with OpenJDK
8).

You may also want to take a look at the VITAL Deployer project, featuring a
script to automatically build and deploy this application and other components
of the VITAL system.

