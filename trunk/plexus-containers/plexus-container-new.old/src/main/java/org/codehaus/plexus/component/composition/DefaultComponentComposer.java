package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultComponentComposer
    implements ComponentComposer
{
    public void assembleComponent( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   PlexusContainer container )
        throws CompositionException
    {
        if ( componentDescriptor.getRequirements().size() == 0 )
        {
            return; //nothing to do
        }

        //!! We should check for cycles here before we attempt this. Look
        //   for possible ways to short-circuit the cycles.

        final Set requirements = componentDescriptor.getRequirements();

        for ( final Iterator i = requirements.iterator(); i.hasNext(); )
        {
            final ComponentRequirement requirement = (ComponentRequirement) i.next();

            final String role = requirement.getRole();

            final Field field = findMatchingField( component, componentDescriptor, requirement );

            if ( !field.isAccessible() )
            {
                field.setAccessible( true );
            }

            // We have a field to which we should assing component(s).
            // Cardinality is determined by field.getType() method
            // It can be array, map, collection or "ordinary" field
            assignRequirmentToField( field, container, role, component );
        }
    }

    protected void assembleComponents( final Map components,
                                       final Map componentDescriptorMap,
                                       final PlexusContainer container )
        throws CompositionException
    {

        final Set roleHints = componentDescriptorMap.keySet();

        for ( final Iterator iterator = roleHints.iterator(); iterator.hasNext(); )
        {
            final String roleHint = (String) iterator.next();

            final ComponentDescriptor componentDescriptor = (ComponentDescriptor) componentDescriptorMap.get( roleHint );

            final Object component = components.get( roleHint );

            assembleComponent( component, componentDescriptor, container );
        }
    }

    private void assignRequirmentToField( final Field field,
                                          PlexusContainer container,
                                          final String role,
                                          Object component )
        throws CompositionException
    {
        try
        {
            if ( field.getType().isArray() )
            {
                Map dependencies = container.lookupMap( role );

                final Object[] array = (Object[]) Array.newInstance( field.getType(), dependencies.size() );

                assembleComponents( dependencies, container.getComponentDescriptorMap( role ), container );

                field.set( component, dependencies.entrySet().toArray( array ) );
            }
            else if ( Map.class.isAssignableFrom( field.getType() ) )
            {
                Map dependencies = container.lookupMap( role );

                assembleComponents( dependencies, container.getComponentDescriptorMap( role ), container );

                field.set( component, dependencies );
            }
            else if ( List.class.isAssignableFrom( field.getType() ) )
            {
                Map dependencies = container.lookupMap( role );

                assembleComponents( dependencies, container.getComponentDescriptorMap( role ), container );

                field.set( component, container.lookupList( role ) );
            }
            else if ( Set.class.isAssignableFrom( field.getType() ) )
            {
                Map dependencies = container.lookupMap( role );

                assembleComponents( dependencies, container.getComponentDescriptorMap( role ), container );

                field.set( component, dependencies.entrySet() );
            }
            else //"ordinary" field
            {
                Object dependency = container.lookup( role );

                // simple recursion
                assembleComponent( dependency, container.getComponentDescriptor( role ), container );

                field.set( component, dependency );
            }
        }
        catch ( Exception e )
        {
            throw new CompositionException( "Composition failed: " + e.getMessage() );
        }
    }

    protected Field findMatchingField( Object component,
                                       ComponentDescriptor componentDescriptor,
                                       final ComponentRequirement requirement )
        throws CompositionException
    {
        final String fieldName = requirement.getFieldName();

        Field field = null;

        if ( fieldName != null )
        {
            field = getFieldByName( component, fieldName, componentDescriptor );
        }
        else
        {
            Class fieldClass = null;

            try
            {
                fieldClass = Class.forName( requirement.getRole() );
            }
            catch ( ClassNotFoundException e )
            {
                StringBuffer msg = new StringBuffer( "Component Composition failed. Requirment class: '" );

                msg.append( requirement.getRole() );

                msg.append( "' not found. Component role: '" );

                msg.append( componentDescriptor.getRole() );

                msg.append( "'" );

                if ( componentDescriptor.getRoleHint() != null )
                {
                    msg.append( ", role-hint: '" );

                    msg.append( componentDescriptor.getRoleHint() );

                    msg.append( "'" );
                }
                throw new CompositionException( msg.toString() );
            }

            field = getFieldByType( component, fieldClass, componentDescriptor );
        }
        return field;
    }

    protected Field getFieldByName( final Object component,
                                    final String fieldName,
                                    final ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        Field field = null;

        try
        {
            field = component.getClass().getDeclaredField( fieldName );
        }
        catch ( NoSuchFieldException e )
        {
            StringBuffer msg = new StringBuffer( "Component Composition failed. No field of name: '" );

            msg.append( fieldName );

            msg.append( "' exists in component of role: '" );

            msg.append( componentDescriptor.getRole() );

            msg.append( "'" );

            if ( componentDescriptor.getRoleHint() != null )
            {
                msg.append( " and role-hint: '" );

                msg.append( componentDescriptor.getRoleHint() );

                msg.append( "'" );
            }
            throw new CompositionException( msg.toString() );
        }

        // we want to use private fields.
        field.setAccessible( true );

        return field;
    }

    protected Field getFieldByTypeIncludingSuperclasses( final Class componentClass,
                                                         final Class type,
                                                         final ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        Field field = null;

        Class arrayType = Array.newInstance( type, 0 ).getClass();

        Field[] fields = componentClass.getDeclaredFields();

        for ( int i = 0; i < fields.length; i++ )
        {
            Class fieldType = fields[i].getType();

            if ( fieldType.isAssignableFrom( type ) || fieldType.isAssignableFrom( arrayType ) )
            {
                field = fields[i];

                break;
            }
        }

        if ( field == null && componentClass.getSuperclass() != Object.class )
        {
            field = getFieldByTypeIncludingSuperclasses( componentClass.getSuperclass(), type, componentDescriptor );
        }

        return field;
    }

    protected Field getFieldByType( final Object component,
                                    final Class type,
                                    final ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        Field field = getFieldByTypeIncludingSuperclasses( component.getClass(), type, componentDescriptor );

        if ( field == null )
        {
            StringBuffer msg = new StringBuffer( "Component Composition failed. No field of type: '" );

            msg.append( type );

            if ( componentDescriptor != null )
            {
                msg.append( "' exists in component of role: '" );

                msg.append( componentDescriptor.getRole() );

                msg.append( "'" );

                if ( componentDescriptor.getRoleHint() != null )
                {
                    msg.append( " and role-hint: '" );

                    msg.append( componentDescriptor.getRoleHint() );

                    msg.append( "'" );
                }
            }

            throw new CompositionException( msg.toString() );
        }

        return field;
    }
}
