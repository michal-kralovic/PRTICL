package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.CommandSender;

public abstract class PrticlCommand {
    BaseMessageComponents messageComponents = new BaseMessageComponents();

    abstract boolean command(String[] args, CommandSender sender);
    abstract String getCommandName();
}
