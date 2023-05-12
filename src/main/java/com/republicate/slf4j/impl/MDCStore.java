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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
     * The <code>key</code> parameter cannot be null. The <code>val</code> can be null.
     * 
     * <p>If the current thread does not have a context map it is created as a side
     * effect of this call.
     */
    @Override
    public void put(String key, String val)
    {
        Map<String, String> map = store.get();
        if (map == null)
        {
            map = new HashMap<>();
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public void setContextMap(Map<String, String> contextMap)
    {
        store.set(new HashMap(contextMap));
    }

    /**
     * Push a value into the deque(stack) referenced by 'key'.
     *
     * @param key identifies the appropriate stack
     * @param value the value to push into the stack
     * @since 3.0
     */
    @Override
    public void pushByKey(String key, String value) {
        Map<String, Deque<String>> map = dequesStore.get();
        if (map == null)
        {
            map = new HashMap<>();
            dequesStore.set(map);
        }
        Deque<String> deque = map.computeIfAbsent(key, (k) -> new ArrayDeque<>());
        deque.push(value);
    }

    /**
     * Pop the stack referenced by 'key' and return the value possibly null.
     *
     * @param key identifies the deque(stack)
     * @return the value just popped. May be null/
     * @since 2.0.0
     */
    @Override
    public String popByKey(String key) {
        Map<String, Deque<String>> map = dequesStore.get();
        if (map != null)
        {
            Deque<String> deque = map.get(key);
            if (deque != null) return deque.pollFirst();
        }
        return null;
    }

    /**
     * Returns a copy of the deque(stack) referenced by 'key'. May be null.
     *
     * @param key identifies the  stack
     * @return copy of stack referenced by 'key'. May be null.
     *
     * @since 3.0
     */
    @Override
    public Deque<String> getCopyOfDequeByKey(String key) {
        Map<String, Deque<String>> map = dequesStore.get();
        if (map != null) return map.get(key);
        return null;
    }

    /**
     * Clear the deque(stack) referenced by 'key'.
     *
     * @param key identifies the  stack
     *
     * @since 3.0
     */
    @Override
    public void clearDequeByKey(String key) {
        Map<String, Deque<String>> map = dequesStore.get();
        if (map != null)
        {
            Deque<String> deque = map.get(key);
            if (deque != null) deque.clear();
        }
    }

    private ThreadLocal<Map<String, String>> store = new ThreadLocal<>();
    private ThreadLocal<Map<String, Deque<String>>> dequesStore = new ThreadLocal<>();

}
