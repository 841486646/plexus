package org.codehaus.plexus.formica;

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

import org.codehaus.plexus.formica.population.TargetPopulationException;
import org.codehaus.plexus.formica.validation.FormValidationResult;
import org.codehaus.plexus.formica.validation.Validator;
import org.codehaus.plexus.formica.validation.group.GroupValidator;
import org.codehaus.plexus.formica.validation.manager.ValidatorNotFoundException;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public interface FormManager
{
    static String ROLE = FormManager.class.getName();

    void addForm( Form form );

    Form getForm( String formId )
        throws FormNotFoundException;

    Validator getValidator( String validatorId )
        throws ValidatorNotFoundException;

    GroupValidator getGroupValidator( String groupValidatorId );

    // ----------------------------------------------------------------------
    // Form validation
    // ----------------------------------------------------------------------

    FormValidationResult validate( String formId, Map data )
        throws FormicaException, FormNotFoundException, ValidatorNotFoundException;

    FormValidationResult validate( Form form, Map data )
        throws FormicaException, ValidatorNotFoundException;

    // ----------------------------------------------------------------------
    // Population
    // ----------------------------------------------------------------------

    public void populate( Form form, Map data, Object target )
        throws TargetPopulationException;

    public void populate( String formId, Map data, Object target )
        throws TargetPopulationException, FormNotFoundException;

    public Object populate( String formId, Map data, ClassLoader classLoader )
        throws TargetPopulationException, FormNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException;

}
