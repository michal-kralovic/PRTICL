package com.minkuh.prticl.nodes.logic;

/**
 * A PrticlNode-specific utility class for a vast variety of PrticlNode validation.
 */
public class PrticlNodeValidation {
    /**
     * Checks whether the input argument is considered an id. <br>
     * <i>(Note: it's considered an id when it starts with '<b>id:</b>')</i>
     *
     * @param arg The input string argument to check
     * @return TRUE if the passed argument starts with '<b>id:</b>'.
     */
    public static boolean isArgAnId(String arg) {
        return arg.startsWith("id:");
    }
}
