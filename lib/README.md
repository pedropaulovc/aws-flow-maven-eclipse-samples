How to Install the AWS Flow Framework
=====================================

Short Version:

```shell
$ mvn install:install-file -Dfile=aws-java-sdk-flow-build-tools-1.3.22.jar -DpomFile=aws-java-sdk-flow-build-tools-1.3.22.pom
```

Long Version:

 * Download AWS Java SDK. Unpack it on a dir.
 * Review the AspectJ version, by looking META-INF/MANIFEST.MF under third-party/aspectj-1.6/aspectjrt.jar

```
C:\projetos\share\aws-java-sdk-1.3.22\third-party\aspectj-1.6>unzip -p aspectjrt.jar META-INF/MANIFEST.MF
Manifest-Version: 1.0

Name: org/aspectj/lang/
Specification-Title: AspectJ Runtime Classes
Specification-Version: 1.6
Specification-Vendor: aspectj.org
Implementation-Title: org.aspectj.tools
Implementation-Version: 1.6.12
Implementation-Vendor: aspectj.org
Bundle-Name: AspectJ Runtime
Bundle-Version: 1.6.12
Bundle-Copyright: (C) Copyright 1999-2001 Xerox Corporation, 2002 Palo
  Alto Research Center, Incorporated (PARC), 2003-2009 Contributors.
  All Rights Reserved.
```

In the case above, aspectj version is 1.6.12.

 * Confirm the freemarker version

 * Review aws-java-sdk-flow-build-tools-1.3.22.pom. In particular, confirm the versions in the pom, as well as aspectjrt and freemarker.

5. Install it using the command in "Short Version".

