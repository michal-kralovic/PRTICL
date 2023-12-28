package com.minkuh.prticl.particles.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil.loadConfigNodes;

public class PrticlListCommand extends PrticlCommand {
    private static Plugin plugin;
    private static FileConfiguration config;

    public PrticlListCommand(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (sender instanceof Player) {
            if (config.getConfigurationSection("particles") != null) {
                Map<String, Object> nodes = loadConfigNodes();
                List<String[]> listOfListableNodeData = new ArrayList<>();

                for (Map.Entry<String, Object> entry : nodes.entrySet()) {
                    MemorySection particle = (MemorySection) entry.getValue();

                    String[] nodeValues = new String[]{
                            particle.get("id").toString(),
                            particle.get("owner").toString(),
                            particle.get("name").toString(),
                            particle.get("particle-type").toString()
                    };

                    listOfListableNodeData.add(nodeValues);
                }

                sender.sendMessage(prticlMessage.list(listOfListableNodeData));
                return true;
            }
        }
        return false;
    }

    @Override
    String getCommandName() {
        return "list";
    }
}
