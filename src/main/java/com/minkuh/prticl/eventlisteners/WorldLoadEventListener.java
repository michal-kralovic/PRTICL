package com.minkuh.prticl.eventlisteners;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.nodes.schedulers.PrticlScheduler;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.sql.SQLException;
import java.util.List;

public class WorldLoadEventListener implements Listener {
    private final Prticl plugin;
    private final PrticlDatabase prticlDatabase;

    public WorldLoadEventListener(Prticl plugin) throws SQLException {
        this.plugin = plugin;
        this.prticlDatabase = new PrticlDatabase(plugin);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) throws SQLException {
        Bukkit.getServer().sendMessage(new BaseMessageComponents().player("Triggered onWorldLoad!"));
        System.out.println("Triggered onWorldLoad!");
        World world = event.getWorld();
        List<PrticlNode> filteredNodeList = prticlDatabase.getNodeFunctions().getNodesListByWorld(world);

        for (PrticlNode node : filteredNodeList){
            Bukkit.getServer().sendMessage(new BaseMessageComponents().player("Loading a node!"));
            System.out.println("Loading a node!");
            Bukkit.getServer().sendMessage(new BaseMessageComponents().player("In world: " + world.getName()));
            System.out.println("In world: " + world.getName());
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new PrticlScheduler(node), 0, node.getRepeatDelay());

        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) throws SQLException {
        // TODO: Add the de-spawning of nodes of the unloading world
    }
}
