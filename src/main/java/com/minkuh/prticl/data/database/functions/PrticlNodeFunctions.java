package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.common.wrappers.PaginatedResult;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.entities.Player;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PrticlNodeFunctions extends PrticlFunctionsBase {

    public List<Node> getByWorld(UUID worldUUID) {
        return transactifyAndReturn(session -> {
            var query = session.createQuery("SELECT n FROM Node n WHERE n.worldUUID = :worldUUID", Node.class);

            query.setParameter("worldUUID", worldUUID);

            return query.getResultList();
        });
    }

    public List<Node> getEnabledNodes() {
        return transactifyAndReturn(session -> {
            var query = session.createQuery("SELECT n from Node n WHERE n.isEnabled = TRUE", Node.class);

            return query.getResultList();
        });
    }

    public PaginatedResult<Node> getByPage(int page) {
        var startCount = page <= 1 ? 0 : (page - 1) * 10;

        var list = transactifyAndReturn(session -> {
            String jpql = "SELECT n FROM Node n";
            var query = session.createQuery(jpql, Node.class);

            query.setFirstResult(startCount);
            query.setMaxResults(10);

            return query.getResultList();
        });

        var count = transactifyAndReturn(session -> {
            String jpql = "SELECT COUNT(n) FROM Node n";
            var query = session.createQuery(jpql, Long.class);

            return query.getSingleResult();
        });

        return new PaginatedResult<>(list, page, Math.toIntExact(count));
    }

    public PaginatedResult<Node> getByPageForPlayer(int page, Player player) {
        var startCount = page <= 1 ? 0 : (page - 1) * 10;

        var list = transactifyAndReturn(session -> {
            String jpql = "SELECT n FROM Node n WHERE n.player.uuid = :uuid";
            var query = session.createQuery(jpql, Node.class);

            query.setParameter("uuid", player.getUUID());
            query.setFirstResult(startCount);
            query.setMaxResults(10);

            return query.getResultList();
        });

        var count = transactifyAndReturn(session -> {
            String jpql = "SELECT COUNT(n) FROM Node n";
            var query = session.createQuery(jpql, Long.class);

            return query.getSingleResult();
        });

        return new PaginatedResult<>(list, page, Math.toIntExact(count));
    }

    public Optional<Node> getById(int id) {
        return transactifyAndReturn(session -> Optional.of(session.find(Node.class, id)));
    }

    public Optional<Node> getByName(String name) {
        return transactifyAndReturn(session -> {
            String jpql = "SELECT n FROM Node n WHERE n.name = :name";
            var query = session.createQuery(jpql, Node.class);
            query.setParameter("name", name);

            try {
                return Optional.of(query.getSingleResult());
            } catch (NoResultException ex) {
                return Optional.empty();
            }
        });
    }

    public void add(Node node) {
        transactify(session -> {
            var player = new PrticlPlayerFunctions().addOrGetExistingPlayer(session, node.getPlayer());
            node.setPlayer(player);
            session.merge(node);
        });
    }

    public void setEnabled(Node node, boolean state) {
        transactify(session -> {
            node.setEnabled(state);
            session.merge(node);
        });
    }

    public boolean isNodeEnabled(Node node) {
        return transactifyAndReturn(session -> {
            var query = session.createQuery("SELECT n FROM Node n WHERE n.id = :nodeID", Node.class);
            query.setParameter("nodeID", node.getId());
            Node output;

            try {
                output = query.getSingleResult();
            } catch (NoResultException nre) {
                return false;
            }

            return output != null && node.isEnabled();
        });
    }

    public boolean isNodeNameUnique(String name) {
        return transactifyAndReturn(session -> {
            String jpql = "SELECT name FROM Node n";
            var query = session.createQuery(jpql, String.class);
            var queryResult = query.getResultList();

            return queryResult.stream().anyMatch(dbName -> dbName.equalsIgnoreCase(name));
        });
    }
}