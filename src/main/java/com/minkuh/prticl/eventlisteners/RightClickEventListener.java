package com.minkuh.prticl.eventlisteners;

import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class RightClickEventListener implements Listener {

    BaseMessageComponents messageComponents = new BaseMessageComponents();

    public RightClickEventListener() {
    }

    @EventHandler
    public void onRightClick(@NotNull PlayerInteractEvent event) {
        if (isMainHandRightClick(event)) {
            event.getPlayer().sendMessage(messageComponents.prticlSystemMessage("You used your RMB for something!"));
        }
    }

    private boolean isMainHandRightClick(@NotNull PlayerInteractEvent event) {
        return event.getAction().isRightClick() && event.getHand() == EquipmentSlot.HAND;
    }
}
