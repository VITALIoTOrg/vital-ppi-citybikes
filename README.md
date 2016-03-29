# vital-ppi-citybikes

* Author: Lorenzo Bracco
* Summary: This is the VITAL PPI for the CityBikes bike sharing API
* Target Project: VITAL (<http://vital-iot.eu>)
* Source: <http://gitlab.atosresearch.eu/vital-iot/vital-ppi-citybikes.git>

## System requirements

For this project you need:

* Git (<https://git-scm.com>)
* Java 8.0 (Java SDK 1.8) (<http://openjdk.java.net> or <https://www.oracle.com/java/index.html>)
* Maven (<https://maven.apache.org>)
* WildFly (10.X.X or later recommended) (<http://www.wildfly.org>)

Follow installation instructions of Git, Java, Maven and WildFly.

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

## Configure, Build and Deploy the CityBikes PPI

1. Checkout the code from the repository:

        git clone http://gitlab.atosresearch.eu/vital-iot/vital-ppi-citybikes.git

2. Open a command line and navigate to the root directory of the project.
3. Type this command to build the application and create a WAR package:

        mvn package

4. Make sure you have started the JBoss Server as described above.
5. In order to deploy copy the package **_target/vital-ppi-citybikes.war_** to the **_standalone/deployments_** directory of the running instance of the server.

## Access the module

The CityBikes PPI is available at the hostname and port of your WildFly instance at path **_/vital-ppi-citybikes_**. Each sub-PPI (corresponding to a bike sharing network) is accessible at path **_/vital-ppi-citybikes/\<network-id\>_**.

## Undeploy the CityBikes PPI

1. Stop the WildFly server (by killing the script used to start it).
2. Remove the package related files from the **_standalone/deployments_** directory of WildFly.
3. Restart WildFly.

Or use any other method offered by WildFly, such as the **_jboss-cli_** interface.

