package com.minkuh.prticl.commands.base;

import com.minkuh.prticl.data.repositories.NodeRepository;
import org.apache.commons.lang3.Validate;

import java.util.Locale;

public class CommandCommons {
    public static String isNodeNameValid(String name, NodeRepository nodeRepository) {
        var output = new StringBuilder();

        Validate.notNull(name);

        if (name.length() > 50) {
            output.append("The node name can't be over 50 characters in length!");
            output.append('\n');
        }

        if (name.isBlank()) {
            output.append("The node name can't be blank!");
            output.append('\n');
        }

        if (name.toLowerCase(Locale.ROOT).startsWith("id:")) {
            output.append("The node name can't start with 'id:'!");
            output.append('\n');
        }

        if (!nodeRepository.isNodeNameUnique(name)) {
            output.append("A node with this name already exists!");
            output.append('\n');
        }

        var result = output.toString();
        if (result.endsWith("\n")) {
            result = result.substring(0, result.length() - "\n".length());
        }

        return result;
    }

    public static double getCoordinate(String arg, double currentCoordinate) {
        if (!arg.startsWith("~"))
            return Double.parseDouble(arg);
        else {
            var withoutRelativeSymbol = arg.substring(1);
            if (withoutRelativeSymbol.isEmpty())
                withoutRelativeSymbol = "0";

            var offset = Double.parseDouble(withoutRelativeSymbol);
            return currentCoordinate + offset;
        }
    }
}