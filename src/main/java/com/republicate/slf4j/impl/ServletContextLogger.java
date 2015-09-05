/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.republicate.slf4j.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * ServletContextLogger implementation.
 * 
 * @author Patrick Mahoney
 * @author Claude Brisson
 */

public final class ServletContextLogger extends MarkerIgnoringBase
{
    
    private static final long serialVersionUID = 2L;
    
    public static final int LOG_LEVEL_TRACE = LocationAwareLogger.TRACE_INT;
    public static final int LOG_LEVEL_DEBUG = LocationAwareLogger.DEBUG_INT;
    public static final int LOG_LEVEL_INFO = LocationAwareLogger.INFO_INT;
    public static final int LOG_LEVEL_WARN = LocationAwareLogger.WARN_INT;
    public static final int LOG_LEVEL_ERROR = LocationAwareLogger.ERROR_INT;
    
    public enum Level
    {
        TRACE(LocationAwareLogger.TRACE_INT),
        DEBUG(LocationAwareLogger.DEBUG_INT),
        INFO(LocationAwareLogger.INFO_INT),
        WARN(LocationAwareLogger.WARN_INT),
        ERROR(LocationAwareLogger.ERROR_INT);
        
        private final int level;

        Level(int level) { this.level = level; }
        public int getValue() { return level; }
        public String toLowerCase()
        {
            if (level <= LocationAwareLogger.TRACE_INT)
            {
                return "trace";
            }
            else if (level <= LocationAwareLogger.DEBUG_INT)
            {
                return "debug";
            }
            else if (level <= LocationAwareLogger.INFO_INT)
            {
                return "info.";
            }
            else if (level <= LocationAwareLogger.WARN_INT)
            {
                return "warn.";
            }
            else /* if (level <= LocationAwareLogger.ERROR_INT) */
            {
                return "error";
            }
        }
        public String toStandardCase()
        {
            if (level <= LocationAwareLogger.TRACE_INT)
            {
                return "Trace";
            }
            else if (level <= LocationAwareLogger.DEBUG_INT)
            {
                return "Debug";
            }
            else if (level <= LocationAwareLogger.INFO_INT)
            {
                return "Info.";
            }
            else if (level <= LocationAwareLogger.WARN_INT)
            {
                return "Warn.";
            }
            else /* if (level <= LocationAwareLogger.ERROR_INT) */
            {
                return "Error";
            }
        }
        public String toUpperCase()
        {
            if (level <= LocationAwareLogger.TRACE_INT)
            {
                return "TRACE";
            }
            else if (level <= LocationAwareLogger.DEBUG_INT)
            {
                return "DEBUG";
            }
            else if (level <= LocationAwareLogger.INFO_INT)
            {
                return "INFO.";
            }
            else if (level <= LocationAwareLogger.WARN_INT)
            {
                return "WARN.";
            }
            else /* if (level <= LocationAwareLogger.ERROR_INT) */
            {
                return "ERROR";
            }
        }
    }

    public enum ElemType
    {
        DATE,
        LEVEL_LC,
        LEVEL_SC,
        LEVEL_UC,
        LOGGER,
        MESSAGE,
        CONTEXT, // MDC
        LITERAL
    }

    protected static Pattern splitter = Pattern.compile("(?:%[a-zA-Z0-9]+)|(?:[^%]+)", Pattern.DOTALL);
    protected static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
    protected static MDCStore mdcStore = MDCStore.getSingleton();

    public static class FormatElem
    {
        public ElemType type;
        public String content = null;
    }

