package com.minkuh.prticl.event_listeners;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.PrticlSpawner;
import com.minkuh.prticl.data.database.PrticlDatabase;
import org.bukkit.block.data.Powerable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.event.block.Action.PHYSICAL;

public class PlayerInteractEventListener implements Listener {

    private final PrticlDatabase prticlDb;
    private final PrticlSpawner prticlSpawner;

    public PlayerInteractEventListener(Prticl plugin) {
        this.prticlDb = new PrticlDatabase();
        this.prticlSpawner = new PrticlSpawner(plugin);
    }

    @EventHandler
    public void onTriggerBlockInteraction(@NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getAction() == PHYSICAL || event.getAction().isRightClick()
                && event.getClickedBlock().getBlockData() instanceof Powerable) {
            var triggerId = prticlDb.getTriggerFunctions().getTriggerForBlock(event.getClickedBlock().getLocation());

            if (triggerId.isEmpty()) {
                return;
            }

            var nodes = prticlDb.getTriggerFunctions().getNodesForTrigger(triggerId.get());

            for (var node : nodes) {
                node.setEnabled(true);
                prticlSpawner.spawnNode(node);
            }
        }
    }
}