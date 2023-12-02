package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.particles.PrticlNode;
import com.minkuh.prticl.particles.PrticlVector;
import com.minkuh.prticl.particles.schedulers.PrticlVectorScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PrticlVectorCommand extends PrticlCommand {

    PrticlVector vector = new PrticlVector();
    private Plugin plugin;

    public PrticlVectorCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {

        if (allVectorInputsAvailable(args)) {
            try {
                vector.setPositionA(Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), ((Player) sender).getWorld());
                vector.setPositionB(Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]), ((Player) sender).getWorld());
            } catch (NumberFormatException e) {
                ((Player) sender).sendMessage(messageComponents.prticlErrorMessage("Incorrect coordinates input!"));
                return true;
            }

            float density = 0.1F;
            PrticlNode nodeA = new PrticlNode();
            nodeA.setRepeatDelay(5);
            nodeA.setCreator((Player) sender);
            nodeA.setLocation(vector.getPositionA().toLocation(((Player) sender).getWorld()));
            nodeA.setParticleType(Particle.HEART);
            PrticlNode nodeB = new PrticlNode();
            nodeB.setRepeatDelay(5);
            nodeB.setCreator((Player) sender);
            nodeB.setLocation(vector.getPositionB().toLocation(((Player) sender).getWorld()));
            nodeB.setParticleType(Particle.HEART);


            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new PrticlVectorScheduler(nodeA, nodeB), 0, nodeA.getRepeatDelay());
        }

        return false;
    }

    private boolean allVectorInputsAvailable(String[] args) {
        int result = 0;
        for (int i = 1; i <= args.length; i++) {
            result++;
        }
        return result == 7;
    }

    @Override
    String getCommandName() {
        return "line";
    }

}
