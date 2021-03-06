package org.codehaus.plexus.ehcache;

/*
 * Copyright 2001-2006 The Codehaus.
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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.StringUtils;

/**
 * AbstractEhcacheComponent
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractEhcacheComponent
    extends AbstractLogEnabled
    implements EhcacheComponent, Initializable, Disposable
{
    /**
     * @plexus.configuration default-value="false"
     */
    private boolean eternal;

    /**
     * @plexus.configuration default-value="1000"
     */
    private int maxElementsInMemory;

    /**
     * @plexus.configuration default-value="LRU"
     */
    private String memoryEvictionPolicy;

    /**
     * @plexus.configuration default-value="cache"
     */
    private String name;

    /**
     * @plexus.configuration default-value="600"
     */
    private int timeToIdleSeconds;

    /**
     * @plexus.configuration default-value="300"
     */
    private int timeToLiveSeconds;

    protected Cache cache;

    protected CacheManager cacheManager;

    public void initialize()
        throws InitializationException
    {
        cacheManager = CacheManager.getInstance();
    }

    public Cache getCache()
    {
        return cache;
    }

    public Element getElement( Object key )
    {
        return cache.get( key );
    }

    public int getMaxElementsInMemory()
    {
        return maxElementsInMemory;
    }

    public String getMemoryEvictionPolicy()
    {
        return memoryEvictionPolicy;
    }

    public MemoryStoreEvictionPolicy getMemoryStoreEvictionPolicy()
    {
        return MemoryStoreEvictionPolicy.fromString( memoryEvictionPolicy );
    }

    public String getName()
    {
        return name;
    }

    public int getTimeToIdleSeconds()
    {
        return timeToIdleSeconds;
    }

    public int getTimeToLiveSeconds()
    {
        return timeToLiveSeconds;
    }

    public boolean hasKey( Object key )
    {
        return cache.get( key ) != null;
    }

    public void invalidateKey( Object key )
    {
        if ( key == null )
        {
            return;
        }

        if ( key instanceof String && StringUtils.isEmpty( (String) key ) )
        {
            return;
        }

        Element el = cache.get( key );
        if ( el != null )
        {
            cache.remove( key );
        }
    }

    public boolean isEternal()
    {
        return eternal;
    }

    public void putElement( Element element )
    {
        cache.put( element );
    }

    public void setEternal( boolean eternal )
    {
        this.eternal = eternal;
    }

    public void setMaxElementsInMemory( int maxElementsInMemory )
    {
        this.maxElementsInMemory = maxElementsInMemory;
    }

    public void setMemoryEvictionPolicy( String memoryEvictionPolicy )
    {
        this.memoryEvictionPolicy = memoryEvictionPolicy;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setTimeToIdleSeconds( int timeToIdleSeconds )
    {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public void setTimeToLiveSeconds( int timeToLiveSeconds )
    {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public void dispose()
    {
        if ( cacheManager.getStatus().equals( Status.STATUS_ALIVE ) )
        {
            getLogger().info( "Disposing cache: " + cache );
            if ( this.cache != null )
            {
                cacheManager.removeCache( this.cache.getName() );
                cache = null;
            }
        }
        else
        {
            getLogger().debug( "Not disposing cache, because cacheManager is not alive: " + cache );
        }
    }
}
