package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.entities.Player;
import org.hibernate.Session;

public class PrticlPlayerFunctions extends PrticlFunctionsBase {

    public Player addOrGetExistingPlayer(Session session, Player player) {
        var query = session.createQuery("FROM Player p WHERE p.uuid = :playerUUID", Player.class);
        query.setParameter("playerUUID", player.getUUID());

        Player existingPlayer = query.uniqueResult();

        if (existingPlayer == null) {
            session.persist(player);
            return player;
        } else {
            existingPlayer.setUsername(player.getUsername()); // Update username if needed
            return existingPlayer;
        }
    }
}