/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.enonic.cms.core.VerticalProperties;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.UserDao;

public final class SendMailServiceImpl
    implements SendMailService
{
    protected final static Logger LOG =
        LoggerFactory.getLogger( SendMailServiceImpl.class );

    @Inject
    private VerticalProperties properties;

    @Inject
    private JavaMailSender mailSender;

    @Inject
    private UserDao userDao;

    private static final String MAIL_ENCODING = "UTF-8";

    private String fromMail;

    public final void setFromMail( String value )
    {
        this.fromMail = value;
    }

    public final void sendChangePasswordMail( QualifiedUsername userName, String newPassword, MessageSettings settings )
    {
        UserEntity entity = this.userDao.findByQualifiedUsername( userName );
        if ( entity != null )
        {
            sendChangePasswordMail( entity, newPassword, settings );
        }
        else
        {
            LOG.warn( "Unknown user [" + userName + "]. Skipped sending mail." );
        }
    }

    public final void sendNotificationMail( String recipient, String ccs, MessageSettings settings )
    {
        try
        {
            settings = createSettingsIfNeeded( settings );
            MimeMessageHelper message = createMessage( settings );
            List<String> ccList = parseMailString( ccs );
            composeNotificationMail( message, recipient, ccList, settings );
            sendMessage( message );
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to send mail", e );
        }
    }

    private List<String> parseMailString (String mailString)
    {
        StringTokenizer strtok = new StringTokenizer( mailString, ",; " );
        List<String> emails = new ArrayList<String>(  );
        while (strtok.hasMoreTokens())
        {
            emails.add( strtok.nextToken() );
        }
        return emails;
    }

    private void composeNotificationMail( MimeMessageHelper message,
                                          String recipient, List<String> ccs, MessageSettings settings)
            throws MessagingException
    {
        message.setSubject( settings.getSubject() );
        message.setText( settings.getBody() );
        message.addTo( recipient );
        for ( String cc: ccs )
        {
            message.addCc( cc );
        }
    }

    private void sendChangePasswordMail( UserEntity user, String newPassword, MessageSettings settings )
    {
        try
        {
            settings = createSettingsIfNeeded( settings );
            MimeMessageHelper message = createMessage( settings );
            composeChangePasswordMail( message, user, newPassword, settings );
            sendMessage( message );
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to send mail", e );
        }
    }

    private MessageSettings createSettingsIfNeeded( MessageSettings settings )
    {
        if ( settings == null )
        {
            settings = new MessageSettings();
        }

        if ( settings.getFromMail() == null )
        {
            settings.setFromMail( this.fromMail );
            settings.setFromName( null );
        }

        return settings;
    }

    private MimeMessageHelper createMessage( MessageSettings settings )
        throws Exception
    {
        MimeMessageHelper message = new MimeMessageHelper( this.mailSender.createMimeMessage(), MAIL_ENCODING );
        message.setFrom( settings.getFromMail(), settings.getFromName() );
        return message;
    }

    private void sendMessage( MimeMessageHelper message )
    {
        this.mailSender.send( message.getMimeMessage() );
    }

    private void composeChangePasswordMail( MimeMessageHelper message, UserEntity user, String newPassword, MessageSettings settings )
        throws Exception
    {
        String subject = settings.getSubject();
        if ( subject == null )
        {
            subject = this.properties.getAdminNewPasswordMailSubject();
        }

        String body = settings.getBody();
        if ( body == null )
        {
            body = this.properties.getAdminNewPasswordMailBody();
        }

        message.addTo( user.getEmail(), user.getDisplayName() );
        message.setSubject( subject );

        body = body.replaceAll( "%password%", newPassword );
        body = body.replaceAll( "%uid%", user.getName() );

        message.setText( body );
    }
}
