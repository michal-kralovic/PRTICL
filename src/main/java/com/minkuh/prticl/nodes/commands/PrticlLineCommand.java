package com.minkuh.prticl.nodes.commands;

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

            try {
                line.setLoc1(
                        getExactOrRelativeX(args[0], player),
                        getExactOrRelativeY(args[1], player),
                        getExactOrRelativeZ(args[2], player),
                        world
                );
                line.setLoc2(
                        getExactOrRelativeX(args[3], player),
                        getExactOrRelativeY(args[4], player),
                        getExactOrRelativeZ(args[5], player),
                        world
                );
                if (args.length == 7) line.setDensity(Double.parseDouble(args[6]));
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
    private boolean allLineInputsAvailable(String[] args) {
        int result = 0;
        for (int i = 1; i <= args.length; i++) {
            result++;
        }
        return result >= 7;
    }

    /**
     * Determines whether the given argument is a relative coordinate.
     *
     * @param arg The given coordinate
     * @return TRUE if the given argument is a relative coordinate.
     */
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

    public static String getCommandName() {
        return LINE_COMMAND;
    }
}
