package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.common.wrappers.PaginatedResult;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.entities.Player;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PrticlNodeFunctions extends PrticlFunctionsBase {
    public PrticlNodeFunctions() {
    }

    public List<Node> getByWorld(UUID worldUUID) {
        return executeInTransactionWithResult(session -> {
            var query = session.createQuery("SELECT n FROM Node n WHERE n.worldUUID = :worldUUID", Node.class);

            query.setParameter("worldUUID", worldUUID);

            return query.getResultList();
        });
    }

    public List<Node> getEnabledNodes() {
        return executeInTransactionWithResult(session -> {
            var query = session.createQuery("SELECT n from Node n WHERE n.isEnabled = TRUE", Node.class);

            return query.getResultList();
        });
    }

    public PaginatedResult<Node> getByPage(int page) {
        var startCount = page <= 1 ? 0 : (page - 1) * 10;

        var list = executeInTransactionWithResult(session -> {
            String jpql = "SELECT n FROM Node n";
            var query = session.createQuery(jpql, Node.class);

            query.setFirstResult(startCount);
            query.setMaxResults(10);

            return query.getResultList();
        });

        var count = executeInTransactionWithResult(session -> {
            String jpql = "SELECT COUNT(n) FROM Node n";
            var query = session.createQuery(jpql, Long.class);

            return query.getSingleResult();
        });

        return new PaginatedResult<>(list, page, Math.toIntExact(count));
    }

    public PaginatedResult<Node> getByPageForPlayer(int page, Player player) {
        var startCount = page <= 1 ? 0 : (page - 1) * 10;

        var list = executeInTransactionWithResult(session -> {
            String jpql = "SELECT n FROM Node n WHERE n.player.uuid = :uuid";
            var query = session.createQuery(jpql, Node.class);

            query.setParameter("uuid", player.getUUID());
            query.setFirstResult(startCount);
            query.setMaxResults(10);

            return query.getResultList();
        });

        var count = executeInTransactionWithResult(session -> {
            String jpql = "SELECT COUNT(n) FROM Node n";
            var query = session.createQuery(jpql, Long.class);

            return query.getSingleResult();
        });

        return new PaginatedResult<>(list, page, Math.toIntExact(count));
    }

    public Optional<Node> getById(int id) {
        return executeInTransactionWithResult(session -> Optional.of(session.find(Node.class, id)));
    }

    public Optional<Node> getByName(String name) {
        return executeInTransactionWithResult(session -> {
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
        new PrticlPlayerFunctions().addOrUpdatePlayerViaNode(node);
        executeInTransaction(session -> session.merge(node));
    }

    public void setEnabled(Node node, boolean state) {
        executeInTransaction(session -> {
            node.setEnabled(state);
            session.merge(node);
        });
    }

    public boolean isNodeEnabled(Node node) {
        return executeInTransactionWithResult(session -> {
            var query = session.createQuery("SELECT n FROM Node n WHERE n.id = :nodeID");
            query.setParameter("nodeID", node.getId());
            Node output;

            try {
                output = (Node) query.getSingleResult();
            } catch (NoResultException nre) {
                return false;
            }

            return output != null && node.isEnabled();
        });
    }

    public boolean isNodeNameUnique(String name) {
        return executeInTransactionWithResult(session -> {
            String jpql = "SELECT name FROM Node n";
            var query = session.createQuery(jpql, String.class);
            var queryResult = query.getResultList();

            return queryResult.stream().anyMatch(dbName -> dbName.equalsIgnoreCase(name));
        });
    }
}