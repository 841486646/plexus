/* Created on Sep 14, 2004 */
package org.codehaus.plexus.cling.tags.app;

import java.util.LinkedList;
import java.util.List;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.cli.InvocationTemplate;
import org.codehaus.plexus.cling.cli.Option;

/**
 * @author jdcasey
 */
public class OptionSetTag
    extends AbstractMarmaladeTag
{
    
    private List options = new LinkedList();

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        processChildren(context);
        
        InvocationTemplate template = new InvocationTemplate(options);
        LegalUsageTag parent = (LegalUsageTag)requireParent(LegalUsageTag.class);
        parent.addOptionSet(template);
    }
    
    public void addOption(Option option) {
        options.add(option);
    }
}
