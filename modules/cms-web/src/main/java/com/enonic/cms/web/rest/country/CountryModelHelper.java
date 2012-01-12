package com.enonic.cms.web.rest.country;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.cms.core.country.Country;

abstract class CountryModelHelper
{
    public static CountryModel toModel( final Country item )
    {
        Preconditions.checkNotNull( item );

        final CountryModel model = new CountryModel();
        model.setCode( item.getCode().toString() );
        model.setEnglishName( item.getEnglishName() );
        model.setLocalName( item.getLocalName() );
        model.setRegionsEnglishName( item.getRegionsEnglishName() );
        model.setRegionsLocalName( item.getRegionsLocalName() );
        model.setCallingCode( item.getCallingCode() );
        model.setRegions( RegionModelHelper.toModelList( item.getRegions() ) );

        return model;
    }

    public static Collection<CountryModel> toModelList( final Collection<Country> items )
    {
        Preconditions.checkNotNull( items );

        final ArrayList<CountryModel> result = Lists.newArrayList();
        for ( final Country item : items )
        {
            result.add( toModel( item ) );
        }

        return result;
    }
}
