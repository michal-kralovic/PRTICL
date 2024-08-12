package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.database.entities.Node;
import com.minkuh.prticl.data.database.entities.Trigger;
import jakarta.persistence.NoResultException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PrticlTriggerFunctions extends PrticlFunctionsBase {

    public Optional<Trigger> getById(int id) {
        return transactifyAndReturn(session -> Optional.of(session.find(Trigger.class, id)));
    }

    public Optional<Trigger> getByName(String name) {
        return transactifyAndReturn(session -> {
            String jpql = "SELECT n FROM Trigger n WHERE n.name = :name";
            var query = session.createQuery(jpql, Trigger.class);
            query.setParameter("name", name);

            try {
                return Optional.of(query.getSingleResult());
            } catch (NoResultException ex) {
                return Optional.empty();
            }
        });
    }

    public void add(Trigger trigger) {
        transactify(session -> {
            var player = new PrticlPlayerFunctions().addOrGetExistingPlayer(session, trigger.getPlayer());
            trigger.setPlayer(player);
            session.merge(trigger);
        });
    }

    public void addNodeToTrigger(Trigger trigger, Node node) {
        transactify(session -> {
            if (node != null && trigger != null) {
                node.getTriggers().add(trigger);
                trigger.getNodes().add(node);

                session.merge(node);
                session.merge(trigger);
            }
        });
    }

    public void removeNodeFromTrigger(int nodeId, int triggerId) {
        transactify(session -> {
            Node node = session.find(Node.class, nodeId);
            Trigger trigger = session.find(Trigger.class, triggerId);

            if (node != null && trigger != null) {
                node.getTriggers().remove(trigger);
                trigger.getNodes().remove(node);
                session.merge(node);
                session.merge(trigger);
            }
        });
    }

    public Set<Node> getNodesForTrigger(int triggerId) {
        return transactifyAndReturn(session -> {
            Trigger trigger = session.find(Trigger.class, triggerId);
            return trigger != null ? new HashSet<>(trigger.getNodes()) : new HashSet<>();
        });
    }

    public Set<Trigger> getTriggersForNode(int nodeId) {
        return transactifyAndReturn(session -> {
            Node node = session.find(Node.class, nodeId);
            return node != null ? new HashSet<>(node.getTriggers()) : new HashSet<>();
        });
    }

    public boolean isTriggerNameUnique(String name) {
        return transactifyAndReturn(session -> {
            String hql = "SELECT name FROM Trigger t";
            var query = session.createQuery(hql, String.class);
            var queryResult = query.getResultList();

            if (queryResult.isEmpty()) return true;

            return queryResult.stream().noneMatch(dbName -> dbName.equalsIgnoreCase(name));
        });
    }
}