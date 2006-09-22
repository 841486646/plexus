package org.codehaus.plexus.security.authentication;

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

import org.codehaus.plexus.util.StringUtils;

/**
 * PasswordBasedAuthenticationDataSource: the username is considered the principal with this data source
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.authentication.AuthenticationDataSource"
 *                   role-hint="password"
 *                   instantiation-strategy="per-lookup"
 */
public class PasswordBasedAuthenticationDataSource
    implements AuthenticationDataSource
{
    private String username;

    private String password;
    
    public PasswordBasedAuthenticationDataSource()
    {
        
    }
    
    public PasswordBasedAuthenticationDataSource(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
    
    public String getPassword()
    {
        return password;
    }

    public String getUsername()
    {
        return username;
    }
    
    public void setPassword( String password )
    {
        this.password = password;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }


    public Object getPrincipal()
    {
        return username;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "PasswordBasedAuthenticationDataSource[" );
        sb.append( "username=" ).append( username );
        sb.append( ",password=" );
        if ( StringUtils.isNotEmpty( password ) )
        {
            // Intentionally not showing real password 
            sb.append( "***" );
        }
        sb.append( "]" );
        return sb.toString();
    }
}
