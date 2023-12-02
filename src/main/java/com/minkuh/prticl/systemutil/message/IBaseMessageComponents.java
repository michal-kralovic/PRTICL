package com.minkuh.prticl.systemutil.message;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

interface IBaseMessageComponents {
    @NotNull TextComponent prticlDash();
    @NotNull TextComponent prticlMessage(String message, int color);

}
