package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.database.entities.Player;
import org.hibernate.Session;

import java.util.function.Function;

public class PrticlPlayerFunctions extends PrticlFunctionsBase {

    /**
     * Adds or updates a player in the database.
     * <p>
     * The returned player is guaranteed to always be the latest in the database.
     *
     * @param session session to use, otherwise, if passed null, it uses its own session and transaction
     * @param player  the player to persist/update and subsequently return
     * @return The latest record of the player in the database.
     */
    public Player addOrGetExistingPlayer(Session session, Player player) {
        Function<Session, Player> func = sess -> {
            var query = sess.createQuery("FROM Player p WHERE p.uuid = :playerUUID", Player.class);
            query.setParameter("playerUUID", player.getUUID());

            Player existingPlayer = query.uniqueResult();

            if (existingPlayer == null) {
                sess.persist(player);
                return player;
            } else {
                existingPlayer.setUsername(player.getUsername()); // Update username if needed
                return existingPlayer;
            }
        };

        if (session == null) {
            return transactifyAndReturn(func);
        } else {
            return func.apply(session);
        }
    }
}