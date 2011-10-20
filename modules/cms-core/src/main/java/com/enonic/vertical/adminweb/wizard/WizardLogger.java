/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.wizard;

import com.enonic.cms.api.util.LogFacade;
import com.enonic.vertical.adminweb.VerticalAdminLogger;

public final class WizardLogger
    extends VerticalAdminLogger
{
    private final static LogFacade LOG = LogFacade.get(WizardLogger.class);

    public static void errorWizard(String message, Object msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new WizardException(message, throwable);
    }

    public static void errorWizard(String message, Throwable throwable)
    {
        LOG.errorCause(message, throwable);
        throw new WizardException(message, throwable);
    }
}
