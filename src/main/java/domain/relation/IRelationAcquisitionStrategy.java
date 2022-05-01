package domain.relation;

import domain.relation.model.RelationshipDO;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author luke
 */
public interface IRelationAcquisitionStrategy {

    List<RelationshipDO> getRelationshipList(String schema, Predicate<String> filter);
}
