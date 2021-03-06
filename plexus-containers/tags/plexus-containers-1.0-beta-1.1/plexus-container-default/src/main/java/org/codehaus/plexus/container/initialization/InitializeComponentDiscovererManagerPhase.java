package org.codehaus.plexus.container.initialization;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;

/**
 * @author Jason van Zyl
 */
public class InitializeComponentDiscovererManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        ComponentDiscovererManager componentDiscovererManager = context.getContainerConfiguration().getComponentDiscovererManager();

        componentDiscovererManager.initialize();

        context.getContainer().setComponentDiscovererManager( componentDiscovererManager );

        for ( ComponentDiscoveryListener listener : componentDiscovererManager.getComponentDiscoveryListeners().keySet() )
        {
            try
            {
                // This is a hack until we have completely live components

                context.getContainer().addComponent( listener, listener.getClass().getName() );

                context.getContainer().getComponentDiscovererManager().removeComponentDiscoveryListener( listener );

                ComponentDiscoveryListener cdl = (ComponentDiscoveryListener) context.getContainer().lookup(
                    listener.getClass().getName() );

                context.getContainer().getComponentDiscovererManager().registerComponentDiscoveryListener( cdl );
            }
            catch ( Exception e )
            {
                throw new ContainerInitializationException( "Error initializing component discovery listener.", e );
            }
        }
    }
}
