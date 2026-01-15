package com.lugr4.utils;

import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import javax.annotation.Nullable;

public class PlayerUtils {

    /**
     * Obtiene la entidad Player online usando la arquitectura oficial:
     * Universe -> PlayerRef -> EntityRef -> Store -> Player Component
     */
    @Nullable
    public static Player getOnlinePlayer(String name) {
        // 1. Obtenemos el Universo
        Universe universe = Universe.get();

        // 2. Buscamos el PlayerRef (La sesión del jugador)
        // Usamos la estrategia EXACT_IGNORE_CASE como indica la documentación
        PlayerRef playerRef = universe.getPlayerByUsername(name, NameMatching.EXACT_IGNORE_CASE);

        if (playerRef == null) {
            return null; // El jugador no existe o no está conectado
        }

        // 3. Obtenemos la referencia a la entidad en el mundo (ECS Reference)
        Ref<EntityStore> entityRef = playerRef.getReference();

        // 4. Verificamos si la referencia es válida (si está spawneado en un mundo)
        if (entityRef != null && entityRef.isValid()) {
            // 5. Obtenemos el "Store" (La bolsa de componentes de esa entidad)
            Store<EntityStore> store = entityRef.getStore();

            // 6. Extraemos el componente "Player" usando su tipo
            // ESTA ES LA LÍNEA CLAVE QUE FALTABA SEGÚN LA DOCS
            Player player = store.getComponent(entityRef, Player.getComponentType());

            return player;
        }

        return null;
    }
}