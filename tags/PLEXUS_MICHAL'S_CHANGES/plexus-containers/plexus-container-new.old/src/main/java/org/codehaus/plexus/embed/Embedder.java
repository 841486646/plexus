package org.codehaus.plexus.embed;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * <tt>Embedder</tt> enables a client to embed Plexus into their
 * application with a minimal amount of work.  The basic usage is
 * as follows:
 * <br/>
 * <pre>
 *     Embedder embedder = new Embedder();
 *     embedder.setConfiguration("/plexus.xml");
 *     embedder.addContextValue("plexus.home", ".");
 *     embedder.start();
 *
 *     PlexusContainer container = embedder.getContainer();
 *     [do stuff with container]
 *
 *     embedder.stop();
 * </pre>
 * <br/>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="pete-codehaus-dev@kazmier.com">Pete Kazmier</a>
 * @version $Id$
 */
public class Embedder
{
    private String configuration;

    private volatile URL configurationURL;

    private final PlexusContainer container;

    private volatile boolean embedderStarted = false;

    private volatile boolean embedderStopped = false;

    public Embedder()
    {
        container = new DefaultPlexusContainer();
    }

    public PlexusContainer getContainer()
    {
        if ( !embedderStarted )
        {
            throw new IllegalStateException( "Embedder must be started" );
        }

        return container;
    }

    public Object lookup( String role )
        throws ComponentLookupException
    {
        return getContainer().lookup( role );
    }

    public Object lookup( String role, String id )
        throws ComponentLookupException
    {
        return getContainer().lookup( role, id );
    }

    public boolean hasService( String role )
    {
        return getContainer().hasComponent( role );
    }

    public boolean hasService( String role, String id )
    {
        return getContainer().hasComponent( role, id );
    }

    public void release( Object service )
    {
        getContainer().release( service );
    }

    public void setConfiguration( String configuration )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "Embedder has already been started" );
        }

        this.configuration = configuration;
    }

    public void setConfiguration( URL configuration )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "Embedder has already been started" );
        }

        this.configurationURL = configuration;
    }

    public void addContextValue( Object key, Object value )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException(
                "Embedder has already been started" );
        }

        container.addContextValue( key, value );
    }

    public synchronized void start()
        throws Exception
    {
        if ( embedderStarted )
        {
            throw new IllegalStateException( "Embedder already started" );
        }

        if ( embedderStopped )
        {
            throw new IllegalStateException( "Embedder cannot be restarted" );
        }

        container.setConfigurationResource( new InputStreamReader( findConfigurationInputStream() ) );

        container.initialize();

        embedderStarted = true;

        container.start();
    }

    public synchronized void stop()
        throws Exception
    {
        if ( embedderStopped )
        {
            throw new IllegalStateException( "Embedder already stopped" );
        }

        if ( !embedderStarted )
        {
            throw new IllegalStateException( "Embedder not started" );
        }

        container.dispose();

        embedderStarted = false;

        embedderStopped = true;
    }

    /**
     * Tries a variety of methods to find the configuration resource.
     *
     * BRW - I see this as fairly pointless as putting your config into the Embedder.class package
     *       will be annoying. Far better to just force the end user to provide a URL and remove
     *       all this logic.
     *
     * JVZ - What about uberjar applications?
     * BRW - If it doesn't work in uberjar, then we'll have a good test case and I'll fix classworlds.
     *
     * @return the stream containing the configuration
     * @throws RuntimeException when the configuration can not be found / opened
     */
    private InputStream findConfigurationInputStream()
    {
        if ( configurationURL != null )
        {
            try
            {
                return configurationURL.openStream();
            }
            catch ( IOException e )
            {
                throw new IllegalStateException( "The specified configuration resource cannot be found: " + configurationURL.toString() );
            }
        }

        InputStream is = getClass().getResourceAsStream( configuration );

        if ( is == null )
        {
            try
            {
                is = new FileInputStream( configuration );
            }
            catch ( FileNotFoundException e )
            {
                // do nothing.
            }
        }

        if ( is == null )
        {
            throw new IllegalStateException( "The specified configuration resource cannot be found: " + configuration );
        }

        return is;
    }
}
