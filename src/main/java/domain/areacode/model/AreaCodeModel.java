package domain.areacode.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author luke
 * @classname AreaCodeModel
 * @date 2022/3/28 2:51 下午
 * {@link test.domain.areacode.AreaCodeSpec#测试AreaCodeModel()}
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaCodeModel {
    public static final int VALID_CODE_LENGTH = 6;
    private static final int VALID_CODE_SORT = 3;
    public static final String DEFAULT_CODE = "000000";

    protected String province = "00";

    protected String municipal = "00";

    protected String county = "00";

    private static String getProvince(String code){
        return StringUtils.substring(code, 0, 2);
    }

    private static String getMunicipal(String code){
        return StringUtils.substring(code, 2, 4);
    }

    private static String getCounty(String code){
        return StringUtils.substring(code, 4, 6);
    }

    private static List<String> spiltCode2List(String code){
        List<String> codeList = Lists.newArrayList();
        codeList.add(getProvince(code));
        codeList.add(getMunicipal(code));
        codeList.add(getCounty(code));
        return codeList;
    }

    public static Boolean isValidCode(String code){
        if(code.length() != VALID_CODE_LENGTH){
            return false;
        }
        List<String> stringList = spiltCode2List(code);
        if(stringList.size() != VALID_CODE_SORT){
            return false;
        }
        return true;
    }
    public static AreaCodeModel of(String code){
        if(!isValidCode(code)){
            throw new RuntimeException("非法的code");
        }
        List<String> stringList = spiltCode2List(code);

        return new AreaCodeModel(stringList.get(0), stringList.get(1), stringList.get(2));
    }

    public String getCode(){
        List<String> codeList = Lists.newArrayList(this.province, this.municipal, this.county);
        return Joiner.on(StringUtils.EMPTY).join(codeList);
    }
}
