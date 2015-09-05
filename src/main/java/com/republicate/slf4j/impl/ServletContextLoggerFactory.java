package com.commongroundpublishing.slf4j.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public final class ServletContextLoggerFactory implements ILoggerFactory {
    
    final static ServletContextLoggerFactory INSTANCE =
            new ServletContextLoggerFactory();

    private final Map<String,Logger> map;

    public ServletContextLoggerFactory() {
        map = new HashMap<String,Logger>();
    }

    /**
     * Return an appropriate {@link ServletContextLogger} instance by name.
     */
    public Logger getLogger(String name) {
        Logger logger = null;
        synchronized (this) {
            logger = map.get(name);
            if (logger == null) {
                logger = new ServletContextLogger(name);
                map.put(name, logger);
            }
        }
        return logger;
    }

}
