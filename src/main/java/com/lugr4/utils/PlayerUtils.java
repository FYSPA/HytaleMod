package com.lugr4.utils;

import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import javax.annotation.Nullable;

public class PlayerUtils {

    /**
     * Busca la REFERENCIA de un jugador conectado.
     * Es seguro usar esto desde cualquier hilo (no crashea).
     */
    @Nullable
    public static PlayerRef getOnlinePlayer(String name) {
        Universe universe = Universe.get();
        // NameMatching.EXACT_IGNORE_CASE permite encontrar "Luis" escribiendo "luis"
        PlayerRef ref = universe.getPlayerByUsername(name, NameMatching.EXACT_IGNORE_CASE);

        if (ref != null && ref.isValid()) {
            return ref;
        }
        return null;
    }
}