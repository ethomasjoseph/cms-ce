package com.enonic.cms.web.rest.country;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.cms.core.country.Region;

abstract class RegionModelHelper
{
    public static RegionModel toModel( final Region item )
    {
        Preconditions.checkNotNull( item );

        final RegionModel model = new RegionModel();
        model.setCode( item.getCode() );
        model.setEnglishName( item.getEnglishName() );
        model.setLocalName( item.getLocalName() );

        return model;
    }

    public static Collection<RegionModel> toModelList( final Collection<Region> items )
    {
        Preconditions.checkNotNull( items );

        final ArrayList<RegionModel> result = Lists.newArrayList();
        for ( final Region item : items )
        {
            result.add( toModel( item ) );
        }

        return result;
    }
}
