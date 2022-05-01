package domain.relation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luke
 * @classname DataSourceDTO
 * @date 2022/5/1 11:01 下午
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceDTO {
    private String schema;
    private String id;
    private String name;

    @Override
    public String toString(){
        return name + ","  + id + "," + schema;
    }
}
