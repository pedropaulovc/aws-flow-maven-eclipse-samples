Newvem SWF Example
==================

Setup
-----

Once you've unpacked the project into a directory, you need to:

1. Install the AWS Flow Build Tools (review the instructions under README.md under "lib" directory)
2. Create an aws.properties file under src\main\resources, with the following format:

```
accessKey=<your aws access key>
sharedKey=<your aws shared key>
```

Stub File Generation
--------------------

Create the stub files, by running maven:

```mvn clean process-classes```

It will generate several files under the target/generated-classes directory.

IDE Setup
---------

1. Open Up Eclipse
2. Pick Help | Eclipse Marketplace
3. Install m2eclipse and AJDT from the Marketplace. Restart when complete
4. Install Maven Integration for AJDT, Help | Install New Software and entering this URL: ```http://dist.springsource.org/release/AJDT/configurator/```. Restart Eclipse.
5. Use File | Import and point to the directory where you unpacked the project files.

When importing, M2Eclipse will ask if its ok to download additional Project Configurations. Yes, let him do it and add the remaining plugins.

Creating an SWF Domain
----------------------

On the AWS Console, log into the SWF Tab, then create a Domain called swf-example. If needed, you can supply a different domain (but replace it in AppConfig.java class in the sources)

Updating Activity / Workflows
-----------------------------

If you update the Workflow Definitions, make sure you either:
a. Bump the version numbers in the Workflow / Activity Interfaces, and
b. Regenerate the stub files for AWS Flow Framework. The command below should be enough:

```
$ mvn clean process-classes
```

