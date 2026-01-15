package com.lugr4.utils;

import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import javax.annotation.Nullable;

public class PlayerUtils {

    @Nullable
    public static PlayerRef getOnlinePlayer(String name) {
        Universe universe = Universe.get();

        PlayerRef ref = universe.getPlayerByUsername(name, NameMatching.EXACT_IGNORE_CASE);

        if (ref != null && ref.isValid()) {
            return ref;
        }
        return null;
    }
}