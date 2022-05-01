package domain.areacode.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author luke
 * @classname AreaCodeQueryPattern
 * @date 2022/3/29 10:34 上午
 */
public class AreaCodeQueryPattern extends AreaCodeModel{

    private static final String VALID2NUM_PATTERN = "([0-9]{1}[1-9]{1}|[1-9]{1}[0-9]{1})";

    private static final String ANY_2_NUM_PATTERN = "[0-9]{2}";
    private static final String ZERO_2 = "00";

    private AreaCodeMaskTypeEnum codeEnum;

    public AreaCodeQueryPattern(AreaCodeMaskTypeEnum codeE){
        codeEnum = codeE;
        if(codeEnum == AreaCodeMaskTypeEnum.CODE_PROVINCE){
            super.province = VALID2NUM_PATTERN;
        } else if(codeEnum == AreaCodeMaskTypeEnum.CODE_MUNICIPAL){
            super.municipal = VALID2NUM_PATTERN;
        } else if(codeEnum == AreaCodeMaskTypeEnum.CODE_COUNTY){
            super.county = VALID2NUM_PATTERN;
        }
    }

    public String getPattern(){
        return getPattern(AreaCodeModel.DEFAULT_CODE);
    }

    public String getPattern(String code){
        return getPattern(AreaCodeModel.of(code));
    }

    public String getPattern(AreaCodeModel codeModel){
        if(codeEnum == AreaCodeMaskTypeEnum.CODE_PROVINCE){
            // 设置省
            // donothing
        } else if(codeEnum == AreaCodeMaskTypeEnum.CODE_MUNICIPAL){
            // 设置市
            this.province = getProvinceOrMunicipalOrCounty(this.province);
            this.municipal = VALID2NUM_PATTERN;
        } else if(codeEnum == AreaCodeMaskTypeEnum.CODE_COUNTY){
            // 设置县
            this.province = getProvinceOrMunicipalOrCounty(this.province);
            this.municipal = getProvinceOrMunicipalOrCounty(this.municipal);
            super.county = VALID2NUM_PATTERN;
        }
        List<String> stringList = Lists.newArrayList(this.province, this.municipal, this.county);
        return Joiner.on(StringUtils.EMPTY).join(stringList);
    }

    private String getProvinceOrMunicipalOrCounty(String pmc) {
        if (ZERO_2.equals(pmc)) {
            return ANY_2_NUM_PATTERN;
        } else {
            return pmc;
        }
    }
}
