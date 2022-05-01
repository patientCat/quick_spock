package test.domain.relation

import com.google.common.collect.Lists
import domain.relation.IRelationAcquisitionStrategy
import domain.relation.model.DataSourceDTO
import domain.relation.model.DatasourceRelationTreeNode
import domain.relation.model.DatasourceRelationTree
import domain.relation.model.RelationshipDO
import domain.relation.TopologicalSortUtils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * {@link domain.relation.model.DatasourceRelationTree}
 */
class DatasourceRelationTreeSpec extends Specification{
    def "测试创建关系树, 一个直线的例子"(){
        given: "mock数据, 生成A,B,C,D,E节点"
        def nodeA = getMockDataSourceDTO("nodeA", "nodeAId", "nodeASchema")
        def nodeB = getMockDataSourceDTO("nodeB", "nodeBId", "nodeBSchema")
        def nodeC = getMockDataSourceDTO("nodeC", "nodeCId", "nodeCSchema")
        def nodeD = getMockDataSourceDTO("nodeD", "nodeDId", "nodeDSchema")
        def nodeE = getMockDataSourceDTO("nodeE", "nodeEId", "nodeESchema")

        and: "Stub依赖关系 A -> B -> C -> D -> E"
        def stubRelationFactory = Stub(IRelationAcquisitionStrategy)
        stubRelationFactory.getRelationshipList(nodeA.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeB)
        stubRelationFactory.getRelationshipList(nodeB.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeC)
        stubRelationFactory.getRelationshipList(nodeC.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeD)
        stubRelationFactory.getRelationshipList(nodeD.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeE)
        stubRelationFactory.getRelationshipList(nodeE.getSchema(), _) >> Lists.newArrayList()

        when: "生成树结构"
        // 从开始节点分析
        def list = stubRelationFactory.getRelationshipList("nodeASchema", null)
        print(list)
        def dsNodeTree = DatasourceRelationTree.create(nodeA, stubRelationFactory)
        def order = DatasourceRelationTree.levelOrder(dsNodeTree)
        def flatMapList = DatasourceRelationTree.flatMap(order)

        then: "validate"
        flatMapList.size() == 5
        order.size() == 5
        print(flatMapList)
    }

    @Unroll
    def "开始节点#startNode, 树深度#expectDepth, 树中节点数目#expectNodeSize"(){
        given: "Stub依赖关系 A -> B -> C -> D -> E"
        def nodeA = getMockDataSourceDTO("nodeA", "nodeAId", "nodeASchema")
        def nodeB = getMockDataSourceDTO("nodeB", "nodeBId", "nodeBSchema")
        def nodeC = getMockDataSourceDTO("nodeC", "nodeCId", "nodeCSchema")
        def nodeD = getMockDataSourceDTO("nodeD", "nodeDId", "nodeDSchema")
        def nodeE = getMockDataSourceDTO("nodeE", "nodeEId", "nodeESchema")

        def stubRelationFactory = Stub(IRelationAcquisitionStrategy)
        stubRelationFactory.getRelationshipList(nodeA.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeB)
        stubRelationFactory.getRelationshipList(nodeB.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeC)
        stubRelationFactory.getRelationshipList(nodeC.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeD)
        stubRelationFactory.getRelationshipList(nodeD.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeE)
        stubRelationFactory.getRelationshipList(nodeE.getSchema(), _) >> Lists.newArrayList()

        when: "生成树结构"
        // 从开始节点分析
        def dsNodeTree = DatasourceRelationTree.create(startNode, stubRelationFactory)
        def depth = DatasourceRelationTree.levelOrder(dsNodeTree)
        def flatMapList = DatasourceRelationTree.flatMap(depth)

        then: "validate"
        flatMapList.size() == expectNodeSize
        depth.size() == expectDepth
        print(flatMapList)

        where:
        startNode                                               || expectDepth | expectNodeSize
        getMockDataSourceDTO("nodeA", "nodeAId", "nodeASchema") || 5           | 5
        getMockDataSourceDTO("nodeB", "nodeBId", "nodeBSchema") || 4           | 4
        getMockDataSourceDTO("nodeC", "nodeCId", "nodeCSchema") || 3           | 3
        getMockDataSourceDTO("nodeD", "nodeDId", "nodeDSchema") || 2           | 2
        getMockDataSourceDTO("nodeE", "nodeEId", "nodeESchema") || 1           | 1
    }

