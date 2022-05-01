package test.domain.areacode

import domain.areacode.model.AreaCodeAggregate
import domain.areacode.model.AreaCodeModel
import domain.areacode.model.AreaCodeQueryPatternSupport
import spock.lang.Specification

class AreaCodeSpec extends Specification{
    /**
     * {@link domain.areacode.model.AreaCodeModel}
     * @return
     */
    def "测试AreaCodeModel"(){
        given: "给定正确的Model"
        def model1 = "112200"
        def model2 = "112990"
        def model3 = "123311"

        when : "进行测试"
        def result1 = AreaCodeModel.of(model1)
        def result2 = AreaCodeModel.of(model2)
        def result3 = AreaCodeModel.of(model3)

        then : "验证"
        result1.getCode().size() == AreaCodeModel.VALID_CODE_LENGTH
        result1.getCode() == model1
        result2.getCode().size() == AreaCodeModel.VALID_CODE_LENGTH
        result2.getCode() == model2
        result3.getCode().size() == AreaCodeModel.VALID_CODE_LENGTH
        result3.getCode() == model3
    }

    /**
     * {@link domain.areacode.model.AreaCodeModel}
     * @return
     */
    def "测试AreaCodeModel with Where 标签"(){
        given: "给定正确的Model"
        when : "进行测试"
        def result = AreaCodeModel.of(testModel)
        def isValid = result.getCode().size() == AreaCodeModel.VALID_CODE_LENGTH
        throw new RuntimeException("")
        then :
        def exception = thrown(RuntimeException)
        exception.getMessage() == expectedMessge
        isValid == expectResult

        where : "分类测试"
        testModel || expectResult | expectedMessge
        "111111"  || true         | ""
        "101111"  || true         | ""
        "110111"  || true         | ""
        "111011"  || true         | ""
        "1111111" || null         | "非法的code"
        "11111"   || null         | "非法的code"
    }

    /**
     * {@link domain.areacode.model.AreaCodeAggregate}
     * @return
     */
    def "测试正则模糊查询"(){
        given:
        def querySupport = new AreaCodeQueryPatternSupport()
        querySupport.init()
        def areaCodeDomain = new AreaCodeAggregate(testCode, testMask, querySupport)

        when:
        def predicate = areaCodeDomain.generatePatternPredicate();
        def matches = predicate.test(dbCode)

        then:
        matches == expectedResult

        where:
        testCode | testMask | dbCode || expectedResult
        "000000" | "111" | "130130" ||true
        // 查询省
        "000000" | "100" | "110000" ||true
//        // 异常情况
        "110000" | "100" | "110000" ||true
        "110000" | "100" | "111100" ||false

        // 查询所有市
        "000000" | "010" | "102200" ||true
        "001100" | "010" | "112200" ||true
        "000000" | "010" | "110000" ||false
        "000000" | "010" | "110002" ||false

        // 查询某个11省下面的市
        "110000" | "010" | "112200" ||true
        "111100" | "010" | "112200" ||true
        "110000" | "010" | "110000" || false
        "110000" | "010" | "110102" || false

        // 查询某个11省下面的市和县
        "110000" | "011" | "112200" || true
        "111100" | "011" | "112200" || true
        "111100" | "011" | "112201" || true
    }
}
