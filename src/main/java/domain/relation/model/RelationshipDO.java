package domain.relation.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luke
 * @classname RelationshipDO
 * @date 2022/2/14 4:53 下午
 */



@Data
@Slf4j
public class RelationshipDO {

    private String parentDataSourceName;

    private String parentDatasourceId;

    private String relationSchema;

}
