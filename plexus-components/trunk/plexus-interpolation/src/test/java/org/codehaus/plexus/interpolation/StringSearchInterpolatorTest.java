package org.codehaus.plexus.interpolation;

/*
 * Copyright 2001-2008 Codehaus Foundation.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

public class StringSearchInterpolatorTest
    extends TestCase
{

    public void testLongDelimitersInContext()
        throws InterpolationException
    {
        String src = "This is a <expression>test.label</expression> for long delimiters in context.";
        String result = "This is a test for long delimiters in context.";

        Properties p = new Properties();
        p.setProperty( "test.label", "test" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "<expression>", "</expression>" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        assertEquals( result, interpolator.interpolate( src ) );
    }

    public void testLongDelimitersWithNoStartContext()
        throws InterpolationException
    {
        String src = "<expression>test.label</expression> for long delimiters in context.";
        String result = "test for long delimiters in context.";

        Properties p = new Properties();
        p.setProperty( "test.label", "test" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "<expression>", "</expression>" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        assertEquals( result, interpolator.interpolate( src ) );
    }

    public void testLongDelimitersWithNoEndContext()
        throws InterpolationException
    {
        String src = "This is a <expression>test.label</expression>";
        String result = "This is a test";

        Properties p = new Properties();
        p.setProperty( "test.label", "test" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "<expression>", "</expression>" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        assertEquals( result, interpolator.interpolate( src ) );
    }

    public void testLongDelimitersWithNoContext()
        throws InterpolationException
    {
        String src = "<expression>test.label</expression>";
        String result = "test";

        Properties p = new Properties();
        p.setProperty( "test.label", "test" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "<expression>", "</expression>" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        assertEquals( result, interpolator.interpolate( src ) );
    }

    public void testSimpleSubstitution()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator();
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        assertEquals( "This is a test value.", interpolator.interpolate( "This is a test ${key}." ) );
    }

    public void testSimpleSubstitution_TwoExpressions()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );
        p.setProperty( "key2", "value2" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator();
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        assertEquals( "value-value2", interpolator.interpolate( "${key}-${key2}" ) );
    }

    public void testBrokenExpression_LeaveItAlone()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator();
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        assertEquals( "This is a test ${key.", interpolator.interpolate( "This is a test ${key." ) );
    }

    public void testShouldFailOnExpressionCycle()
    {
        Properties props = new Properties();
        props.setProperty( "key1", "${key2}" );
        props.setProperty( "key2", "${key1}" );

        StringSearchInterpolator rbi = new StringSearchInterpolator();
        rbi.addValueSource( new PropertiesBasedValueSource( props ) );

        try
        {
            rbi.interpolate( "${key1}", new SimpleRecursionInterceptor() );

            fail( "Should detect expression cycle and fail." );
        }
        catch ( InterpolationException e )
        {
            // expected
        }
    }

    public void testShouldResolveByMy_getVar_Method()
        throws InterpolationException
    {
        StringSearchInterpolator rbi = new StringSearchInterpolator();
        rbi.addValueSource( new ObjectBasedValueSource( this ) );
        String result = rbi.interpolate( "this is a ${var}" );

        assertEquals( "this is a testVar", result );
    }

    public void testShouldResolveByContextValue()
        throws InterpolationException
    {
        StringSearchInterpolator rbi = new StringSearchInterpolator();

        Map context = new HashMap();
        context.put( "var", "testVar" );

        rbi.addValueSource( new MapBasedValueSource( context ) );

        String result = rbi.interpolate( "this is a ${var}" );

        assertEquals( "this is a testVar", result );
    }

    public void testShouldResolveByEnvar()
        throws IOException, InterpolationException
    {
        StringSearchInterpolator rbi = new StringSearchInterpolator();

        rbi.addValueSource( new EnvarBasedValueSource( false ) );

        String result = rbi.interpolate( "this is a ${env.HOME} ${env.PATH}" );

        assertFalse( "this is a ${HOME} ${PATH}".equals( result ) );
        assertFalse( "this is a ${env.HOME} ${env.PATH}".equals( result ) );
    }

    public void testUsePostProcessor_DoesNotChangeValue()
        throws InterpolationException
    {
        StringSearchInterpolator rbi = new StringSearchInterpolator();

        Map context = new HashMap();
        context.put( "test.var", "testVar" );

        rbi.addValueSource( new MapBasedValueSource( context ) );

        rbi.addPostProcessor( new InterpolationPostProcessor()
        {
            public Object execute( String expression, Object value )
            {
                return null;
            }
        } );

        String result = rbi.interpolate( "this is a ${test.var}" );

        assertEquals( "this is a testVar", result );
    }

    public void testUsePostProcessor_ChangesValue()
        throws InterpolationException
    {

        StringSearchInterpolator rbi = new StringSearchInterpolator();

        Map context = new HashMap();
        context.put( "test.var", "testVar" );

        rbi.addValueSource( new MapBasedValueSource( context ) );

        rbi.addPostProcessor( new InterpolationPostProcessor()
        {
            public Object execute( String expression, Object value )
            {
                return value + "2";
            }
        } );

        String result = rbi.interpolate( "this is a ${test.var}" );

        assertEquals( "this is a testVar2", result );
    }

    public void testSimpleSubstitutionWithDefinedExpr()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "@{", "}" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        assertEquals( "This is a test value.", interpolator.interpolate( "This is a test @{key}." ) );
    }

    public void testEscape()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "@{", "}" );
        interpolator.setEscapeString( "\\" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        String result = interpolator.interpolate( "This is a test \\@{key}." );

        assertEquals( "This is a test @{key}.", result );
    }

    public void testEscapeWithLongEscapeStr()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "@{", "}" );
        interpolator.setEscapeString( "$$" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        String result = interpolator.interpolate( "This is a test $$@{key}." );

        assertEquals( "This is a test @{key}.", result );
    }

    public void testEscapeWithLongEscapeStrAtStart()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "@{", "}" );
        interpolator.setEscapeString( "$$" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        String result = interpolator.interpolate( "$$@{key} This is a test." );

        assertEquals( "@{key} This is a test.", result );
    }

    public void testNotEscapeWithLongEscapeStrAtStart()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "@{", "}" );
        interpolator.setEscapeString( "$$" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        String result = interpolator.interpolate( "@{key} This is a test." );

        assertEquals( "value This is a test.", result );
    }

    public void testEscapeNotFailWithNullEscapeStr()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "@{", "}" );
        interpolator.setEscapeString( null );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        String result = interpolator.interpolate( "This is a test @{key}." );

        assertEquals( "This is a test value.", result );
    }

    public void testOnlyEscapeExprAtStart()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "@{", "}" );
        interpolator.setEscapeString( "\\" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        String result = interpolator.interpolate( "\\@{key} This is a test." );

        assertEquals( "@{key} This is a test.", result );
    }

    public void testNotEscapeExprAtStart()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "@{", "}" );
        interpolator.setEscapeString( "\\" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        String result = interpolator.interpolate( "@{key} This is a test." );

        assertEquals( "value This is a test.", result );
    }

    public void testEscapeExprAtStart()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "@", "@" );
        interpolator.setEscapeString( "\\" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        String result = interpolator.interpolate( "\\@key@ This is a test @key@." );

        assertEquals( "@key@ This is a test value.", result );
    }

    public void testNPEFree()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator( "@{", "}" );
        interpolator.setEscapeString( "\\" );
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        String result = interpolator.interpolate( null );

        assertEquals( "", result );
    }

    public void testInterruptedInterpolate()
        throws InterpolationException
    {
        Interpolator interpolator = new StringSearchInterpolator();
        RecursionInterceptor recursionInterceptor = new SimpleRecursionInterceptor();
        final boolean[] error = new boolean[] { false };
        interpolator.addValueSource( new ValueSource()
        {
            public Object getValue( String expression ) {
                if ( expression.equals( "key" ) )
                {
                    if ( error[ 0 ] )
                    {
                        throw new IllegalStateException( "broken" );
                    }
                    return "val";
                }
                else
                {
                    return null;
                }
            }
            public List getFeedback()
            {
                return Collections.EMPTY_LIST;
            }
            public void clearFeedback()
            {
            }
        } );
        assertEquals( "control case", "-val-" , interpolator.interpolate( "-${key}-", recursionInterceptor ) );
        error[ 0 ] = true;
        try
        {
            interpolator.interpolate( "-${key}-", recursionInterceptor );
            fail( "should have thrown exception" );
        }
        catch ( IllegalStateException x )
        {
            // right
        }
        error[ 0 ] = false;
        assertEquals( "should not believe there is a cycle here", "-val-", interpolator.interpolate( "-${key}-", recursionInterceptor ) );
    }

    public String getVar()
    {
        return "testVar";
    }

}
