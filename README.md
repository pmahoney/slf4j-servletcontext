<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

webapp-slf4j-logger
===================

This is a simple logger with minimal configuration.  It is an
[SLF4J](http://www.slf4j.org/) backend that forwards logs to a
`ServletContext` object.

All log messages are logged using
[ServletContext#log](http://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html#log%28java.lang.String,%20java.lang.Throwable%29).

Features:

 * zero-config for default functional behaviour
 * Custom format (with a functional %ip tag to log HTTP request IP, included in the default format)
 * Mapped Diagnostic Contexts (MDC)
 * Supports serialization and deserialization

# Configuration

## Inclusion in a 3.0 webapp

If your J2EE container is complient with the 3.0 servlet API, then you just have to include webapp-slf4j-logger.jar in your WEB-INF/lib directory.

## Log level

The logging level can be set with a context parameter.  Possible
values are (case insensitive) `trace`, `debug`, `info`, `warn`,
`error`, following the standard slf4j levels.

    <context-param>
      <param-name>webapp-slf4j-logger.level</param-name>
      <param-value>debug</param-value>
    </context-param>

The default enabled level is INFO.

## Format

The format can be specified with a context parameter, as a sequence of placeholders and literal text.

    <context-param>
      <param-name>webapp-slf4j-logger.format</param-name>
      <param-value>%logger [%level] [%ip] %message</param-value>
    </context-param>

Placeholders begin with '%' and must only contain alpha-numeric characters.

Predefined placeholders:

* %date - the timestamp, formatted as "YYYY-MM-DD HH:mm:ss,sss".
* %level, %Level, %LEVEL - the level in lowercase, standard case or uppercase (and left-padded to five characters).
* %logger - the name of the logger (for class names, the package is not shown).
* %ip - the IP address of the current request
* %message - the actual log message string

Custom placeholders must correspond to existing MDC tags. For instance, to see IPs of each log line's request,
you can use the provided com.republicate.slf4j.helpers.IPFilter, and set up a format with the %ip placeholder.

The default format is:
    %logger [%level] [%ip] %message
(it doesn't include %date, as the date will usually be added by the J2EE container, nor does it add a terminal \n, as the container will take care of it).

## Email notifications

The logger can be configured to send an email if severity is beyond a certain level (typically, warning or error). The configuration parameter is of the form:
    *level*:*protocol*:*mail_server*:*port*:*from_address*:*to_address*
The only protocol supported for now is smtp.

Example:

    <context-param>
      <param-name>webapp-slf4j-logger.notification</param-name>
      <param-value>warn:smtp:mail.server:25:from@address:to@address</param-value>
    </context-param>

## Inclusion in a maven-based project

Declare a dependency on `webapp-slf4j-logger`:

    <dependency>
      <groupId>com.republicate</groupId>
      <artifactId>webapp-slf4j-logger</artifactId>
      <version>1.0.0</version>
    </dependency>

## Inclusion in a non-3.0 webapp

If your J2EE container is not complient with servlet API 3.0, you have to add to `web.xml`:

    <listener>
      <listener-class>com.republicate.slf4j.impl.ServletContextLoggerSCL</listener-class>
    </listener>

And if you want to enable the %ip format tag, you'll also have to add the following filter:

    <filter>
      <filter-name>webapp-slf4j-logger-ip-tag-filter</filter-name>
      <filter-class>com.republicate.slf4j.impl.IPTagFilter</filter-class>
    </filter>

with its mapping:

    <filter-mapping>
      <filter-name>webapp-slf4j-logger-ip-tag-filter</filter-name>
      <url-pattern>/*</url-pattern>
      <dispatcher>REQUEST</dispatcher>
      <dispatcher>FORWARD</dispatcher>
    </filter-mapping>

## FAQ

### All other SLF4J jars begin with "slf4j". Why isn't this library called slf4j-webapp-logger?

Some containers, Tomcat at least, will not search for servlet-3.0 annotations in a certain number of jars, among which all slf4j-* jars...
