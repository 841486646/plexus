package org.codehaus.plexus.server.ftp.usermanager;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @plexus.component 
 */
public class MemoryUserManager
    implements UserManager, Initializable
{
    private Map users;

    public User getUserByName( String name )
    {
        return (User) users.get( name );
    }

    public boolean doesExist( String name )
    {
        return users.containsKey( name );
    }

    public boolean authenticate( String login,
                                 String password )
    {
        return true;
    }

    public String getAdminName()
    {
        return "The Big Cheese";
    }

    public void save( User user )
    {                
        users.put( user.getName(), user );
    }

    public void delete( String user )
    {
        User u = (User) users.get( user );

        users.remove( u);
    }

    public Collection getAllUserNames()
    {
        return null;
    }

    public void reload()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    // ----------------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        users = new HashMap();        
    }
}
