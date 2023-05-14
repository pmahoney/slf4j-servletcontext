package com.republicate.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class ServletContextLoggerServiceProvider implements SLF4JServiceProvider {
    @Override
    public ILoggerFactory getLoggerFactory() {
        return ServletContextLoggerFactory.INSTANCE;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return ServletContextLoggerFactory.MARKER_FACTORY;
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return MDCStore.getSingleton();
    }

    @Override
    public String getRequestedApiVersion() {
        return "2.0.4";
    }

    @Override
    public void initialize() {
    }
}
