package com.minkuh.prticl.events;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.NodeSpawnManager;
import com.minkuh.prticl.data.repositories.NodeRepository;
import com.minkuh.prticl.data.repositories.TriggerRepository;
import org.bukkit.block.data.Powerable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractEventListener implements Listener {
    private final TriggerRepository triggerRepository;
    private final NodeRepository nodeRepository;
    private final NodeSpawnManager spawnManager;

    public PlayerInteractEventListener(Prticl prticl) {
        this.triggerRepository = new TriggerRepository(prticl.getLogger());
        this.nodeRepository = new NodeRepository(prticl.getLogger());
        this.spawnManager = new NodeSpawnManager(prticl);
    }

    @EventHandler
    public void onTriggerBlockInteraction(@NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() == null)
            return;

        if (event.getAction() == Action.PHYSICAL || event.getAction().isRightClick() && event.getClickedBlock().getBlockData() instanceof Powerable) {
            var triggerOpt = triggerRepository.getByLocation(event.getClickedBlock().getLocation(), true);

            if (triggerOpt.isEmpty())
                return;

            for (var node : triggerOpt.get().getNodes()) {
                node.setEnabled(true);
                nodeRepository.setSpawned(node, true);
                spawnManager.spawnNode(node);
            }
        }
    }
}