package com.enonic.cms.core.country

import spock.lang.*
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired

@ContextConfiguration
class CountryServiceImplTest extends Specification
{
    @Autowired
    CountryServiceImpl countryService

    def "spring context test"()
    {
        expect:
        countryService != null
    }
}
