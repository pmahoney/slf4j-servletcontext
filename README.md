slf4j-servletcontext
====================

This is a simple logger with minimal configuration.  It is an
[SLF4J](http://www.slf4j.org/) backend that forwards logs to a
`ServletContext` object.

To initialize `slf4j-servletcontext`, add to `web.xml`:

    <listener>
      <listener-class>com.commongroundpublishing.slf4j.impl.ServletContextLoggerSCL</listener-class>
    </listener>

All log messages will now be logged using
[ServletContext#log](http://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html#log%28java.lang.String,%20java.lang.Throwable%29).

The logging level can be set with a context parameter.  Possible
values are (case insensitive) `trace`, `debug`, `info`, `warn`,
`error`, following the standard slf4j levels.

    <context-param>
      <param-name>ServletContextLogger.LEVEL</param-name>
      <param-value>debug</param-value>
    </context-param>
