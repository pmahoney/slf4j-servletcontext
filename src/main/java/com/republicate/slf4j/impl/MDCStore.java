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

import org.slf4j.spi.MDCAdapter;

/**
 * WebAppLogger MDC implementation.
 * 
 * @author Claude Brisson
 */

public class MDCStore implements MDCAdapter
{

    private MDCStore()
    {
    }

    private static final MDCStore singleton = new MDCStore();

    public static MDCStore getSingleton()
    {
        return singleton;
    }

    /**
     * Put a context value (the <code>val</code> parameter) as identified with
     * the <code>key</code> parameter into the current thread's context map. 
     * The <code>key</code> parameter cannot be null. The code>val</code> can be null.
     * 
     * <p>If the current thread does not have a context map it is created as a side
     * effect of this call.
     */
    public void put(String key, String val)
    {
        Map<String, String> map = store.get();
        if (map == null)
        {
            map = new HashMap<String, String>();
            store.set(map);
        }
        map.put(key, val);
    }

    /**
     * Get the context identified by the <code>key</code> parameter.
     * The <code>key</code> parameter cannot be null.
     * 
     * @return the string value identified by the <code>key</code> parameter.
     */
    public String get(String key)
    {
        Map<String, String> map = store.get();
        return map == null ? null : map.get(key);
    }

    /**
     * Remove the the context identified by the <code>key</code> parameter. 
     * The <code>key</code> parameter cannot be null. 
     * 
     * <p>
     * This method does nothing if there is no previous value 
     * associated with <code>key</code>.
     */
    public void remove(String key)
    {
        Map<String, String> map = store.get();
        if (map != null)
        {
            map.remove(key);
        }
    }

    /**
     * Clear all entries in the MDC.
     */
    public void clear()
    {
        Map<String, String> map = store.get();
        if (map != null)
        {
            map.clear();
        }        
    }

    /**
     * Return a copy of the current thread's context map, with keys and 
     * values of type String. Returned value may be null.
     * 
     * @return A copy of the current thread's context map. May be null.
     */
    public Map<String, String> getCopyOfContextMap()
    {
        Map<String, String> map = store.get();
        return map == null ? null : new HashMap<String, String>(map);
    }

    /**
     * Set the current thread's context map by first clearing any existing 
     * map and then copying the map passed as parameter. The context map 
     * parameter must only contain keys and values of type String.
     * 
     * @param contextMap must contain only keys and values of type String
     */
    public void setContextMap(Map<String, String> contextMap)
    {
        store.set(new HashMap(contextMap));
    }

    private ThreadLocal<Map<String, String>> store = new ThreadLocal<Map<String, String>>();

}
