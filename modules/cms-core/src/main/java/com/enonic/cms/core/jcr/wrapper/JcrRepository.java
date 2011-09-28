/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

public interface JcrRepository
{

    public JcrSession login();

    public void logout(JcrSession session);
}
