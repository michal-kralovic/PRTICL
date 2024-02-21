package com.minkuh.prticl.nodes.commands;

import com.minkuh.prticl.data.PrticlLineCommandArguments;
import com.minkuh.prticl.nodes.prticl.PrticlLine;
import com.minkuh.prticl.nodes.schedulers.PrticlLineScheduler;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * A Command for handling the creation of particle lines.
 * <br>TODO: Create logic for creation of particle lines from existing saved PrticlNodes.
 */
public class PrticlLineCommand extends PrticlCommand {
    PrticlLine line = new PrticlLine();
    private final Plugin plugin;

    public PrticlLineCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (!isCommandSentByPlayer(sender))
            return true;

        if (allLineInputsAvailable(args)) {
            World world = ((Player) sender).getWorld();
            Player player = (Player) sender;

            PrticlLineCommandArguments cmdArgsObject = turnIntoCommandArgumentsObject(args);
            try {
                line.setLoc1(
                        getExactOrRelativeX(cmdArgsObject.getX1(), player),
                        getExactOrRelativeY(cmdArgsObject.getY1(), player),
                        getExactOrRelativeZ(cmdArgsObject.getZ1(), player),
                        world
                );
                line.setLoc2(
                        getExactOrRelativeX(cmdArgsObject.getX2(), player),
                        getExactOrRelativeY(cmdArgsObject.getY2(), player),
                        getExactOrRelativeZ(cmdArgsObject.getZ2(), player),
                        world
                );
                if (cmdArgsObject.getParticleDensity() != null) line.setDensity(cmdArgsObject.getParticleDensity());
            } catch (NumberFormatException e) {
                sender.sendMessage(prticlMessage.error(INCORRECT_COORDINATES_INPUT));
                return true;
            }

            drawLine(line.getLoc1(), line.getLoc2(), line.getDensity());
            return true;
        }
        sender.sendMessage(prticlMessage.error(INCORRECT_COMMAND_SYNTAX_OR_OTHER));
        return true;
    }

    /**
     * Utility method for returning the relative or the given X coordinate.
     *
     * @param arg    The given argument
     * @param player The executing Player
     * @return Relative coordinate if '~' present, else given coordinate.
     */
    private double getExactOrRelativeX(String arg, Player player) {
        if (isRelative(arg)) {
            arg = arg.equalsIgnoreCase("~") ? "~0" : arg;
            return player.getX() + Double.parseDouble(arg.substring(1));
        }
        return Double.parseDouble(arg);
    }

    /**
     * Utility method for returning the relative or the given Y coordinate.
     *
     * @param arg    The given argument
     * @param player The executing Player
     * @return Relative Y coordinate if '~' present, else given Y coordinate.
     */
    private double getExactOrRelativeY(String arg, Player player) {
        if (isRelative(arg)) {
            arg = arg.equalsIgnoreCase("~") ? "~0" : arg;
            return player.getY() + Double.parseDouble(arg.substring(1));
        }
        return Double.parseDouble(arg);
    }

    /**
     * Utility method for returning the relative or the given Z coordinate.
     *
     * @param arg    The given argument
     * @param player The executing Player
     * @return Relative Z coordinate if '~' present, else given Z coordinate.
     */
    private double getExactOrRelativeZ(String arg, Player player) {
        if (isRelative(arg)) {
            arg = arg.equalsIgnoreCase("~") ? "~0" : arg;
            return player.getZ() + Double.parseDouble(arg.substring(1));
        }
        return Double.parseDouble(arg);
    }

    /**
     * Determines whether all necessary arguments are passed.
     *
     * @param args The array of arguments to check
     * @return TRUE if all necessary args are present.
     */
    @Contract(pure = true)
    private boolean allLineInputsAvailable(String[] args) {
        return args.length >= 7;
    }

    /**
     * Determines whether the given argument is a relative coordinate.
     *
     * @param arg The given coordinate
     * @return TRUE if the given argument is a relative coordinate.
     */
    @Contract(pure = true)
    private boolean isRelative(String arg) {
        return arg.charAt(0) == '~';
    }

    /**
     * Draws a line between two points in a world.
     *
     * @param point1 The starting point
     * @param point2 The ending point
     * @param space  The amount of space to include between particles
     */
    public void drawLine(Location point1, Location point2, double space) {
        World world = point1.getWorld();

        Validate.isTrue(point2.getWorld().equals(world), MISMATCHING_WORLDS);

        double distance = point1.distance(point2);

        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);

        double length = 0;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new PrticlLineScheduler(length, distance, point1, vector, space), 0, 5);
    }

    @Contract("_ -> new")
    private static @NotNull PrticlLineCommandArguments turnIntoCommandArgumentsObject(String[] args) {
        return new PrticlLineCommandArguments(args);
    }


    public static String getCommandName() {
        return LINE_COMMAND;
    }
}
