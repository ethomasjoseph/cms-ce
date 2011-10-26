package com.enonic.cms.core.search;

public class FacetEntry
{
    private final String term;

    private final int count;

    public FacetEntry( String term, int count )
    {
        this.term = term;
        this.count = count;
    }

    public String getTerm()
    {
        return term;
    }

    public int getCount()
    {
        return count;
    }
}
