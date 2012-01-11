/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.event.MenuHandlerEvent;
import com.enonic.vertical.event.MenuHandlerListener;

import com.enonic.cms.core.log.LogType;
import com.enonic.cms.core.log.Table;
import com.enonic.cms.core.log.StoreNewLogEntryCommand;

public final class LogHandler
    extends BaseHandler
    implements MenuHandlerListener
{
    public void createdMenuItem( MenuHandlerEvent e )
        throws VerticalCreateException
    {

        StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setTableKey( Table.MENUITEM );
        command.setTableKeyValue( e.getMenuItemKey() );
        command.setType( LogType.ENTITY_CREATED );
        command.setUser( e.getUser().getKey() );
        command.setTitle( e.getTitle() );

        logService.storeNew( command );
    }

    public void removedMenuItem( MenuHandlerEvent e )
        throws VerticalRemoveException
    {
        StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setTableKey( Table.MENUITEM );
        command.setTableKeyValue( e.getMenuItemKey() );
        command.setType( LogType.ENTITY_REMOVED );
        command.setUser( e.getUser().getKey() );
        command.setTitle( e.getTitle() );

        logService.storeNew( command );
    }

    public void updatedMenuItem( MenuHandlerEvent e )
    {
        StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setTableKey( Table.MENUITEM );
        command.setTableKeyValue( e.getMenuItemKey() );
        command.setType( LogType.ENTITY_UPDATED );
        command.setUser( e.getUser().getKey() );
        command.setTitle( e.getTitle() );

        logService.storeNew( command );
    }
}
