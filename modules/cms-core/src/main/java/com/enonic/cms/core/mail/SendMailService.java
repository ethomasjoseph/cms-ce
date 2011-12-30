/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mail;

import com.enonic.cms.core.security.user.QualifiedUsername;

public interface SendMailService
{
    public void sendChangePasswordMail( QualifiedUsername userName, String newPassword, MessageSettings settings );

    public void sendNotificationMail( String recipient, String ccs, MessageSettings settings );
}
