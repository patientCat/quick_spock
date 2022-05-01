package domain.areacode.model;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author luke
 * @classname AreaCodeDomain
 * @date 2022/3/28 4:16 下午
 * {@link test.domain.areacode.AreaCodeSpec#测试正则模糊查询()}
 */


public class AreaCodeAggregate {
    private final AreaCodeModel areaCodeModel;

    private final AreaCodeMaskModel areaCodeMaskModel;

    private AreaCodeQueryPatternSupport areaCodeQueryPatternSupport;

    public AreaCodeAggregate(String code, String mask, AreaCodeQueryPatternSupport areaCodeQueryPatternSupport){
        this.areaCodeModel =AreaCodeModel.of(code);
        this.areaCodeMaskModel =AreaCodeMaskModel.of(mask);
        this.areaCodeQueryPatternSupport = areaCodeQueryPatternSupport;
    }

    public List<String> generatePattern(){
        List<String> queryPatternList = Lists.newArrayList();

       AreaCodeMaskTypeEnum[] areaCodeMaskTypeEnumList =AreaCodeMaskTypeEnum.values();
        for(AreaCodeMaskTypeEnum areaCodeMaskTypeEnum : areaCodeMaskTypeEnumList){
            if(areaCodeMaskModel.isMask(areaCodeMaskTypeEnum)) {
               AreaCodeQueryPattern queryPattern = areaCodeQueryPatternSupport.getQueryPatternMap().get(areaCodeMaskTypeEnum);
                String pattern = queryPattern.getPattern(areaCodeModel);
                queryPatternList.add(pattern) ;
            }
        }
        return queryPatternList;
    }

    public Predicate<String> generatePatternPredicate(){
        List<String> patternList = generatePattern();
        return s -> {
            for(String pattern : patternList){
                if(s.matches(pattern)) {
                    return true;
                }
            }
            return false;
        };
    }
}
