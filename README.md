# VITAL Istanbul Traffic PPI

---

This project contains the code for the VITAL Istanbul Traffic PPI. The module
exposes the underlying HiReply IoT system through a REST interface compliant to
D3.2.2 of the VITAL Project.

## Configuration

In order to change the configuration you need to edit file
"src/main/resources/config.properties" and change the values of the properties.

## Build and run

In order to build the application you will need the following tool to be
installed on your machine:

* **Maven** (https://maven.apache.org/)

Then you can build issuing the following command:

```
mvn compile assembly:single
```

You will then find a ".jar" file in the "target" folder; you can deploy it by
copying it onto your target machine and executing:

```
java -jar <package>.jar # Java 8 should be used
```

You may also want to take a look at the VITAL Deployer project, featuring a
script to automatically build and deploy this application and other components
of the VITAL system.

