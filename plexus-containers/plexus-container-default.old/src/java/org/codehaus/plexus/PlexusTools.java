package org.codehaus.plexus;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.Configuration;
import org.codehaus.plexus.configuration.builder.XmlPullConfigurationBuilder;
import org.codehaus.plexus.configuration.xstream.XStreamTool;

import java.io.StringReader;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PlexusTools
{
    public static ComponentDescriptor buildComponentDescriptor( String configuration )
        throws Exception
    {
        XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();

        Configuration c = builder.parse( new StringReader( configuration ) );

        return buildComponentDescriptor( c );
    }

    public static ComponentDescriptor buildComponentDescriptor( Configuration configuration )
        throws Exception
    {
        XStreamTool xstreamTool = new XStreamTool();

        xstreamTool.alias( "component", ComponentDescriptor.class );

        xstreamTool.alias( "requirement", String.class );

        ComponentDescriptor cd = (ComponentDescriptor) xstreamTool.build( (Configuration) configuration, ComponentDescriptor.class );

        return cd;
    }
}
