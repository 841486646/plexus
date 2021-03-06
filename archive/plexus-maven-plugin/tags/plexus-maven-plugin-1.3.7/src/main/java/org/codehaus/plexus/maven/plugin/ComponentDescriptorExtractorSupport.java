/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.plexus.maven.plugin;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.component.repository.cdc.ComponentDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support for {@link ComponentDescriptorExtractor} implementations.
 *
 * @version $Id$
 */
public abstract class ComponentDescriptorExtractorSupport
    implements ComponentDescriptorExtractor
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected Map getDefaultsByRole(final ComponentDescriptor[] roleDefaults) {
        Map defaultsByRole = new HashMap();

        if (roleDefaults != null) {
            for (int i = 0; i < roleDefaults.length; i++) {
                String role = roleDefaults[i].getRole();

                if (role == null) {
                    throw new IllegalArgumentException("Invalid role defaults; found null role in: " + roleDefaults[i]);
                }

                defaultsByRole.put(role, roleDefaults[i]);
            }
        }

        return defaultsByRole;
    }

    protected void applyDefaults(final ComponentDescriptor descriptor, final Map defaultsByRole) {
        assert descriptor != null;
        assert defaultsByRole != null;

        if (defaultsByRole.containsKey(descriptor.getRole())) {
            ComponentDescriptor defaults = (ComponentDescriptor) defaultsByRole.get(descriptor.getRole());

            if (descriptor.getInstantiationStrategy() == null) {
                descriptor.setInstantiationStrategy(defaults.getInstantiationStrategy());
            }
        }
    }
}