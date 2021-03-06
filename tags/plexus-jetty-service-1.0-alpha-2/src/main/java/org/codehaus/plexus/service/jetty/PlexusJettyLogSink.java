package org.codehaus.plexus.service.jetty;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import org.codehaus.plexus.logging.Logger;

import org.mortbay.util.LogSink;
import org.mortbay.util.Frame;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusJettyLogSink
    implements LogSink
{
    private Logger logger;

    private boolean started;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public PlexusJettyLogSink( Logger logger )
    {
        this.logger = logger;
    }

    // ----------------------------------------------------------------------
    // LogSink Implementation
    // ----------------------------------------------------------------------

    public void setOptions( String s )
    {
        // No options to set
    }

    public String getOptions()
    {
        return "";
    }

    public void log( String tag, Object message, Frame frame, long time )
    {
//        System.err.println( "tag: " + tag );
//        System.err.println( "message:" + message );
//        System.err.println( "frame: " + frame );
    }

    public void log( String formattedLog )
    {
//        logger.info( formattedLog );
    }

    // ----------------------------------------------------------------------
    // LifeCycle Implementation
    // ----------------------------------------------------------------------

    public void start()
        throws Exception
    {
        started = true;
    }

    public void stop()
        throws InterruptedException
    {
        started = false;
    }

    public boolean isStarted()
    {
        return started;
    }
}
