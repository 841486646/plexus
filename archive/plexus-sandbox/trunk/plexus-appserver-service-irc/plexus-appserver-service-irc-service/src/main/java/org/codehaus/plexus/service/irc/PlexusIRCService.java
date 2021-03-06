package org.codehaus.plexus.service.irc;

import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;
import org.codehaus.plexus.appserver.service.PlexusService;
import org.codehaus.plexus.appserver.service.PlexusServiceException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.IOException;

/**
 * The plexus service wrapper for the IRCServiceManager
 * User: aje
 * Date: 23-Jul-2006
 * Time: 22:42:20
 *
 * @plexus.component role="org.codehaus.plexus.appserver.service.PlexusService"
 * role-hint="irc"
 */
public class PlexusIRCService
    implements PlexusService
{

    /**
     * @plexus.configuration default-value="irc.codehaus.org"
     */
    private String host;

    /**
     * @plexus.configuration default-value="PlexusIRC"
     */
    private String nick;

    /**
     * @plexus.configuration default-value=""
     */
    private String pass;

    /**
     * @plexus.configuration default-value="plexus"
     */
    private String username;

    /**
     * @plexus.configuration default-value="Plexus IRC Framework"
     */
    private String realname;

    /**
     * @plexus.configuration default-value="#test"
     */
    private String channel;

    /**
     * @plexus.requirement
     */
    private org.codehaus.plexus.service.irc.IRCServiceManager manager;

    public void beforeApplicationStart( AppRuntimeProfile applicationRuntimeProfile,
                                        PlexusConfiguration plexusConfiguration )
        throws PlexusServiceException
    {
        try
        {
            manager.connect( host, nick, pass, username, realname );
            manager.join( channel );
        }
        catch ( IOException e )
        {
            throw new PlexusServiceException( "Error starting IRC service", e );
        }
    }

    public void afterApplicationStart( AppRuntimeProfile applicationRuntimeProfile,
                                       PlexusConfiguration plexusConfiguration )
        throws PlexusServiceException
    {
    }

    public void applicationStop( AppRuntimeProfile runtimeProfile )
        throws PlexusServiceException
    {
    }
}