    public static class Format
    {
        Format(String str)
        {
            List lst = new ArrayList();
            Matcher matcher = splitter.matcher(str);
            while (matcher.find())
            {
                FormatElem element = new FormatElem();
                lst.add(element);
                String elem = matcher.group();
                if (elem.startsWith("%"))
                {
                    if (elem.equals("%date"))
                    {
                        element.type = ElemType.DATE;
                    }
                    else if (elem.equals("%level"))
                    {
                        element.type = ElemType.LEVEL_LC;
                    }
                    else if (elem.equals("%Level"))
                    {
                        element.type = ElemType.LEVEL_SC;
                    }
                    else if (elem.equals("%LEVEL"))
                    {
                        element.type = ElemType.LEVEL_UC;
                    }
                    else if (elem.equals("%logger"))
                    {
                        element.type = ElemType.LOGGER;
                    }
                    else if (elem.equals("%message"))
                    {
                        element.type = ElemType.MESSAGE;
                    }
                    else
                    {
                        element.type = ElemType.CONTEXT;
                        element.content = elem.substring(1);
                    }
                }
                else
                {
                    element.type = ElemType.LITERAL;
                    element.content = elem;
                }
            }
            elements = (FormatElem[])lst.toArray(new FormatElem[lst.size()]);
        }

        String layout(String logger, Level level, String message)
        {
            StringBuilder builder = new StringBuilder(128);
            for (FormatElem element : elements)
            {
                switch (element.type)
                {
                    case DATE:
                    {
                        builder.append(dateFormat.format(System.currentTimeMillis()));
                        break;
                    }
                    case LEVEL_LC:
                    {
                        builder.append(level.toLowerCase());
                        break;
                    }
                    case LEVEL_SC:
                    {
                        builder.append(level.toStandardCase());
                        break;
                    }
                    case LEVEL_UC:
                    {
                        builder.append(level.toUpperCase());
                        break;
                    }
                    case LOGGER:
                    {
                        builder.append(logger);
                        break;
                    }
                    case MESSAGE:
                    {
                        builder.append(message);
                        break;
                    }
                    case CONTEXT:
                    {
                        String fragment = mdcStore.get(element.content);
                        if (fragment != null)
                        {
                            builder.append(fragment);
                        }
                        break;
                    }
                    case LITERAL:
                    {
                        builder.append(element.content);
                        break;
                    }
                }
            }
            return builder.toString();
        }
        private FormatElem elements[] = null;
    }
    
    // Servlet context
    private static ServletContext context = null;
    
    private static Level enabledLevel = Level.INFO;

    private static String defaultFormat = "%logger [%level] [%ip] %message";
    private static Format format = new Format(defaultFormat);

    /**
     * Set the ServletContext used by all ServletContextLogger objects.  This
     * should be done in a ServletContextListener, e.g. ServletContextLoggerSCL.
     * 
     * @param ctx
     */
    public static void setServletContext(ServletContext ctx)
    {
        context = ctx;
        if (context != null)
        {
            final String defaultLevel = context.getInitParameter("webapp-slf4j-logger.level");
            if (defaultLevel != null)
            {
                enabledLevel = Level.valueOf(defaultLevel.toUpperCase());
            }

            final String givenFormat = context.getInitParameter("webapp-slf4j-logger.format");
            if (givenFormat != null)
            {
                format = new Format(givenFormat);
            }
        }
    }
    
    public static ServletContext getServletContext()
    {
        return context;
    }
    
    private final String name;    
    private final String simpleName;
    
    /**
     * Package access allows only to instantiate
     * ServletContextLogger instances.
     */
    ServletContextLogger(String name)
    {
        this.name = name;
        final int dot;
        if ((dot = name.lastIndexOf(".")) != -1) simpleName = name.substring(dot+1);
        else simpleName = name;
    }
    
    /**
     * Is the given log level currently enabled?
     *
     * @param level is this level enabled?
     */
    protected boolean isLevelEnabled(Level level)
    {
        // log level are numerically ordered so can use simple numeric
        // comparison
        return (context != null && level.getValue() >= enabledLevel.getValue());
    }

    private void log(Level level, String message, Throwable t)
    {
        if (!isLevelEnabled(level)) return;
        final ServletContext context = getServletContext();
        if (context != null)
        {
            String formatted = format.layout(simpleName, level, message);
            if (t == null)
            {
                context.log(formatted);
            }
            else
            {
                context.log(formatted, t);
            }
        }
    }
    
    private void log(Level level, String message)
    {
        log(level, message, null);
    }

    /**
     * For formatted messages, first substitute arguments and then log.
     *
     * @param level
     * @param format
     * @param arg1
     * @param arg2
     */
    private void formatAndLog(Level level, String format, Object arg1, Object arg2)
    {
        if (!isLevelEnabled(level))
        {
            return;
        }
      
        final FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
        log(level, tp.getMessage(), tp.getThrowable());
    }

