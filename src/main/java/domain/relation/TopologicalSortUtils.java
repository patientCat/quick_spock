package domain.relation;

import com.google.common.collect.Lists;
import domain.relation.model.DataSourceDTO;
import domain.relation.model.RelationshipDO;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author luke
 */
@Slf4j
public class TopologicalSortUtils {

    /**
     * 生成依赖图
     * @param dataSourceDTOList
     * @param relationshipFactory
     * @param filterPredicate
     * @return
     */
    public static Map<String, List<String>> generateGraphDeps(final List<DataSourceDTO> dataSourceDTOList, IRelationAcquisitionStrategy relationshipFactory, final Predicate<String> filterPredicate){
        Map<String, List<String>> graphDeps = dataSourceDTOList.stream()
                .collect(Collectors.toMap(
                        DataSourceDTO::getName,
                        templateModelDO -> {
                            String schema = templateModelDO.getSchema();
                            List<String> dependencies = relationshipFactory.getRelationshipList(schema, filterPredicate).stream().map(RelationshipDO::getParentDataSourceName).filter(Objects::nonNull).distinct().collect(Collectors.toList());

                            return dependencies;
                        }));
        log.info("graphDeps={}, models={}", graphDeps, dataSourceDTOList);
        return graphDeps;
    }

    /**
     * 拓扑排序, 每次将入度为0的DTO取出, 顺序为从父到子
     *
     * @param depsGraph 依赖关系，{自身id: 依赖的父亲id}
     * @return 排序后的id集合
     * @throws RuntimeException 错误信息
     */
    public static List<List<String>> sortByLevel(String requestId, Map<String, List<String>> depsGraph) throws RuntimeException {
        Map<String, Integer> degreeGraph = new HashMap<>(depsGraph.size());
        Map<String, List<String>> childGraph = new HashMap<>(depsGraph.size());
        Deque<String> pres = new LinkedList<>();
        for (final String nodeKey : depsGraph.keySet()) {
            List<String> deps = depsGraph.get(nodeKey);
            if (null == deps) {
                log.error("TopologicalSortUtils.sort with dependency null, requestId={}, depsGraph={}",
                        requestId, degreeGraph);
                throw new RuntimeException( "TopoSort");
            }
            if (deps.size() != 0) {
                degreeGraph.put(nodeKey, deps.size());
                deps.forEach(dep -> {
                    if (!childGraph.containsKey(dep)) {
                        childGraph.put(dep, new ArrayList<>());
                    }
                    childGraph.get(dep).add(nodeKey);
                });
            } else {
                pres.add(nodeKey);
            }
        }
        if (pres.size() == 0 && depsGraph.size() != 0) {
            log.error("TopologicalSortUtils.sort no pre node exists, requestId={}, depsGraph={}, pres={}, childGraph={}",
                    requestId, degreeGraph, pres, childGraph);
            throw new RuntimeException( "Toposort");
        }
        List<List<String>> topologicalSortList = Lists.newArrayList();
        while (pres.size() > 0) {
            List<String> presList = new ArrayList<>(pres);
            topologicalSortList.add(presList);
            int size = pres.size();
            for(int i = 0; i < size; i++){
                String node = pres.remove();
                var children = childGraph.getOrDefault(node, new ArrayList<>());
                children.forEach(child -> {
                    int inDegree = degreeGraph.get(child) - 1;
                    if (inDegree < 0) {
                        log.error("TopologicalSortUtils.sort graph, degree < 0, requestId={}, depsGraph={}, degreeGraph={}, pres={}, childGraph={}",
                                requestId, depsGraph, degreeGraph, pres, childGraph);
                        throw new RuntimeException("Toposort");
                    }
                    if (0 == inDegree) {
                        pres.add(child);
                        degreeGraph.remove(child);
                    } else {
                        degreeGraph.put(child, inDegree);
                    }
                });
            }
        }
        if (degreeGraph.size() > 0) {
            log.error("TopologicalSortUtils.sort graph not logical, exist circle, requestId={}, depsGraph={}, degreeGraph={}, pres={}, childGraph={}",
                    requestId, depsGraph, degreeGraph, pres, childGraph);
            throw new RuntimeException( "Toposort Circle");
        }
        return topologicalSortList;
    }

    /**
     * 拓扑排序
     *
     * @param depsGraph 依赖关系，{自身id: 依赖的父亲id}
     * @return 排序后的id集合
     * @throws RuntimeException 错误信息
     */
    public static List<String> sort(String requestId, Map<String, List<String>> depsGraph) throws RuntimeException {
        Map<String, Integer> degreeGraph = new HashMap<>(depsGraph.size());
        Map<String, List<String>> childGraph = new HashMap<>(depsGraph.size());
        Queue<String> pres = new LinkedList<>();
        for (final String nodeKey : depsGraph.keySet()) {
            List<String> deps = depsGraph.get(nodeKey);
            if (null == deps) {
                log.error("TopologicalSortUtils.sort with dependency null, requestId={}, depsGraph={}",
                        requestId, degreeGraph);
                throw new RuntimeException( "TopoSort");
            }
            if (deps.size() != 0) {
                degreeGraph.put(nodeKey, deps.size());
                deps.forEach(dep -> {
                    if (!childGraph.containsKey(dep)) {
                        childGraph.put(dep, new ArrayList<>());
                    }
                    childGraph.get(dep).add(nodeKey);
                });
            } else {
                pres.add(nodeKey);
            }
        }
        if (pres.size() == 0 && depsGraph.size() != 0) {
            log.error("TopologicalSortUtils.sort no pre node exists, requestId={}, depsGraph={}, pres={}, childGraph={}",
                    requestId, degreeGraph, pres, childGraph);
            throw new RuntimeException( "Toposort");
        }
        List<String> topologicalSortList = new ArrayList<>(depsGraph.size());
        while (pres.size() > 0) {
            String node = pres.remove();
            topologicalSortList.add(node);
            var children = childGraph.getOrDefault(node, new ArrayList<>());
            children.forEach(child -> {
                int inDegree = degreeGraph.get(child) - 1;
                if (inDegree < 0) {
                    log.error("TopologicalSortUtils.sort graph, degree < 0, requestId={}, depsGraph={}, degreeGraph={}, pres={}, childGraph={}",
                            requestId, depsGraph, degreeGraph, pres, childGraph);
                    throw new RuntimeException("Toposort");
                }
                if (0 == inDegree) {
                    pres.add(child);
                    degreeGraph.remove(child);
                } else {
                    degreeGraph.put(child, inDegree);
                }
            });
        }
        if (degreeGraph.size() > 0) {
            log.error("TopologicalSortUtils.sort graph not logical, exist circle, requestId={}, depsGraph={}, degreeGraph={}, pres={}, childGraph={}",
                    requestId, depsGraph, degreeGraph, pres, childGraph);
            throw new RuntimeException( "Toposort");
        }
        return topologicalSortList;
    }

}
