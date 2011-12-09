package com.enonic.cms.core.country

import spock.lang.*
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired

@ContextConfiguration
class CountryServiceImplTest extends Specification
{
    @Autowired
    CountryServiceImpl countryService

    def "test get country"()
    {
        setup:
        def country

        expect: "country service is not null"
        countryService != null

        when: "trying to get nonexisting country"
        country = countryService.getCountry new CountryCode("NO")
        then:
        country == null

        when: "trying to get existing country"
        country = countryService.getCountry new CountryCode("BB")
        then:
        country != null
        country.code.toString() == "BB"
        country.englishName == "BARBADOS"

    }

    def "test get countries"()
    {
        setup:
        def countries
        def iterator

        when: "countries should be not null"
        countries = countryService.getCountries()

        then:
        countries != null

        when: "and there should be an iterator"
        iterator = countries.iterator()
        then:
        iterator != null

        expect:
        countries.size() == 3
        iterator.next().code.toString() == "BB"
        iterator.next().code.toString() == "GY"
        iterator.next().code.toString() == "UG"
        iterator.hasNext() == false




    }
}
