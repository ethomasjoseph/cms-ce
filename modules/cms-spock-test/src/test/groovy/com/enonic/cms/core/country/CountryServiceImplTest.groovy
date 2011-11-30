package com.enonic.cms.core.country

import spock.lang.*
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired

@ContextConfiguration( locations = "CountryServiceImplTest.xml")
class CountryServiceImplTest extends Specification
{
    @Autowired
    CountryService countryService

    def "spring context test"()
    {
        expect:
        countryService != null
    }
}
