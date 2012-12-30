package com.commongroundpublishing.slf4j.impl;

import javax.servlet.ServletContext;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

public final class ServletContextLogger extends MarkerIgnoringBase {
    
    private static final long serialVersionUID = 1L;
    
    public static final int LOG_LEVEL_TRACE = LocationAwareLogger.TRACE_INT;
    public static final int LOG_LEVEL_DEBUG = LocationAwareLogger.DEBUG_INT;
    public static final int LOG_LEVEL_INFO = LocationAwareLogger.INFO_INT;
    public static final int LOG_LEVEL_WARN = LocationAwareLogger.WARN_INT;
    public static final int LOG_LEVEL_ERROR = LocationAwareLogger.ERROR_INT;
    
    public enum Level {
        TRACE(LocationAwareLogger.TRACE_INT),
        DEBUG(LocationAwareLogger.DEBUG_INT),
        INFO(LocationAwareLogger.INFO_INT),
        WARN(LocationAwareLogger.WARN_INT),
        ERROR(LocationAwareLogger.ERROR_INT);
        
        private final int level;

        Level(int level) { this.level = level; }
        public int getValue() { return level; }
    }
    
    // private static final Object lock = new Object();
    
    private static ServletContext CONTEXT;
    
    private static Level DEFAULT_LOG_LEVEL = Level.INFO;

    /**
     * Set the ServletContext used by all ServletContextLogger objects.  This
     * should be done in a ServletContextListener, e.g. ServletContextLoggerSCL.
     * 
     * @param context
     */
    public static void setServletContext(ServletContext context) {
        CONTEXT = context;
        final String defaultLevel = CONTEXT.getInitParameter("ServletContextLogger.LEVEL");
        if (defaultLevel != null) {
            DEFAULT_LOG_LEVEL = Level.valueOf(defaultLevel.toUpperCase());
        }
    }
    
    public static ServletContext getServletContext() {
        return CONTEXT;
    }
    
    private final String name;
    
    private final String simpleName;
    
    /**
     * Package access allows only {@link SimpleLoggerFactory} to instantiate
     * SimpleLogger instances.
     */
    ServletContextLogger(String name) {
        this.name = name;
        final int dot;
        if ((dot = name.lastIndexOf(".")) != -1) {
            simpleName = name.substring(dot+1);
        } else {
            simpleName = name;
        }
    }
    
    private Level getCurrentLogLevel() {
        final Object o = getServletContext().
                getAttribute("ServletContextLogger.LEVEL");
        if (o != null && o instanceof Level) {
            return (Level) o;
        } else {
            return DEFAULT_LOG_LEVEL;
        }
    }

    /**
     * Is the given log level currently enabled?
     *
     * @param level is this level enabled?
     */
    protected boolean isLevelEnabled(Level level) {
        // log level are numerically ordered so can use simple numeric
        // comparison
        return (level.getValue() >= getCurrentLogLevel().getValue());
    }

    private void log(Level level, String message, Throwable t) {
        if (!isLevelEnabled(level)) {
            return;
        }
        
        final ServletContext context = getServletContext();
        if (context != null) {
            final StringBuilder builder = new StringBuilder();
            builder
            .append(level)
            .append(":")
            .append(simpleName)
            .append(":")
            .append(message);
            
            if (t == null) {
                context.log(builder.toString());
            } else {
                context.log(builder.toString(), t);
            }
        }
    }
    
    private void log(Level level, String message) {
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
    private void formatAndLog(Level level, String format, Object arg1, Object arg2) {
        if (!isLevelEnabled(level)) {
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
    private void formatAndLog(Level level, String format, Object... arguments) {
        if (!isLevelEnabled(level)) {
            return;
        }
      
        final FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
        log(level, tp.getMessage(), tp.getThrowable());
    }

    public void debug(String arg0) {
        log(Level.DEBUG, arg0);
    }
    public void debug(String arg0, Object arg1) {
        formatAndLog(Level.DEBUG, arg0, arg1);
    }
    public void debug(String arg0, Object[] arg1) {
        formatAndLog(Level.DEBUG, arg0, arg1);
    }
    public void debug(String arg0, Throwable arg1) {
        formatAndLog(Level.DEBUG, arg0, arg1);
    }
    public void debug(String arg0, Object arg1, Object arg2) {
        formatAndLog(Level.DEBUG, arg0, arg1, arg2);
    }

    public void error(String arg0) {
        log(Level.ERROR, arg0);
    }
    public void error(String arg0, Object arg1) {
        formatAndLog(Level.ERROR, arg0, arg1);
    }
    public void error(String arg0, Object[] arg1) {
        formatAndLog(Level.ERROR, arg0, arg1);
    }
    public void error(String arg0, Throwable arg1) {
        formatAndLog(Level.ERROR, arg0, arg1);
    }
    public void error(String arg0, Object arg1, Object arg2) {
        formatAndLog(Level.ERROR, arg0, arg1, arg2);
    }

    public void info(String arg0) {
        log(Level.INFO, arg0);
    }
    public void info(String arg0, Object arg1) {
        formatAndLog(Level.INFO, arg0, arg1);
    }
    public void info(String arg0, Object[] arg1) {
        formatAndLog(Level.INFO, arg0, arg1);
    }
    public void info(String arg0, Throwable arg1) {
        formatAndLog(Level.INFO, arg0, arg1);
    }
    public void info(String arg0, Object arg1, Object arg2) {
        formatAndLog(Level.INFO, arg0, arg1, arg2);
    }

    public boolean isDebugEnabled() {
        return isLevelEnabled(Level.DEBUG);
    }
    public boolean isErrorEnabled() {
        return isLevelEnabled(Level.ERROR);
    }
    public boolean isInfoEnabled() {
        return isLevelEnabled(Level.INFO);
    }
    public boolean isTraceEnabled() {
        return isLevelEnabled(Level.TRACE);
    }
    public boolean isWarnEnabled() {
        return isLevelEnabled(Level.WARN);
    }

    public void trace(String arg0) {
        log(Level.TRACE, arg0);
    }
    public void trace(String arg0, Object arg1) {
        formatAndLog(Level.TRACE, arg0, arg1);
    }
    public void trace(String arg0, Object[] arg1) {
        formatAndLog(Level.TRACE, arg0, arg1);
    }
    public void trace(String arg0, Throwable arg1) {
        formatAndLog(Level.TRACE, arg0, arg1);
    }
    public void trace(String arg0, Object arg1, Object arg2) {
        formatAndLog(Level.TRACE, arg0, arg1, arg2);
    }

    public void warn(String arg0) {
        log(Level.WARN, arg0);
    }
    public void warn(String arg0, Object arg1) {
        formatAndLog(Level.WARN, arg0, arg1);
    }
    public void warn(String arg0, Object[] arg1) {
        formatAndLog(Level.WARN, arg0, arg1);
    }
    public void warn(String arg0, Throwable arg1) {
        formatAndLog(Level.WARN, arg0, arg1);
    }
    public void warn(String arg0, Object arg1, Object arg2) {
        formatAndLog(Level.WARN, arg0, arg1, arg2);
    }

}
