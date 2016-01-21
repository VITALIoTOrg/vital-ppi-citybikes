# vital-istanbul-ppi

* Author: Lorenzo Bracco, Fabrizio De Ceglia, Andrea Martelli
* Summary: This is the VITAL HiReply PPI for Istanbul traffic data
* Target Project: VITAL (<http://vital-iot.eu>)
* Source: <http://31.210.156.219/devtry/hireplyppi.git>

## System requirements

For this project you need:

* Java 8.0 (Java SDK 1.8) (<http://openjdk.java.net> or <https://www.oracle.com/java/index.html>)
* Maven (<https://maven.apache.org>)
* WildFly (10.X.X or later recommended) (<http://www.wildfly.org>)

Follow installation instructions of Java, Maven and WildFly.

## Configure and Start WildFly

1. Open file **_WILDFLY_HOME/standalone/configuration/standalone.xml_** and perform the following changes:
  1. In section **_management->security-realms_** add the following text (change the attributes values with those for your keystore):

        ```xml
        <security-realm name="UndertowRealm">
            <server-identities>
                <ssl>
                    <keystore path="my.jks" relative-to="jboss.server.config.dir" keystore-password="password" alias="mycert" key-password="password"/>
                </ssl>
            </server-identities>
        </security-realm>
        ```

  2. Under **_profile->subsystem (the undertow one)->server_** make sure to have:

        ```xml
        <http-listener name="default" redirect-socket="https" socket-binding="http"/>
        <https-listener name="https" security-realm="UndertowRealm" socket-binding="https"/>
        ```

  3. HTTPS should now be enabled.
2. Open a command line and navigate to the root of the WildFly server directory.
3. The following shows the command line to start the server:

        For Linux:   WILDFLY_HOME/bin/standalone.sh
        For Windows: WILDFLY_HOME\bin\standalone.bat

## Configure, Build and Deploy the HiReply PPI

1. Checkout the code from the repository:

        git clone http://31.210.156.219/devtry/hireplyppi.git

2. Open file **_src/main/resources/config.properties_** and set the values defined there to match you deployment scenario. For example:

      ```
      # PPI Server configuration (used to construct resources ids)
      SYMBOLIC_URI=vitalserver.com/hireplyppi
      TRANSF_PROTOCOL=http://

      # HiReply service configuration (the IoT system external to VITAL)
      HI_HOSTNAME=istanbul-iot.com
      HI_PORT=80
      HI_CONTEXT=/hireply
      HI_GETSNAPSHOT_PATH=/ServiceRegistry/getsnapshot
      HI_GETPROPERTYNAMES_PATH=/getpropertynames
      HI_GETPROPERTYVALUE_PATH=/getpropertyvalue
      HI_SETPROPERTYVALUE_PATH=/setpropertyvalue
      HI_GETPROPERTYATTRIBUTE_PATH=/getpropertyattribute
      HI_GETPROPERTYHISTORICALVALUES_PATH=/getpropertyhistoricalvalues
      HI_ISSERVICERUNNING_PATH=/isservicerunning
      HI_LOGS_VERBOSITITY_SETTING=LogsPriorityLevel

      # Ontology configuration
      ONT_BASE_URI_PROPERTY=vital-iot.eu/ontology/ns/
      CONTEXTS_LOC=http://vital-iot.eu/contexts/
      SPEED_PROP=Speed
      COLOR_PROP=Color
      REVERSE_SPEED_PROP=ReverseSpeed
      REVERSE_COLOR_PROP=ReverseColor

      # Configuration options name
      LOG_VERBOSITY=logVerbosity
      ```

3. Open a command line and navigate to the root directory of the project.
4. Type this command to build the application and create a WAR package:

        mvn package

5. Make sure you have started the JBoss Server as described above.
6. In order to deploy copy the package **_target/wildppi.war_** to the **_standalone/deployments_** directory of the running instance of the server.

## Access the module

The HiReply PPI is available at the hostname and port of your WildFly instance at path **_/hireplyppi_**.

## Undeploy the HiReply PPI

1. Stop the WildFly server (by killing the script used to start it).
2. Remove the application related files from the **_standalone/deployments_** directory of WildFly.
3. Restart WildFly.

Or use any other method offered by WildFly, such as the **_jboss-cli_** interface.

