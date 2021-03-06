package org.codehaus.plexus.summit.renderer;

import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pull.tools.TemplateLink;
import org.codehaus.plexus.formica.FormManager;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.web.FormRendererManager;
import org.codehaus.plexus.formica.web.FormRendererNotFoundException;
import org.codehaus.plexus.formica.web.FormRenderer;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.StringUtils;

import java.io.Writer;
import java.util.Map;
import java.util.HashMap;

import ognl.Ognl;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @todo the collection stuff needs to be refined, how to create the collection subset for massive collections
 *       you don't want the whole collection ...
 * @todo need to throw a rendering exception here and get rid of Exception and SummitException
 */
public class WebFormRenderer
    extends AbstractRenderer
{
    private I18N i18n;

    private FormManager formManager;

    private FormRendererManager formRendererManager;

    // How to get the collection I need to render summaries

    public void render( RunData data, String view, Writer writer )
        throws SummitException, Exception
    {
        String target = data.getTarget();

        String id = target.substring( 0, target.indexOf( "." ) );

        String mode = data.getParameters().getString( "mode" );

        Object formData = null;

        Form form = formManager.getForm( id );

        // I would like to get this and set it somewhere else...

        if ( mode == null )
        {
            mode = "add";
        }

        // I need to deal with these options polymorphically so that i can
        // add more options that i have not forseen.

        // ----------------------------------------------------------------------

        // This is still a bit hackish, need to do it completely with Ognl in
        // some fashion. Would eventually like to generate classes for the lookup.

        if ( mode.equals( "summary" ) )
        {
            Object o = lookup( form.getSourceRole() );

            formData = Ognl.getValue( "issues", o );
        }
        else if ( mode.equals( "update" ) || mode.equals( "view" ) )
        {
            Object o = lookup( form.getSourceRole() );

            String i = data.getParameters().getString( "id" );

            Map map = new HashMap();

            map.put( "id", i );

            formData = Ognl.getValue( form.getLookupExpression(), map, o );
        }

        // ----------------------------------------------------------------------

        TemplateLink tl = new TemplateLink();

        tl.setRunData( data );

        FormRenderer renderer = null;

        try
        {
            renderer = formRendererManager.getFormRenderer( mode );
        }
        catch ( FormRendererNotFoundException e )
        {
            e.printStackTrace();
        }

        renderer.render( form, writer, i18n, formData, tl.toString() );
    }

    public boolean viewExists( String view )
    {
        return true;
    }
}
