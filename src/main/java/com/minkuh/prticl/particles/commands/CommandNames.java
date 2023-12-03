package com.minkuh.prticl.particles.commands;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Obsolete
public class CommandNames {
    public static List<String> commandNames = new ArrayList<String>() {{
       commandNames.add("spawn");
       commandNames.add("line");
    }};
}
