package com.enonic.cms.api.client.model.user

import spock.lang.*

class UserInfoTest extends Specification {

    def "we can test address stuff here"() {
        setup:
        def a1 = new Address()
        def a2 = new Address()
        def a3 = new Address()

        def info = new UserInfo()

        expect:
        info.addresses.length == 0
        info.primaryAddress == null

        when:
        info.addresses = a1
        then:
        info.addresses.length == 1
        info.primaryAddress == a1

        when:
        info.setAddresses a2, a3, a1
        then:
        info.addresses.length == 3
        info.primaryAddress == a2


    }
}
