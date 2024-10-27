package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.common.PaginatedResult;
import com.minkuh.prticl.data.database.entities.IPrticlEntity;
import com.minkuh.prticl.data.database.entities.Node;
import com.minkuh.prticl.data.database.entities.Player;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PrticlNodeFunctions extends PrticlFunctionsBase {

    public List<Node> getByWorld(UUID worldUUID) {
        return transactify(session -> {
            var query = session.createQuery("SELECT n FROM Node n WHERE n.worldUUID = :worldUUID", Node.class);

            query.setParameter("worldUUID", worldUUID);

            return query.getResultList();
        });
    }

    public List<Node> getEnabledNodes() {
        return transactify(session -> {
            var query = session.createQuery("SELECT n from Node n WHERE n.isEnabled = TRUE", Node.class);

            return query.getResultList();
        });
    }

    public PaginatedResult<IPrticlEntity> getByPage(int page) {
        var startCount = page <= 1 ? 0 : (page - 1) * 10;

        var list = transactify(session -> {
            String jpql = "SELECT n FROM Node n ORDER BY n.id ASC";
            var query = session.createQuery(jpql, IPrticlEntity.class);

            query.setFirstResult(startCount);
            query.setMaxResults(10);

            return query.getResultList();
        });

        var count = transactify(session -> {
            String jpql = "SELECT COUNT(n) FROM Node n";
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
            String jpql = "SELECT n FROM Node n WHERE n.player.uuid = :uuid ORDER BY n.id ASC";
            var query = session.createQuery(jpql, IPrticlEntity.class);

            query.setParameter("uuid", player.getUUID());
            query.setFirstResult(startCount);
            query.setMaxResults(10);

            return query.getResultList();
        });

        var count = transactify(session -> {
            String jpql = "SELECT COUNT(n) FROM Node n";
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

    public Optional<Node> getById(int id) {
        return transactify(session -> {
            var node = session.find(Node.class, id);
            return node == null ? Optional.empty() : Optional.of(node);
        });
    }

    public Optional<Node> getByName(String name) {
        return transactify(session -> {
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

    public boolean isNodeEnabled(Node node) {
        return transactify(session -> {
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
        return transactify(session -> {
            String jpql = "SELECT name FROM Node n";
            var query = session.createQuery(jpql, String.class);
            var queryResult = query.getResultList();

            return queryResult.stream().noneMatch(dbName -> dbName.equalsIgnoreCase(name));
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
}