    def "测试创建关系树, 一个复杂的例子"(){
        given: "mock数据"
        def nodeA = getMockDataSourceDTO("nodeA", "nodeAId", "nodeASchema")
        def nodeB = getMockDataSourceDTO("nodeB", "nodeBId", "nodeBSchema")
        def nodeC = getMockDataSourceDTO("nodeC", "nodeCId", "nodeCSchema")
        def nodeD = getMockDataSourceDTO("nodeD", "nodeDId", "nodeDSchema")
        def nodeE = getMockDataSourceDTO("nodeE", "nodeEId", "nodeESchema")

        and: "spy"
        def stubRelationFactory = Stub(IRelationAcquisitionStrategy)
        stubRelationFactory.getRelationshipList(nodeA.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeB, nodeC)
        stubRelationFactory.getRelationshipList(nodeC.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeD)
        stubRelationFactory.getRelationshipList(nodeD.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeE)
        stubRelationFactory.getRelationshipList(nodeE.getSchema(), _) >> Lists.newArrayList()

        when: "生成树结构"
        def list = stubRelationFactory.getRelationshipList("nodeASchema", null)
        print(list)
        def dsNodeTree = DatasourceRelationTree.create(nodeA, stubRelationFactory)
        def order = DatasourceRelationTree.levelOrder(dsNodeTree)
        def flatMapList = DatasourceRelationTree.flatMap(order)

        then: "validate"
        flatMapList.size() == 5
        order.size() == 4
        print(flatMapList)
    }

    def getRelationShipByDatasourceDTOList(DataSourceDTO... nodes){
        List<RelationshipDO> relationshipDOList = Lists.newArrayList();
        for(DataSourceDTO node : nodes){
            relationshipDOList.add(getRelationShipByDatasourceDTO(node))
        }
        return relationshipDOList
    }
    def getRelationShipByDatasourceDTO(DataSourceDTO node){
        def relaiton = new RelationshipDO()
        relaiton.setParentDatasourceId(node.getId())
        relaiton.setParentDataSourceName(node.getName())
        relaiton.setRelationSchema(node.getSchema())
        return relaiton
    }
    def getMockDataSourceDTO(name, id, schema) {
        def node = new DataSourceDTO();
        node.setName(name)
        node.setId(id)
        node.setSchema(schema)
        return node
    }

    def "进行依赖分析"(){
        given: "mock数据"
        def nodeA = getMockDataSourceDTO("nodeA", "nodeAId", "nodeASchema")
        def nodeB = getMockDataSourceDTO("nodeB", "nodeBId", "nodeBSchema")
        def nodeC = getMockDataSourceDTO("nodeC", "nodeCId", "nodeCSchema")
        def nodeD = getMockDataSourceDTO("nodeD", "nodeDId", "nodeDSchema")
        def nodeE = getMockDataSourceDTO("nodeE", "nodeEId", "nodeESchema")

        and: "Stub 生成依赖关系"
        // A -> B
        //   -> C -> D -> E
        def stubRelationFactory = Stub(IRelationAcquisitionStrategy)
        stubRelationFactory.getRelationshipList(nodeA.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeB, nodeC)
        stubRelationFactory.getRelationshipList(nodeC.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeD)
        stubRelationFactory.getRelationshipList(nodeD.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeE)
        stubRelationFactory.getRelationshipList(nodeE.getSchema(), _) >> Lists.newArrayList()

        when: "生成树结构"
        def graphDeps = TopologicalSortUtils.generateGraphDeps(Lists.newArrayList(nodeA, nodeB, nodeC, nodeD, nodeE), stubRelationFactory, null)
        def sort = TopologicalSortUtils.sortByLevel("test", graphDeps);
        println(graphDeps)
        println(sort)

        then: "validate"
        graphDeps.get("nodeA") =~ ["nodeC", "nodeB"]
        graphDeps.get("nodeC") == ["nodeD"]
        graphDeps.get("nodeD") == ["nodeE"]
        graphDeps.get("nodeE") == []
        graphDeps.get("nodeB") == []
        graphDeps.size() == 5
    }

