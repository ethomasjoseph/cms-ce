package com.enonic.cms.core.jcr;

import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

class JcrSessionImpl
    implements JcrSession
{
    private Session session;

    private JcrRepository repository;


    JcrSessionImpl( Session session, JcrRepository repository )
    {
        this.session = session;
        this.repository = repository;
    }

    public Session getRealSession()
    {
        return session;
    }

    public JcrRepository getRepository()
    {
        return repository;
    }

    public void login()
    {

    }

    public void logout()
    {
        session.logout();
    }

    public void save()
    {
        try
        {
            session.save();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public Node getRootNode()
    {
        try
        {
            return session.getRootNode();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public Node getNode( String absPath )
    {
        try
        {
            return session.getNode( absPath );
        }
        catch ( PathNotFoundException e )
        {
            return null;
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public Node getOrCreateNode( String absPath )
    {
        try
        {
            return session.getNode( absPath );
        }
        catch ( PathNotFoundException e )
        {
            try
            {
                return session.getRootNode().addNode( absPath );
            }
            catch ( RepositoryException re )
            {
                throw new RepositoryRuntimeException( re );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public boolean nodeExists( String absPath )
    {
        try
        {
            return session.nodeExists( absPath );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void removeItem( String absPath )
    {
        try
        {
            session.removeItem( absPath );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public boolean propertyExists( String absPath )
    {
        try
        {
            return session.propertyExists( absPath );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public Property getProperty( String absPath )
    {

        try
        {
            return session.getProperty( absPath );
        }
        catch ( PathNotFoundException e )
        {
            return null;
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public String getPropertyString( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : property.getString();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public boolean getPropertyBoolean( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : property.getBoolean();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public long getPropertyLong( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : property.getLong();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public double getPropertyDouble( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : property.getDouble();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public Date getPropertyDate( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : calendarToDate( property.getDate() );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public Calendar getPropertyCalendar( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : property.getDate();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyString( String absPath, String value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyBoolean( String absPath, String value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyLong( String absPath, long value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyDouble( String absPath, double value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyDate( String absPath, Date value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( dateToCalendar( value ) );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyCalendar( String absPath, Calendar value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    private Calendar dateToCalendar( Date date )
    {
        if ( date == null )
        {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        return cal;
    }

    private Date calendarToDate( Calendar calendar )
    {
        return calendar == null ? null : calendar.getTime();
    }
}
