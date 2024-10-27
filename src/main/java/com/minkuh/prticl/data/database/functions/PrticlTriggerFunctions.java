package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.common.PaginatedResult;
import com.minkuh.prticl.data.database.entities.IPrticlEntity;
import com.minkuh.prticl.data.database.entities.Node;
import com.minkuh.prticl.data.database.entities.Player;
import com.minkuh.prticl.data.database.entities.Trigger;
import jakarta.persistence.NoResultException;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PrticlTriggerFunctions extends PrticlFunctionsBase {

    public Optional<Trigger> getById(int id) {
        return transactify(session -> {
            return Optional.of(session.find(Trigger.class, id));
        });
    }

    public Optional<Trigger> getByName(String name) {
        return transactify(session -> {
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

    public Set<Node> getNodesForTrigger(int triggerId) {
        return transactify(session -> {
            Trigger trigger = session.find(Trigger.class, triggerId);
            return trigger != null ? new HashSet<>(trigger.getNodes()) : new HashSet<>();
        });
    }

    public PaginatedResult<IPrticlEntity> getByPage(int page) {
        var startCount = page <= 1 ? 0 : (page - 1) * 10;

        var list = transactify(session -> {
            String jpql = "SELECT t FROM Trigger t ORDER BY t.id ASC";
            var query = session.createQuery(jpql, IPrticlEntity.class);

            query.setFirstResult(startCount);
            query.setMaxResults(10);

            return query.getResultList();
        });

        var count = transactify(session -> {
            String jpql = "SELECT COUNT(t) FROM Trigger t";
            var query = session.createQuery(jpql, Long.class);

            return query.getSingleResult();
        });

        var totalCount = count;

        if (count <= 0) {
            count = 0L;
        } else {
            count = (count / 10) + 1;
        }

        return new PaginatedResult<>(list, page, Math.toIntExact(count), Math.toIntExact(totalCount));
    }

    public PaginatedResult<IPrticlEntity> getByPageForPlayer(int page, Player player) {
        var startCount = page <= 1 ? 0 : (page - 1) * 10;

        var list = transactify(session -> {
            String jpql = "SELECT t FROM Trigger t WHERE t.player.uuid = :uuid ORDER BY t.id ASC";
            var query = session.createQuery(jpql, IPrticlEntity.class);

            query.setParameter("uuid", player.getUUID());
            query.setFirstResult(startCount);
            query.setMaxResults(10);

            return query.getResultList();
        });

        var count = transactify(session -> {
            String jpql = "SELECT COUNT(t) FROM Trigger t";
            var query = session.createQuery(jpql, Long.class);

            return query.getSingleResult();
        });

        var totalCount = count;

        if (count <= 0) {
            count = 0L;
        } else {
            count = (count / 10) + 1;
        }

        return new PaginatedResult<>(list, page, Math.toIntExact(count), Math.toIntExact(totalCount));
    }

    public Set<Trigger> getTriggersForNode(int nodeId) {
        return transactify(session -> {
            Node node = session.find(Node.class, nodeId);
            return node != null ? new HashSet<>(node.getTriggers()) : new HashSet<>();
        });
    }

    public Optional<Integer> getTriggerForBlock(Location location) {
        var x = location.x();
        var y = location.y();
        var z = location.z();

        var sql = "SELECT t.id FROM Trigger t WHERE t.x = :x AND t.y = :y AND t.z = :z";

        return transactify(session -> {
            var query = session.createQuery(sql, Integer.class);

            query.setParameter("x", x);
            query.setParameter("y", y);
            query.setParameter("z", z);

            var outputFromDb = query.getResultList();

            return outputFromDb.isEmpty() ? Optional.empty() : Optional.of(outputFromDb.getFirst());
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

    public boolean isTriggerNameUnique(String name) {
        return transactify(session -> {
            String hql = "SELECT name FROM Trigger t";
            var query = session.createQuery(hql, String.class);
            var queryResult = query.getResultList();

            if (queryResult.isEmpty()) return true;

            return queryResult.stream().noneMatch(dbName -> dbName.equalsIgnoreCase(name));
        });
    }
}