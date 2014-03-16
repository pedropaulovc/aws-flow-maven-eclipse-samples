AWS Flow Framework Samples + Maven + Eclipse
============================================

[![Build Status](https://travis-ci.org/pedropaulovc/aws-flow-maven-eclipse-samples.png?branch=master)](https://travis-ci.org/pedropaulovc/aws-flow-maven-eclipse-samples)

These samples demonstrate how to use AWS Flow Framework. They were taken from the [AWS Java SDK](https://aws.amazon.com/pt/sdkforjava/) and adapted to be built and tested using Maven and Eclipse, instead of Ant.

The following samples are included: 

* **HelloWorld** – this sample includes a very simple workflow that calls an activity to print hello world to the console. It shows the basic usage of AWS Flow Framework, including defining contracts, implementation of activities and workflow coordination logic and worker programs to host them.
* **Booking** – shows an example workflow for making a reservation, including flight and rental car. 
* **FileProcessing** – shows a workflow for media processing use case. The sample workflow downloads a file from an Amazon S3 bucket, creates a zip file and uploads that zip file back to S3. The sample uses the task routing feature.
* **PeriodicWorkflow** – shows how to create a workflow that periodically executes an activity. The workflow can run for extended periods and hence it uses the continue as new execution feature.
* **SplitMerge** – the workflow in this sample processes a large data set by splitting it up into smaller data sets. The sample calculates the average of a large set of numbers stored in a file in S3. The smaller data sets are assigned to workers and the results of processing are merged to produce the final result.
* **Deployment** – the workflow in this sample shows deployment of interdependent components.
* **Cron** – the workflow in this sample starts an activity periodically based on a cron schedule.
* **CronWithRetry** – this is an enhanced version of the Cron sample that uses the exponential retry feature to retry the activity if it fails.

Prerequisites
-------------
* You must have a valid Amazon Web Services developer account.
* You must be signed up for the following services:
    * Amazon Simple Workflow Service (SWF). For more information, see [aws.amazon.com/swf](http://aws.amazon.com/swf).
    * Amazon Simple Storage Service (S3). For more information, see [aws.amazon.com/s3](http://aws.amazon.com/s3).


Setup
-----

Once you've unpacked the project into a directory, you need to:

1. Install the AWS Flow Build Tools (review the instructions inside the `lib/README.md` directory)
2. Edit the `access.properties` file inside `src/main/resources`, with your AWS credentials and configurations.


Stub File Generation
--------------------
The Flow Framework generates source code based on your source code via annotation processing. Create the stub files, by running maven:

```
mvn clean process-classes
```


Eclipse Setup
---------

http://download.eclipse.org/tools/ajdt/43/update

1. Open Up Eclipse
2. Pick Help > Install New Software and enter the URL ```http://download.eclipse.org/tools/ajdt/43/update``` for Eclipse 4.3 (Kepler). Install at least:
    * AspectJ Development Tools (Required)
    * Equinox Weaving SDK
    * Eclipse Weaving Service Source Code
3. `m2eclipse` should be already installed. Restart when complete.
4. Install Maven Integration for AJDT, Help > Install New Software and entering this URL: ```http://dist.springsource.org/release/AJDT/configurator/```. Restart Eclipse.
5. Use File > Import and point to the directory or repository where are the project files.
6. Build with Maven with a goal `clean process-classes` in order to generate necessary stub classes. See "Updating Activity / Workflows" below.

When importing, M2Eclipse will ask if its ok to download additional Project Configurations. Yes, let him do it and add the remaining plugins.


Updating Activity / Workflows
-----------------------------

The Flow Framework generates source code based on your own code via annotation processing. If you update the Workflow Definitions, make sure you either:

1. Bump the version numbers in the Workflow / Activity Interfaces, and
2. Regenerate the stub files for AWS Flow Framework. The command below should be enough:

```
mvn clean process-classes
```

It will generate several files under the `target/generated-sources/apt` directory. If Eclipse still reports missing `*Client`, `*ClientImpl`, `*ClientExternal`, `*ClientExternalFactory`, `*ClientExternalFactoryImpl` classes/interfaces, refreshing the project (`F5`) or Project > Clean... should fix the problem.


Weaving classes
---------------

The Flow Framework uses Aspect Oriented Programming and it's necessary to weave ("compile") the used aspects (eg. `@Asynchronous` and `@ExponentialRetry`) into the binaries.

### Inside Eclipse
The AJDT plugin weaves automatically the aspects for us and so building a project with a `install` or `test` goal should be enough. If it's not working, you may try building just like if you were outside Eclipse:

### Outside Eclipse
To weave the `src/main/java` classes run:
```
mvn clean compile
```

To weave the `src/test/java` classes run:
```
mvn clean test-compile
```

Running the Samples
-------------------

The steps for running the AWS Flow Framework samples are:

1. Create the Samples domain
    1. Go to the [SWF Management Console](https://console.aws.amazon.com/swf/home).
    2. Follow the on-screen instructions to log in.
    3. Click Manage Domains and register a new domain with the name Samples.
2. Open the access.properties in the `src/main/resources` folder.
3. Locate the following sections and fill in your Access Key ID and Secret Access Key. You can use the same values for SWF and S3:
```
# Fill in your AWS Access Key ID and Secret Access Key for SWF # http://aws.amazon.com/security-credentials
AWS.Access.ID=<Your AWS Access Key>
AWS.Secret.Key=<Your AWS Secret Key>
AWS.Account.ID=<Your AWS Account ID>

# Fill in your AWS Access Key ID and Secret Access Key for S3
# http://aws.amazon.com/security-credentials
S3.Access.ID=<Your AWS Access Key>
S3.Secret.Key=<Your AWS Secret Key>
S3.Account.ID=<Your AWS Account ID>
```

4. Some samples upload files to S3. Locatet he following section and fill in the name of S3 bucket that you want the samples to use:

```
####### FileProcessing Sample Config Values ##########
 Workflow.Input.TargetBucketName=<Your S3 bucket name>
```

5. Save the file.

6. Compile the samples by running Maven with the `compile` goal.

7. To run the samples follow these instructions:

**Note**: For Windows PowerShell, replace
```
mvn exec:java -Dexec.mainClass="???"
```
with
```
mvn exec:java "-Dexec.mainClass=???"
```

### Hello World Sample:
The sample has three executables. You should run each in a separate terminal/console or via Run > Run.

```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.helloworld.ActivityHost"
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.helloworld.WorkflowHost" 
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.helloworld.WorkflowExecutionStarter"
```

### Booking Sample:
 The sample has three executables. You should run each in a separate terminal/console or via Run > Run.
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.booking.ActivityHost" 
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.booking.WorkflowHost" 
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.booking.WorkflowExecutionStarter"
```

### Split Merge Sample:
 The sample has three executables. You should run each in a separate terminal/console or via Run > Run.
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.splitmerge.ActivityHost"
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.splitmerge.WorkflowHost" 
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.splitmerge.WorkflowExecutionStarter"
```

### Periodic Workflow Sample:
 The sample has three executables. You should run each in a separate terminal/console or via Run > Run.
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.periodicworkflow.ActivityHost"
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.periodicworkflow.WorkflowHost" 
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.periodicworkflow.WorkflowExecutionStarter"
```

### File Processing Sample:
 The sample has three executables. You should run each in a separate terminal/console or via Run > Run.
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.fileprocessing.ActivityHost"
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.fileprocessing.WorkflowHost" 
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.fileprocessing.WorkflowExecutionStarter"
```

### Cron Sample:
 The sample has three executables. You should run each in a separate terminal/console or via Run > Run.
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.cron.ActivityHost"
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.cron.WorkflowHost" 
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.cron.CronWorkflowExecutionStarter" -Dmain-args="\"*/10 * * * * *\" PST 60"
```
 The workflow starter takes 3 command line arguments that must be specified:

1. **CRON_PATTERN**: specifies the pattern used to determine the cron schedule for the periodic activity task. The above command specifies the pattern `*/10 * * * * *` to run the task every 10 seconds.
2. **TIME_ZONE**: specifies the time zone to use for time calculations. The above command specifies PST (Pacific Standard Time).
3. **CONTINUE_AS_NEW_AFTER_SECONDS**: specifies the duration, in seconds, after which the current execution should be closed and continued as a new execution. The above command specifies 60 seconds.

### Cron With Retry Sample:
 The sample has three executables. You should run each in a separate terminal/console or via Run > Run.
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.ActivityHost"
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.WorkflowHost" 
```
```
mvn exec:java -Dexec.mainClass="com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflowExecutionStarter" -Dmain-args="\"*/10 * * * * *\" PST 60"
```
The workflow starter takes 3 command line arguments that must be specified:

1. **CRON_PATTERN**: specifies the pattern used to determine the cron schedule for the periodic activity task. The above command specifies the pattern `*/10 * * * * *` to run the task every 10 seconds.
2. **TIME_ZONE**: specifies the time zone to use for time calculations. The above command specifies PST (Pacific Standard Time).
3. **CONTINUE_AS_NEW_AFTER_SECONDS**: specifies the duration, in seconds, after which the current execution should be closed and continued as a new execution. The above command specifies 60 seconds.

References
----------
Converting these Flow Framework samples to Maven and Eclipse was no easy task and was helped by other people that kindly shared their experiences using the Flow Framework:

* [AWS Simple Workflow Service (SWF) – Part 2, Sample Use Case](http://www.newvem.com/aws-simple-workflow-service-swf-part-2-sample-use-case/) and [Newvem SWF Example](https://bitbucket.org/ingenieux/newvem-swf-sample), this project's `pom.xml` was based on the Newvem SWF Example.
* [How to consume Amazon SWF](http://stackoverflow.com/questions/9392655/how-to-consume-amazon-swf)
* [A dozen things to know about AWS Simple Workflow in Eclipse and Maven ](http://blog.cyclopsgroup.org/2012/12/a-dozen-things-to-know-about-aws-simple.html)
* [Using the AWS flow framework in a Maven project](http://nithint.wordpress.com/2013/09/18/using-the-aws-flow-framework-in-a-maven-project/)

