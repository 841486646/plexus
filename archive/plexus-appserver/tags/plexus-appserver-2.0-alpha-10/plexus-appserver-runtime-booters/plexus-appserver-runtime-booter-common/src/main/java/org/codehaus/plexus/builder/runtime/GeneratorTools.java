package org.codehaus.plexus.builder.runtime;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

/**
 * @author Jason van Zyl
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.builder.runtime.GeneratorTools"
 */
public class GeneratorTools
    extends AbstractLogEnabled
{
    private static final String PROPERTY_APP_NAME = "app.name";

    private static final String PROPERTY_APP_LONG_NAME = "app.long.name";

    private static final String PROPERTY_APP_DESCRIPTION = "app.description";

    /**
     * @plexus.requirement
     */
    public VelocityComponent velocity;

    public void executable( File file )
        throws IOException,
            CommandLineException
    {
        if ( Os.isFamily( "unix" ) )
        {
            Commandline cli = new Commandline();

            cli.setExecutable( "chmod" );

            cli.createArgument().setValue( "+x" );

            cli.createArgument().setValue( file.getAbsolutePath() );

            cli.execute();
        }
    }

    public File mkdirs( File directory )
        throws IOException
    {
        if ( !directory.exists() )
        {
            if ( !directory.mkdirs() )
            {
                throw new IOException( "Could not make directories '" + directory.getAbsolutePath() + "'." );
            }
        }

        return directory;
    }

    /**
     * Creates parent directories for file.
     *
     * @param file
     * @return the file parameter
     * @throws IOException
     */
    public File mkParentDirs( File file )
        throws IOException
    {
        File directory = file.getParentFile();
        if ( !directory.exists() )
        {
            if ( !directory.mkdirs() )
            {
                throw new IOException( "Could not make directories '" + directory.getAbsolutePath() + "'." );
            }
        }

        return file;
    }

    public InputStream getResourceAsStream( String resource )
        throws IOException
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream( resource );

        if ( is == null )
        {
            throw new IOException( "Could not find resource '" + resource + "'." );
        }

        return is;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void filterCopy( File in, File out, Map map )
        throws IOException
    {
        filterCopy( new FileReader( in ), out, map );
    }

    public void filterCopy( InputStream in, File out, Map map )
        throws IOException
    {
        filterCopy( new InputStreamReader( in ), out, map );
    }

    public void filterCopy( Reader in, File out, Map map )
        throws IOException
    {
        InterpolationFilterReader reader = new InterpolationFilterReader( in, map, "@", "@" );

        Writer writer = new FileWriter( out );

        IOUtil.copy( reader, writer );

        writer.close();
    }

    protected void filterCopy( File in, File out, Map map, String beginToken, String endToken )
        throws IOException
    {
        filterCopy( new FileReader( in ), out, map, beginToken, endToken );
    }

    protected void filterCopy( InputStream in, File out, Map map, String beginToken, String endToken )
        throws IOException
    {
        filterCopy( new InputStreamReader( in ), out, map, beginToken, endToken );
    }

    protected void filterCopy( Reader in, File out, Map map, String beginToken, String endToken )
        throws IOException
    {
        InterpolationFilterReader reader = new InterpolationFilterReader( in, map, beginToken, endToken );

        Writer writer = new FileWriter( out );

        IOUtil.copy( reader, writer );

        writer.close();
    }

    public void copyResource( String filename, String resource, boolean makeExecutable, File basedir )
        throws IOException
    {
        File target = new File( basedir, filename );

        copyResourceToFile( resource, target );

        if ( makeExecutable )
        {
            try
            {
                executable( target );
            }
            catch ( CommandLineException e )
            {
                throw new IOException( "Error making resource executable '" + resource + "'" );
            }
        }
    }

    public void copyResourceToFile( String resource, File target )
        throws IOException
    {
        InputStream is = getResourceAsStream( resource );

        mkdirs( target.getParentFile() );

        OutputStream os = new FileOutputStream( target );

        IOUtil.copy( is, os );

        IOUtil.close( is );

        IOUtil.close( os );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public Properties loadConfigurationProperties( File configurationPropertiesFile )
        throws IOException,
            PlexusRuntimeBootloaderGeneratorException
    {
        Properties properties = new Properties();

        if ( configurationPropertiesFile == null )
        {
            throw new PlexusRuntimeBootloaderGeneratorException(
                "The runtime builder requires a configurator properties file." );
        }

        properties.load( new FileInputStream( configurationPropertiesFile ) );

        // ----------------------------------------------------------------------
        // Validate that some required properties are present
        // ----------------------------------------------------------------------

        assertHasProperty( properties, PROPERTY_APP_NAME );

        assertHasProperty( properties, PROPERTY_APP_LONG_NAME );

        assertHasProperty( properties, PROPERTY_APP_DESCRIPTION );

        return properties;
    }

    public void assertHasProperty( Properties properties, String key )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        if ( StringUtils.isEmpty( properties.getProperty( key ) ) )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Missing configurator property '" + key + "'." );
        }
    }

    // ----------------------------------------------------------------------
    // Velocity methods
    // ----------------------------------------------------------------------

    public void mergeTemplate( String templateName, File outputFileName, boolean dos, Properties configurationProperties )
        throws IOException,
            PlexusRuntimeBootloaderGeneratorException
    {
        StringWriter buffer = new StringWriter( 100 * FileUtils.ONE_KB );

        File tmpFile = File.createTempFile( outputFileName.getName(), null );

        // noinspection OverlyBroadCatchBlock
        try
        {
            velocity.getEngine().mergeTemplate( templateName, new VelocityContext(), buffer );
        }
        catch ( ResourceNotFoundException ex )
        {
            throw new PlexusRuntimeBootloaderGeneratorException(
                "Missing Velocity template: '" + templateName + "'.",
                ex );
        }
        catch ( Exception ex )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Exception merging the velocity template.", ex );
        }

        FileOutputStream output = new FileOutputStream( tmpFile );

        BufferedReader reader = new BufferedReader( new StringReader( buffer.toString() ) );

        String line;

        // noinspection NestedAssignment
        while ( ( line = reader.readLine() ) != null )
        {
            output.write( line.getBytes() );

            if ( dos )
            {
                output.write( '\r' );
            }

            output.write( '\n' );
        }

        output.close();

        filterCopy( tmpFile, outputFileName, configurationProperties, "@{", "}@" );
    }
}
