package domain.model;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luke
 * @classname AreaCodeQuerySupport
 * @date 2022/3/29 10:39 上午
 */

public class AreaCodeQueryPatternSupport {
    @Getter
    private Map<AreaCodeMaskTypeEnum, AreaCodeQueryPattern> queryPatternMap = new ConcurrentHashMap<>();
    public void init() {
        queryPatternMap.putIfAbsent(AreaCodeMaskTypeEnum.CODE_PROVINCE, new AreaCodeQueryPattern(AreaCodeMaskTypeEnum.CODE_PROVINCE));
        queryPatternMap.putIfAbsent(AreaCodeMaskTypeEnum.CODE_MUNICIPAL, new AreaCodeQueryPattern(AreaCodeMaskTypeEnum.CODE_MUNICIPAL));
        queryPatternMap.putIfAbsent(AreaCodeMaskTypeEnum.CODE_COUNTY, new AreaCodeQueryPattern(AreaCodeMaskTypeEnum.CODE_COUNTY));
    }
}