    def "进行依赖分析, 错误的例子"(){
        given: "mock数据"
        def nodeA = getMockDataSourceDTO("nodeA", "nodeAId", "nodeASchema")
        def nodeB = getMockDataSourceDTO("nodeB", "nodeBId", "nodeBSchema")
        def nodeC = getMockDataSourceDTO("nodeC", "nodeCId", "nodeCSchema")
        def nodeD = getMockDataSourceDTO("nodeD", "nodeDId", "nodeDSchema")
        def nodeE = getMockDataSourceDTO("nodeE", "nodeEId", "nodeESchema")

        and: "Stub 生成依赖关系，组成环"
        // A -> B
        //   -> C -> D
        //        <-
        def stubRelationFactory = Stub(IRelationAcquisitionStrategy)
        stubRelationFactory.getRelationshipList(nodeA.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeB, nodeC)
        stubRelationFactory.getRelationshipList(nodeC.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeD)
        stubRelationFactory.getRelationshipList(nodeD.getSchema(), _) >> getRelationShipByDatasourceDTOList(nodeC)
        stubRelationFactory.getRelationshipList(nodeE.getSchema(), _) >> Lists.newArrayList()

        when: "生成树结构"
        def graphDeps = TopologicalSortUtils.generateGraphDeps(Lists.newArrayList(nodeA, nodeB, nodeC, nodeD, nodeE), stubRelationFactory, null)
        def sort = TopologicalSortUtils.sortByLevel("test", graphDeps);
        println(graphDeps)
        println(sort)

        then: "validate"
        def e = thrown(RuntimeException)
        e.getMessage() == "Toposort Circle"
    }

    def "测试层次遍历A"(){
        given: "mock一个树结构"
        def head = new DatasourceRelationTreeNode("headA", "headA")
        def nodeA1 = new DatasourceRelationTreeNode("nodeA1", "nodeA1")
        def nodeA2 = new DatasourceRelationTreeNode("nodeA2", "nodeA2")
        def nodeA3 = new DatasourceRelationTreeNode("nodeA3", "nodeA3")
        def nodeA4 = new DatasourceRelationTreeNode("nodeA4", "nodeA4")
        head.setParentDatasourceRelationTreeNodeList(Lists.newArrayList(nodeA1))

        and: "生成A树"
        nodeA3.setParentDatasourceRelationTreeNodeList(Lists.newArrayList(nodeA4))
        nodeA2.setParentDatasourceRelationTreeNodeList(Lists.newArrayList(nodeA3))
        nodeA1.setParentDatasourceRelationTreeNodeList(Lists.newArrayList(nodeA2))

        when: "层次遍历"
        def order = DatasourceRelationTree.levelOrder(head)
        def listOfStrings = DatasourceRelationTree.flatMap(order)
        print(order)

        then: "validate"
        listOfStrings.size() == 5
        order.size() == 5

    }

    def "测试层次遍历A, B, C"(){
        given: "mock一个树结构"
        def head = new DatasourceRelationTreeNode("headA", "headA")
        def nodeA1 = new DatasourceRelationTreeNode("nodeA1", "nodeA1")
        def nodeA2 = new DatasourceRelationTreeNode("nodeA2", "nodeA2")
        def nodeA3 = new DatasourceRelationTreeNode("nodeA3", "nodeA3")
        def nodeA4 = new DatasourceRelationTreeNode("nodeA4", "nodeA4")
        def nodeB1 = new DatasourceRelationTreeNode("nodeB1", "nodeB1")
        def nodeB2 = new DatasourceRelationTreeNode("nodeB2", "nodeB2")
        def nodeB3 = new DatasourceRelationTreeNode("nodeB3", "nodeB3")
        def nodeB4 = new DatasourceRelationTreeNode("nodeB4", "nodeB4")
        def nodeC1 = new DatasourceRelationTreeNode("nodeC1", "nodeC1")
        def nodeC2 = new DatasourceRelationTreeNode("nodeC2", "nodeC2")
        def nodeC3 = new DatasourceRelationTreeNode("nodeC3", "nodeC3")
        def nodeC4 = new DatasourceRelationTreeNode("nodeC4", "nodeC4")

        head.setParentDatasourceRelationTreeNodeList(Lists.newArrayList(nodeA1, nodeB1, nodeC1))

        and: "生成A树"
        nodeA3.setParentDatasourceRelationTreeNodeList(Lists.newArrayList(nodeA4))
        nodeA2.setParentDatasourceRelationTreeNodeList(Lists.newArrayList(nodeA3))
        nodeA1.setParentDatasourceRelationTreeNodeList(Lists.newArrayList(nodeA2))

        and: "生成B树"
        nodeB1.setParentDatasourceRelationTreeNodeList(Lists.newArrayList(nodeB2, nodeB3, nodeB4))

        and: "生成C树"
        nodeC1.setParentDatasourceRelationTreeNodeList(Lists.newArrayList(nodeC2, nodeC3, nodeC4))


        when: "层次遍历"
        def order = DatasourceRelationTree.levelOrder(head)
        def listOfStrings = DatasourceRelationTree.flatMap(order)
        print(order)

        then: "validate"
        listOfStrings.size() == 13
        order.size() == 5

    }
}
