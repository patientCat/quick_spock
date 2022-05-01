package domain.areacode.model;


import com.google.common.collect.Lists;
import org.apache.commons.lang3.CharUtils;

import java.util.List;

/**
 * @author luke
 * @classname AreaMaskModel
 * @date 2022/3/28 3:04 下午
 *
 */
public class AreaCodeMaskModel {
    /**
     有效的codeLength
     */
    private static final int ValidCodeLength = 3;

    private List<Integer> maskIntegerList = Lists.newArrayList();

    private AreaCodeMaskModel(List<Integer> maskList){
        this.maskIntegerList = maskList;
    }

    public static AreaCodeMaskModel of(String mask){
        List<Integer> maskIntegerList = Lists.newArrayList();
        for(int i = 0; i < ValidCodeLength; i++){
            char c = mask.charAt(i);
            maskIntegerList.add(CharUtils.toIntValue(c));
        }
        return new AreaCodeMaskModel(maskIntegerList);
    }

    public Boolean maskProvince(){
        return isMask(AreaCodeMaskTypeEnum.CODE_PROVINCE);
    }

    public Boolean maskMunicipal(){
        return isMask(AreaCodeMaskTypeEnum.CODE_MUNICIPAL);
    }

    public Boolean maskCounty(){
        return isMask(AreaCodeMaskTypeEnum.CODE_COUNTY);
    }

    public Boolean isMask(AreaCodeMaskTypeEnum maskType){
        return maskIntegerList.get(maskType.getType()).equals(1);
    }
}
