package com.enonic.cms.admin.locale;

import java.util.Collection;
import java.util.Locale;

public class LocaleModelHelper
{
    public static LocaleModel toModel(final Locale entity)
    {
        final LocaleModel model = new LocaleModel();
        model.setId( entity.getISO3Language() );
        model.setDisplayName( entity.getDisplayName() );
        return model;
    }

    public static LocalesModel toModel(final Collection<Locale> list)
    {
        final LocalesModel model = new LocalesModel();
        model.setTotal(list.size());

        for (final Locale entity : list) {
            model.addLocale(toModel(entity));
        }

        return model;
    }
}
