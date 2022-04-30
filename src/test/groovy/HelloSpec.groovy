

import spock.lang.Specification

class HelloSpec extends Specification{
    def "helloworld"(){

        expect:
        name.size() == length

        where:
        name     | length
        "Spock"  | 5
        "Kirk"   | 4
        "Scotty" | 6
    }
}
