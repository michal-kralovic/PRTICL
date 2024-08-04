package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.entities.Player;
import jakarta.persistence.NoResultException;

public class PrticlPlayerFunctions extends PrticlFunctionsBase {
    public PrticlPlayerFunctions() {
    }

    public void addOrUpdatePlayerViaNode(Node node) {
        executeInTransaction(session -> {
            Player player = node.getPlayer();

            var query = session.createQuery("SELECT p FROM Player p WHERE p.uuid = :playerUUID");
            query.setParameter("playerUUID", player.getUUID());

            try {
                var playerInDb = query.getSingleResult();

                if (playerInDb instanceof Player p) {
                    session.merge(p);
                    node.setPlayer(p);
                }

            } catch (NoResultException ex) {
                session.persist(player);
                node.setPlayer(session.find(Player.class, player));
            }
        });
    }
}