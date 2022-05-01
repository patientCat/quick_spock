package domain.relation.model;

import com.google.common.collect.Lists;
import domain.relation.IRelationAcquisitionStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author luke
 * @classname RelationByName
 * @date 2022/4/27 5:01 下午
 */

@Data
@AllArgsConstructor
public class DatasourceRelationTreeNode {
    private String dataSourceName;

    private String dataSourceId;

    private List<DatasourceRelationTreeNode> parentDatasourceRelationTreeNodeList;

    public DatasourceRelationTreeNode(String name, String id){
        dataSourceName = name;
        dataSourceId = id;
    }

    public static Predicate<String> defaultFilter = Objects::isNull;

    public void setParentDatasourceRelationTreeNodeList(String schema, IRelationAcquisitionStrategy relationAcquisitionStrategy){
        List<RelationshipDO> relationshipList = relationAcquisitionStrategy.getRelationshipList(schema, defaultFilter);
        if(CollectionUtils.isEmpty(relationshipList)){
            return;
        }
        this.parentDatasourceRelationTreeNodeList = getParentDatasourceRelationTreeNodeList(relationshipList, relationAcquisitionStrategy);
    }

    private List<DatasourceRelationTreeNode> getParentDatasourceRelationTreeNodeList(List<RelationshipDO> relationshipDOList, IRelationAcquisitionStrategy acquisitionStrategy){
        List<DatasourceRelationTreeNode> rtnDatasourceRelationTreeNodeList = Lists.newArrayList();
        for(RelationshipDO relationshipDO : relationshipDOList){
            String dsName = relationshipDO.getParentDataSourceName();
            String dsId = relationshipDO.getParentDatasourceId();
            String schema = relationshipDO.getRelationSchema();
            DatasourceRelationTreeNode datasourceRelationTreeNode = new DatasourceRelationTreeNode(dsName, dsId);
            datasourceRelationTreeNode.setParentDatasourceRelationTreeNodeList(schema, acquisitionStrategy);
            rtnDatasourceRelationTreeNodeList.add(datasourceRelationTreeNode);
        }
        return rtnDatasourceRelationTreeNodeList;
    }


}
