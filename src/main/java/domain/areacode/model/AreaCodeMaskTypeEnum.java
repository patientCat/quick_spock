package domain.areacode.model;

import lombok.Getter;

public enum AreaCodeMaskTypeEnum {

    // 省
    CODE_PROVINCE(0),
    // 市
    CODE_MUNICIPAL(1),
    // 县
    CODE_COUNTY(2),
    ;

    AreaCodeMaskTypeEnum(Integer type){
        this.type = type;
    }

    @Getter
    private final Integer type;
}
