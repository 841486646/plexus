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
package org.codehaus.plexus.server.jabber;

import org.codehaus.plexus.server.jabber.session.IMServerSession;

/**
 * @author AlAg
 * @version 1.0
 */
public interface S2SConnectorManager
{

    public void setConnectionHandler( IMConnectionHandler connectionHandler );

    public IMServerSession getCurrentRemoteSession( String hostname )
        throws Exception;

    public IMServerSession getRemoteSessionWaitForValidation( String hostname,
                                                              long timeout )
        throws Exception;

    public void verifyRemoteHost( String hostname,
                                  String dialbackValue,
                                  String id,
                                  IMServerSession session )
        throws Exception;
} // class