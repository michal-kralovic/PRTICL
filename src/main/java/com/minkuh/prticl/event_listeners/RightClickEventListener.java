package com.minkuh.prticl.event_listeners;

import com.minkuh.prticl.common.PrticlMessages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class RightClickEventListener implements Listener {

    private final PrticlMessages messageComponents = new PrticlMessages();

    @EventHandler
    public void onRightClick(@NotNull PlayerInteractEvent event) {
        if (isMainHandRightClick(event)) {
            event.getPlayer().sendMessage(messageComponents.system("You used your RMB for something!"));
        }
    }

    private boolean isMainHandRightClick(@NotNull PlayerInteractEvent event) {
        return event.getAction().isRightClick() && event.getHand() == EquipmentSlot.HAND;
    }
}