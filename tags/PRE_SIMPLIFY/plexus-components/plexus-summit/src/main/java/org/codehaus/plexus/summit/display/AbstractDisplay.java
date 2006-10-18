package org.codehaus.plexus.summit.display;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.ServletOutputStream;

import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * @see org.codehaus.plexus.summit.display.Display
 *
 * @author <a href="mailto:jvanzyl@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Id$
 */
public abstract class AbstractDisplay
    extends AbstractSummitComponent
    implements Display
{
    private String encoding = "ISO-8859-1";

    private String defaultMimeType = "text/html";

    public abstract void render( RunData data )
        throws Exception;

    protected Writer getWriter( RunData data )
        throws IOException, UnsupportedEncodingException
    {
        data.getResponse().setContentType( defaultMimeType );

        data.getResponse().setStatus( 200 );

        ServletOutputStream output = data.getResponse().getOutputStream();

        OutputStreamWriter osw = new OutputStreamWriter( output, encoding );

        return osw;
    }
    
    public String getDefaultEncoding()
    {
        return encoding;
    }
}