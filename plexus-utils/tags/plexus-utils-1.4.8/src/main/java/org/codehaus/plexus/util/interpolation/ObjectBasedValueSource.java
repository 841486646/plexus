package org.codehaus.plexus.util.interpolation;

/*
 * Copyright 2007 The Codehaus Foundation.
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
import org.codehaus.plexus.util.introspection.ReflectionValueExtractor;

/**
 * @version $Id$
 */
public class ObjectBasedValueSource
    implements ValueSource
{

    private final Object root;

    public ObjectBasedValueSource( Object root )
    {
        this.root = root;
    }

    public Object getValue( String expression )
    {
        try
        {
            return ReflectionValueExtractor.evaluate( expression, root, false );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

}
