/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.content.*;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.content.contentdata.*;
import com.enonic.cms.core.content.contentdata.custom.support.CustomContentDataFormParser;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.portal.httpservices.UserServicesException;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.UserServicesService;
import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.VerticalSecurityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;

/**
 * Created by rmy - Date: Jun 24, 2009
 */
public class CustomContentHandlerController
    extends ContentHandlerBaseController
{

    protected void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey, String operation )
        throws VerticalUserServicesException, VerticalEngineException, IOException, ClassNotFoundException, IllegalAccessException,
        InstantiationException, ParseException
    {
        if ( operation.equals( "modify" ) )
        {
            handlerModify( request, response, formItems );
        }
    }

    @Override
    protected void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalSecurityException, RemoteException
    {
        User oldUser = securityService.getLoggedInPortalUser();

        int categoryKey = formItems.getInt( "categorykey", -1 );

        if ( categoryKey == -1 )
        {
            String message = "Category key not specified.";
            VerticalUserServicesLogger.warn(message );
            redirectToErrorPage( request, response, formItems, ERR_MISSING_CATEGORY_KEY );
            return;
        }

        CreateContentCommand createContentCommand;

        try
        {
            createContentCommand = parseCreateContentCommand( formItems );
        }
        catch ( ContentDataParserInvalidDataException e )
        {
            String message = e.getMessage();
            VerticalUserServicesLogger.warn(message );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID );
            return;
        }
        catch ( ContentDataParserException e )
        {
            VerticalUserServicesLogger.error( e.getMessage(), e );
            throw new UserServicesException( ERR_OPERATION_BACKEND );
        }
        catch ( ContentDataParserUnsupportedTypeException e )
        {
            VerticalUserServicesLogger.error( e.getMessage(), e );
            throw new UserServicesException( ERR_OPERATION_BACKEND );
        }

        UserEntity runningUser = securityService.getUser( oldUser );

        createContentCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );

        createContentCommand.setCreator( runningUser );

        try
        {
            contentService.createContent( createContentCommand );
        }
        catch ( CreateContentException e )
        {
            RuntimeException cause = e.getRuntimeExceptionCause();

            if ( cause instanceof MissingRequiredContentDataException )
            {
                String message = e.getMessage();
                VerticalUserServicesLogger.warn(message );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_MISSING );
                return;
            }
            else if ( cause instanceof InvalidContentDataException )
            {
                String message = e.getMessage();
                VerticalUserServicesLogger.warn(message );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID );
                return;
            }
            else
            {
                throw cause;
            }
        }

        redirectToPage( request, response, formItems );
    }


    @Override
    protected void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
    {
        User oldTypeUser = securityService.getOldUserObject();

        int contentKey = formItems.getInt( "key", -1 );

        if ( contentKey == -1 )
        {
            String message = "Content key not specified.";
            VerticalUserServicesLogger.warn(message );
            redirectToErrorPage( request, response, formItems, ERR_MISSING_CATEGORY_KEY );
            return;
        }

        UserEntity runningUser = securityService.getUser( oldTypeUser );

        UpdateContentCommand updateContentCommand;

        try
        {
            updateContentCommand = parseUpdateContentCommand( runningUser, formItems, false );
        }
        catch ( ContentDataParserException e )
        {
            VerticalUserServicesLogger.error( e.getMessage(), e );
            throw new UserServicesException( ERR_OPERATION_BACKEND );
        }
        catch ( ContentDataParserInvalidDataException e )
        {
            String message = e.getMessage();
            VerticalUserServicesLogger.warn(message );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID );
            return;
        }
        catch ( ContentDataParserUnsupportedTypeException e )
        {
            VerticalUserServicesLogger.error( e.getMessage(), e );
            throw new UserServicesException( ERR_OPERATION_BACKEND );
        }

        updateContentCommand.setUpdateStrategy( UpdateContentCommand.UpdateStrategy.UPDATE );

        UpdateContentResult updateContentResult;

        try
        {
            updateContentResult = contentService.updateContent( updateContentCommand );
        }
        catch ( UpdateContentException e )
        {
            RuntimeException cause = e.getRuntimeExceptionCause();

            if ( cause instanceof MissingRequiredContentDataException )
            {
                String message = e.getMessage();
                VerticalUserServicesLogger.warn(message );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_MISSING );
                return;
            }
            else if ( cause instanceof InvalidContentDataException )
            {
                String message = e.getMessage();
                VerticalUserServicesLogger.warn(message );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID );
                return;
            }
            else
            {
                throw cause;
            }
        }

        if ( updateContentResult.isAnyChangesMade() )
        {
            new PageCacheInvalidatorForContent( siteCachesService ).invalidateForContent( updateContentResult.getTargetedVersion() );
        }

        redirectToPage( request, response, formItems );
    }

    protected void handlerModify( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
    {
        User oldTypeUser = securityService.getOldUserObject();

        int contentKey = formItems.getInt( "key", -1 );

        if ( contentKey == -1 )
        {
            String message = "Content key not specified.";
            VerticalUserServicesLogger.error(message );
            redirectToErrorPage( request, response, formItems, ERR_MISSING_CATEGORY_KEY );
            return;
        }

        UserEntity runningUser = securityService.getUser( oldTypeUser );

        UpdateContentCommand updateContentCommand;

        try
        {
            updateContentCommand = parseUpdateContentCommand( runningUser, formItems, true );
        }
        catch ( ContentDataParserInvalidDataException e )
        {
            String message = e.getMessage();
            VerticalUserServicesLogger.warn(message );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID );
            return;
        }
        catch ( ContentDataParserException e )
        {
            VerticalUserServicesLogger.error( e.getMessage(), e );
            throw new UserServicesException( ERR_OPERATION_BACKEND );
        }
        catch ( ContentDataParserUnsupportedTypeException e )
        {
            VerticalUserServicesLogger.error( e.getMessage(), e );
            throw new UserServicesException( ERR_OPERATION_BACKEND );
        }

        updateContentCommand.setUpdateStrategy( UpdateContentCommand.UpdateStrategy.MODIFY );

        UpdateContentResult updateContentResult;

        try
        {
            updateContentResult = contentService.updateContent( updateContentCommand );
        }
        catch ( UpdateContentException e )
        {
            RuntimeException cause = e.getRuntimeExceptionCause();

            if ( cause instanceof MissingRequiredContentDataException )
            {
                String message = e.getMessage();
                VerticalUserServicesLogger.warn(message );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_MISSING );
                return;
            }
            else if ( cause instanceof InvalidContentDataException )
            {
                String message = e.getMessage();
                VerticalUserServicesLogger.warn(message );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID );
                return;
            }
            else
            {
                throw cause;
            }
        }

        if ( updateContentResult.isAnyChangesMade() )
        {
            new PageCacheInvalidatorForContent( siteCachesService ).invalidateForContent( updateContentResult.getTargetedVersion() );
        }

        redirectToPage( request, response, formItems );
    }

    protected UpdateContentCommand parseUpdateContentCommand( UserEntity user, ExtendedMap formItems, boolean modifyMode )
    {
        int contentKey = formItems.getInt( "key", -1 );

        if ( contentKey == -1 )
        {
            contentKey = formItems.getInt( "contentkey", -1 );
        }

        if ( contentKey == -1 )
        {
            throw new UserServicesException( ERR_PARAMETERS_MISSING );
        }

        ContentEntity persistedContent = contentDao.findByKey( new ContentKey( contentKey ) );

        if ( persistedContent == null || persistedContent.isDeleted() )
        {
            String message = "Content with key " + contentKey + " not found";
            VerticalUserServicesLogger.warn(message );
            throw new UserServicesException( ERR_OPERATION_HANDLER );
        }

        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();

        UpdateContentCommand updateContentCommand = UpdateContentCommand.storeNewVersionEvenIfUnchanged(
                persistedVersion.getKey() );
        updateContentCommand.setModifier( user );
        updateContentCommand.setLanguage( persistedContent.getLanguage() );
        updateContentCommand.setPriority( persistedContent.getPriority() == null ? 0 : persistedContent.getPriority() );
        updateContentCommand.setContentKey( persistedContent.getKey() );
        updateContentCommand.setOwner( persistedContent.getOwner().getKey() );
        updateContentCommand.setUpdateAsMainVersion( true );
        updateContentCommand.setAvailableFrom( persistedContent.getAvailableFrom() );
        updateContentCommand.setAvailableTo( persistedContent.getAvailableTo() );
        updateContentCommand.setStatus( ContentStatus.APPROVED );
        updateContentCommand.setSyncAccessRights( false );

        ContentTypeEntity contentType = persistedContent.getContentType();
        CustomContentDataFormParser customContentParser = new CustomContentDataFormParser( contentType.getContentTypeConfig(), formItems );
        if ( modifyMode )
        {
            customContentParser.setParseOnlyCheckboxesMarkedAsInlcluded( true );
        }
        ContentData contentData = customContentParser.parseContentData();
        updateContentCommand.setContentData( contentData );

        return updateContentCommand;
    }

}
