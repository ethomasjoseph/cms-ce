package com.enonic.cms.admin.locale;

import java.util.ArrayList;
import java.util.List;

public class LocalesModel
{

    private int total;

    private List<LocaleModel> locales = new ArrayList<LocaleModel>(  );

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public List<LocaleModel> getLocales()
    {
        return locales;
    }

    public void addLocale( LocaleModel locale )
    {
        locales.add( locale );
    }
}
