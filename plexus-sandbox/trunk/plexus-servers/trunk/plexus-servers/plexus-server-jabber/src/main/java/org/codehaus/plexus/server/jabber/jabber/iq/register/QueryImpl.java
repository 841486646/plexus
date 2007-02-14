/*
 * BSD License http://open-im.org/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.org
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.org/
 */
package org.codehaus.plexus.server.jabber.jabber.iq.register;

import java.util.Map;
import java.util.HashMap;

import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;
import org.codehaus.plexus.server.jabber.IMRouter;
import org.codehaus.plexus.server.jabber.ServerParameters;

import org.codehaus.plexus.server.jabber.data.jabber.User;
import org.codehaus.plexus.server.jabber.data.UsersManager;
import org.codehaus.plexus.server.jabber.data.jabber.IQPacket;
import org.codehaus.plexus.server.jabber.data.Account;
import org.codehaus.plexus.server.jabber.data.storage.AccountRepositoryHolder;
import org.codehaus.plexus.server.jabber.session.IMClientSession;
import org.codehaus.plexus.server.jabber.session.IMSession;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="iq.register.Query" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.iq.register.Query"
 */
public class QueryImpl
    extends DefaultSessionProcessor
    implements Query
{

    private ServerParameters m_serverParameters;
    private UsersManager m_usersManager;
    private AccountRepositoryHolder m_accountRepository;

    /**
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.ServerParameters:1.0"  key="ServerParameters"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.data.Account:1.0" key="Account"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.data.UsersManager:1.0" key="UsersManager"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.data.storage.AccountRepositoryHolder:1.0" key="AccountRepositoryHolder"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.iq.register.Username:1.0" key="iq.register.Username"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.iq.register.Password:1.0" key="iq.register.Password"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.iq.register.Remove:1.0" key="iq.register.Remove"
     */
    public void service( org.apache.avalon.framework.service.ServiceManager serviceManager )
        throws org.apache.avalon.framework.service.ServiceException
    {
        m_usersManager = (UsersManager) serviceManager.lookup( "UsersManager" );
        m_accountRepository = (AccountRepositoryHolder) serviceManager.lookup( "AccountRepositoryHolder" );
        m_serverParameters = (ServerParameters) serviceManager.lookup( "ServerParameters" );
        super.service( serviceManager );
    }

    //-------------------------------------------------------------------------
    public void process( final IMSession session,
                         final Object context )
        throws Exception
    {

        IMClientSession clientSession = (IMClientSession) session;

        User currentUser = clientSession.getUser();
        User user = m_usersManager.getNewUser();
        clientSession.setUser( user );

        Map contextMap = new HashMap();
        contextMap.put( CTX_SHOULD_REMOVE, Boolean.FALSE );
        super.process( session, contextMap );

        String iqId = ( (IQPacket) context ).getId();
        String type = ( (IQPacket) context ).getType();

        // GET
        if ( IQPacket.TYPE_GET.equals( type ) )
        {
            String s = "<iq type='" + IQPacket.TYPE_RESULT + "' id='" + iqId + "' from='" +
                m_serverParameters.getHostName() + "'>" + "<query xmlns='jabber:iq:register'>" +
                "<instructions>Choose a username and password to register with this service.</instructions>" +
                "<password/><username/>" + "</query></iq>";
            session.writeOutputStream( s );
        }

        // SET
        else if ( IQPacket.TYPE_SET.equals( type ) )
        {

            Boolean shouldRemove = (Boolean) contextMap.get( CTX_SHOULD_REMOVE );
            if ( shouldRemove.booleanValue() )
            {
                m_accountRepository.removeAccount( currentUser.getName() );
                String s = "<iq type='" + IQPacket.TYPE_RESULT + "' id='" + iqId + "' />";
                session.writeOutputStream( s );
                clientSession.setUser( null );
            }

            else
            { // no remove
                Account existingAccount = m_accountRepository.getAccount( user.getName() );
                if ( existingAccount == null )
                {
                    setAccount( user );

                    IMRouter router = session.getRouter();
                    router.registerSession( clientSession );

                    String s = "<iq type='" + IQPacket.TYPE_RESULT + "' id='" + iqId + "' />";
                    session.writeOutputStream( s );
                }

                else if ( currentUser != null )
                { // account already exists and we are logged
                    String s = null;
                    if ( currentUser.getName().equals( user.getName() ) )
                    {
                        setAccount( user );
                        s = "<iq type='" + IQPacket.TYPE_RESULT + "' id='" + iqId + "' />";
                    }
                    else
                    {
                        clientSession.setUser( currentUser );
                        s = "<iq type='" + IQPacket.TYPE_ERROR + "' id='" + iqId + "' />";
                    }
                    session.writeOutputStream( s );
                }


                else
                { // abnormal sitatuation sending error
                    String s = "<iq type='" + IQPacket.TYPE_ERROR + "' id='" + iqId + "' />";
                    session.writeOutputStream( s );
                }

            } // else shouldremove                
        }
    }


    private void setAccount( User user )
        throws Exception
    {
        Account account = (Account) m_serviceManager.lookup( "Account" );
        account.setName( user.getName() );
        account.setPassword( user.getPassword() );
        m_accountRepository.setAccount( account );
    }


}

