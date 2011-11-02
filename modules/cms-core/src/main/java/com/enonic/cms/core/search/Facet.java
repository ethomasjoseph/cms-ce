package com.enonic.cms.core.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Facet
    implements Iterable<FacetEntry>
{
    private final String name;

    private final List<FacetEntry> entries;

    public Facet( String name )
    {
        this.name = name;
        this.entries = new ArrayList<FacetEntry>();
    }

    public String getName()
    {
        return name;
    }

    public int getCount()
    {
        return this.entries.size();
    }

    public void addEntry( FacetEntry entry )
    {
        this.entries.add( entry );
    }

    public Iterator<FacetEntry> iterator()
    {
        return this.entries.iterator();
    }
}