    /**
     * For formatted messages, first substitute arguments and then log.
     *
     * @param level
     * @param format
     * @param arguments a list of 3 ore more arguments
     */
    private void formatAndLog(Level level, String format, Object... arguments)
    {
        if (!isLevelEnabled(level))
        {
            return;
        }
      
        final FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
        log(level, tp.getMessage(), tp.getThrowable());
    }

    public void debug(String arg0)
    {
        log(Level.DEBUG, arg0);
    }

    public void debug(String arg0, Object arg1)
    {
        formatAndLog(Level.DEBUG, arg0, arg1);
    }

    public void debug(String arg0, Object[] arg1)
    {
        formatAndLog(Level.DEBUG, arg0, arg1);
    }

    public void debug(String arg0, Throwable arg1)
    {
        formatAndLog(Level.DEBUG, arg0, arg1);
    }

    public void debug(String arg0, Object arg1, Object arg2)
    {
        formatAndLog(Level.DEBUG, arg0, arg1, arg2);
    }

    public void error(String arg0)
    {
        log(Level.ERROR, arg0);
    }

    public void error(String arg0, Object arg1)
    {
        formatAndLog(Level.ERROR, arg0, arg1);
    }

    public void error(String arg0, Object[] arg1)
    {
        formatAndLog(Level.ERROR, arg0, arg1);
    }

    public void error(String arg0, Throwable arg1)
    {
        formatAndLog(Level.ERROR, arg0, arg1);
    }

    public void error(String arg0, Object arg1, Object arg2)
    {
        formatAndLog(Level.ERROR, arg0, arg1, arg2);
    }

    public void info(String arg0)
    {
        log(Level.INFO, arg0);
    }
    public void info(String arg0, Object arg1)
    {
        formatAndLog(Level.INFO, arg0, arg1);
    }

    public void info(String arg0, Object[] arg1)
    {
        formatAndLog(Level.INFO, arg0, arg1);
    }

    public void info(String arg0, Throwable arg1)
    {
        formatAndLog(Level.INFO, arg0, arg1);
    }

    public void info(String arg0, Object arg1, Object arg2)
    {
        formatAndLog(Level.INFO, arg0, arg1, arg2);
    }

    public boolean isDebugEnabled()
    {
        return isLevelEnabled(Level.DEBUG);
    }

    public boolean isErrorEnabled()
    {
        return isLevelEnabled(Level.ERROR);
    }

    public boolean isInfoEnabled()
    {
        return isLevelEnabled(Level.INFO);
    }

    public boolean isTraceEnabled()
    {
        return isLevelEnabled(Level.TRACE);
    }

    public boolean isWarnEnabled()
    {
        return isLevelEnabled(Level.WARN);
    }

    public void trace(String arg0)
    {
        log(Level.TRACE, arg0);
    }

    public void trace(String arg0, Object arg1)
    {
        formatAndLog(Level.TRACE, arg0, arg1);
    }

    public void trace(String arg0, Object[] arg1)
    {
        formatAndLog(Level.TRACE, arg0, arg1);
    }

    public void trace(String arg0, Throwable arg1)
    {
        formatAndLog(Level.TRACE, arg0, arg1);
    }

    public void trace(String arg0, Object arg1, Object arg2)
    {
        formatAndLog(Level.TRACE, arg0, arg1, arg2);
    }

    public void warn(String arg0)
    {
        log(Level.WARN, arg0);
    }

    public void warn(String arg0, Object arg1)
    {
        formatAndLog(Level.WARN, arg0, arg1);
    }

    public void warn(String arg0, Object[] arg1)
    {
        formatAndLog(Level.WARN, arg0, arg1);
    }

    public void warn(String arg0, Throwable arg1)
    {
        formatAndLog(Level.WARN, arg0, arg1);
    }

    public void warn(String arg0, Object arg1, Object arg2)
    {
        formatAndLog(Level.WARN, arg0, arg1, arg2);
    }
}
