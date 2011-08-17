/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.httpservices;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.StringUtil;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.CreateContentException;
import com.enonic.cms.core.content.PageCacheInvalidatorForContent;
import com.enonic.cms.core.content.UpdateContentException;
import com.enonic.cms.core.content.UpdateContentResult;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.ContentDataParserException;
import com.enonic.cms.core.content.contentdata.ContentDataParserInvalidDataException;
import com.enonic.cms.core.content.contentdata.ContentDataParserUnsupportedTypeException;
import com.enonic.cms.core.content.contentdata.InvalidContentDataException;
import com.enonic.cms.core.content.contentdata.MissingRequiredContentDataException;
import com.enonic.cms.core.content.contentdata.custom.support.CustomContentDataFormParser;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.UserServicesService;
import com.enonic.cms.portal.PrettyPathNameCreator;

import com.enonic.cms.domain.SiteKey;

/**
 * Created by rmy - Date: Jun 24, 2009
 */
@Controller
@RequestMapping(value = "/site/**/_services/customcontent")
public class CustomContentHandlerController
    extends ContentHandlerBaseController
{
    private static final Logger LOG = LoggerFactory.getLogger( CustomContentHandlerController.class.getName() );

    private final static int ERR_UNSUPPORTED_TYPE = 101;


    @RequestMapping(value = "/modify", method = RequestMethod.GET)
    public void modify( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        handleRequest( request, response );
    }

    protected void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey, String operation )
        throws VerticalUserServicesException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException,
        ParseException
    {
        if ( operation.equals( "modify" ) )
        {
            handlerModify( request, response, formItems );
        }
    }

    @Override
    protected void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, RemoteException
    {
        User oldUser = securityService.getLoggedInPortalUser();

        int categoryKey = formItems.getInt( "categorykey", -1 );

        if ( categoryKey == -1 )
        {
            String message = "Category key not specified.";
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_MISSING_CATEGORY_KEY, null );
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
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID, null );
            return;
        }
        catch ( ContentDataParserException e )
        {
            LOG.error( StringUtil.expandString( e.getMessage(), (Object) null, e ), e );
            throw new UserServicesException( ERR_OPERATION_BACKEND );
        }
        catch ( ContentDataParserUnsupportedTypeException e )
        {
            LOG.error( StringUtil.expandString( e.getMessage(), (Object) null, e ), e );
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
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_MISSING, null );
                return;
            }
            else if ( cause instanceof InvalidContentDataException )
            {
                String message = e.getMessage();
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID, null );
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
        throws VerticalUserServicesException, RemoteException
    {
        User oldTypeUser = securityService.getOldUserObject();

        int contentKey = formItems.getInt( "key", -1 );

        if ( contentKey == -1 )
        {
            String message = "Content key not specified.";
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_MISSING_CATEGORY_KEY, null );
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
            LOG.error( StringUtil.expandString( e.getMessage(), (Object) null, e ), e );
            throw new UserServicesException( ERR_OPERATION_BACKEND );
        }
        catch ( ContentDataParserInvalidDataException e )
        {
            String message = e.getMessage();
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID, null );
            return;
        }
        catch ( ContentDataParserUnsupportedTypeException e )
        {
            LOG.error( StringUtil.expandString( e.getMessage(), (Object) null, e ), e );
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
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_MISSING, null );
                return;
            }
            else if ( cause instanceof InvalidContentDataException )
            {
                String message = e.getMessage();
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID, null );
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

        throws VerticalUserServicesException, RemoteException
    {
        User oldTypeUser = securityService.getOldUserObject();

        int contentKey = formItems.getInt( "key", -1 );

        if ( contentKey == -1 )
        {
            String message = "Content key not specified.";
            LOG.error( StringUtil.expandString( message, (Object) null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_MISSING_CATEGORY_KEY, null );
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
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID, null );
            return;
        }
        catch ( ContentDataParserException e )
        {
            LOG.error( StringUtil.expandString( e.getMessage(), (Object) null, e ), e );
            throw new UserServicesException( ERR_OPERATION_BACKEND );
        }
        catch ( ContentDataParserUnsupportedTypeException e )
        {
            LOG.error( StringUtil.expandString( e.getMessage(), (Object) null, e ), e );
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
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_MISSING, null );
                return;
            }
            else if ( cause instanceof InvalidContentDataException )
            {
                String message = e.getMessage();
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID, null );
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


    protected CreateContentCommand parseCreateContentCommand( ExtendedMap formItems )
    {
        CreateContentCommand createContentCommand = new CreateContentCommand();

        int categoryKey = formItems.getInt( "categorykey" );

        CategoryEntity category = categoryDao.findByKey( new CategoryKey( categoryKey ) );

        ContentTypeEntity contentType = category.getContentType();

        createContentCommand.setCategory( category );

        if ( category.getAutoMakeAvailableAsBoolean() )
        {
            createContentCommand.setAvailableFrom( new Date() );

            createContentCommand.setStatus( ContentStatus.APPROVED );
        }
        else
        {
            createContentCommand.setStatus( ContentStatus.DRAFT );
        }

        createContentCommand.setPriority( 0 );

        createContentCommand.setLanguage( category.getLanguage() );

        CustomContentDataFormParser customContentParser = new CustomContentDataFormParser( contentType.getContentTypeConfig(), formItems );
        ContentData contentData = customContentParser.parseContentData();
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( PrettyPathNameCreator.generatePrettyPathName( contentData.getTitle() ) );

        return createContentCommand;
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
            LOG.warn( StringUtil.expandString( message, null, null ) );
            throw new UserServicesException( ERR_OPERATION_HANDLER );
        }

        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();

        UpdateContentCommand updateContentCommand = UpdateContentCommand.storeNewVersionEvenIfUnchanged( persistedVersion.getKey() );
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
