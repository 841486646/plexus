package org.codehaus.plexus.builder.application;

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

import org.apache.maven.artifact.repository.ArtifactRepository;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Jason van Zyl
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ApplicationBuilder
{
    String ROLE = ApplicationBuilder.class.getName();

    void assemble( String applicationName, File workingDirectory, List remoteRepositories,
                   ArtifactRepository localRespository, Set projectArtifacts, Set additionalCoreArtifacts,
                   Set serviceArtifacts, File applicationConfiguration, File configurationsDirectory,
                   Properties configurationProperties )
        throws ApplicationBuilderException;

    void bundle( File outputFile, File workingDirectory )
        throws ApplicationBuilderException;
}