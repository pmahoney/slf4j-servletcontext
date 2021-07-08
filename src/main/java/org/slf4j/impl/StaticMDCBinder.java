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

package org.slf4j.impl;

import org.slf4j.spi.MDCAdapter;
import com.republicate.slf4j.impl.MDCStore;

/**
 * This implementation is bound to {@link MDCStore}.
 *
 * @author Claude Brisson
 */

public class StaticMDCBinder
{

    /**
     * The unique instance of this class.
     */
    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    private StaticMDCBinder()
    {
    }

    /**
     * Currently this method always returns an instance of 
     * {@link StaticMDCBinder}.
     * @return static MDCAdapter
     */
    public MDCAdapter getMDCA()
    {
        return MDCStore.getSingleton();
    }

    public String getMDCAdapterClassStr()
    {
        return MDCStore.class.getName();
    }
}
