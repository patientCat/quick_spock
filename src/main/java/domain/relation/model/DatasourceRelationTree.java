package domain.relation.model;

import domain.relation.IRelationAcquisitionStrategy;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luke
 * @classname DatasourceRelationTree
 * @date 2022/4/27 8:06 下午
 * {@link test.domain.relation.DatasourceRelationTreeSpec}
 * {@link DatasourceRelationTreeNode}
 */

public class DatasourceRelationTree {
    @Getter
    private final DatasourceRelationTreeNode head;

    private DatasourceRelationTree(DatasourceRelationTreeNode tree){
        this.head = tree;
    }
    /**
     * 递归的拿到数据源的关联关系
     * @param dataSourceDTO
     * @return
     */
    public static DatasourceRelationTree create(final DataSourceDTO dataSourceDTO, IRelationAcquisitionStrategy acquisitionStrategy){
        String schema = dataSourceDTO.getSchema();
        String dataSourceName = dataSourceDTO.getName();
        String dataSourceId = dataSourceDTO.getId();
        DatasourceRelationTreeNode headNode = new DatasourceRelationTreeNode(dataSourceName, dataSourceId);
        headNode.setParentDatasourceRelationTreeNodeList(schema, acquisitionStrategy);
        return new DatasourceRelationTree(headNode);
    }

    public static List<List<String>> levelOrder(DatasourceRelationTree tree) {
        return levelOrder(tree.getHead());
    }

    private static List<List<String>> levelOrder(DatasourceRelationTreeNode root) {
        List<List<String>> list = new LinkedList<>();
        if (root == null) {
            return list;
        }
        ArrayDeque<DatasourceRelationTreeNode> deque = new ArrayDeque<>();
        deque.addLast(root);

        while (!deque.isEmpty()) {
            int num = deque.size();
            List<String> subList = new LinkedList<>();
            for (int i = 0; i < num; i++) {
                DatasourceRelationTreeNode node = deque.removeFirst();
                if(CollectionUtils.isNotEmpty(node.getParentDatasourceRelationTreeNodeList())){
                    for(DatasourceRelationTreeNode parentNode : node.getParentDatasourceRelationTreeNodeList()){
                        deque.addLast(parentNode);
                    }
                }
                subList.add(node.getDataSourceName());
            }
            list.add(subList);
        }
        return list;
    }

    public static List<String> flatMap(List<List<String>> levelDsNameList){
        return levelDsNameList.stream().flatMap(Collection::stream).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
