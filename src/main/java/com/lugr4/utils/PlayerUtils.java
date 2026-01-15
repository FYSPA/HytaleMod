package com.lugr4.utils;

import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerUtils {

    @Nullable
    public static Player getOnlinePlayer(String name) {
        // 1. Obtenemos el Universo
        Universe universe = Universe.get();

        // 2. Buscamos la referencia (rápido y seguro para obtener el UUID)
        PlayerRef ref = universe.getPlayerByUsername(name, NameMatching.EXACT_IGNORE_CASE);

        if (ref != null && ref.isValid()) {
            UUID targetUuid = ref.getUuid();

            // === AQUÍ ESTÁ LA CORRECCIÓN ===

            // CORRECCIÓN 1: Si getWorlds() no existe, usamos getWorldMap().values()
            // Esto nos da la colección de mundos cargados.
            for (World world : universe.getWorldMap().values()) {

                // CORRECCIÓN 2: Si getPlayers() no existe, usamos getEntities(Clase)
                // Esto filtra todas las entidades del mundo y nos da solo los Jugadores.
                for (Player p : world.getEntities(Player.class)) {

                    // Comparamos UUIDs para estar 100% seguros
                    if (p.getUuid().equals(targetUuid)) {
                        return p; // ¡Encontrado!
                    }
                }
            }
        }
        return null;
    }
}