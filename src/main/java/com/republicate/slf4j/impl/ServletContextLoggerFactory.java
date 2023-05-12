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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.Logger;
import org.slf4j.helpers.BasicMarkerFactory;

public final class ServletContextLoggerFactory implements ILoggerFactory {
    
    final static ServletContextLoggerFactory INSTANCE =
            new ServletContextLoggerFactory();

    final static IMarkerFactory MARKER_FACTORY = new BasicMarkerFactory();

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
