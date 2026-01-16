package com.lugr4.utils;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.lugr4.managers.HomeManager;

// Imports de movimiento
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.component.Component; // Para el cast

public class TeleportUtils {

    /**
     * Teletransporta a un jugador a una casa específica.
     * Se encarga de los hilos, la rotación y la seguridad.
     */
    public static void teleportToHome(Player player, String homeName) {
        // 1. Buscar la casa en el Manager
        HomeManager.HomeLocation loc = HomeManager.getInstance().getHome(player.getDisplayName(), homeName);

        if (loc == null) {
            player.sendMessage(Message.raw("§cError: No se encontró la casa '" + homeName + "'"));
            return;
        }

        // 2. Ejecutar en el hilo del mundo (Seguridad)
        player.getWorld().execute(() -> {
            try {
                var entityRef = player.getReference();
                var store = entityRef.getStore();

                // 3. Preservar Rotación (Yaw/Pitch)
                HeadRotation headRot = store.getComponent(entityRef, HeadRotation.getComponentType());
                float yaw = 0f;
                float pitch = 0f;

                if (headRot != null) {
                    try {
                        var rot = headRot.getRotation();
                        yaw = rot.getYaw();
                        pitch = rot.getPitch();
                    } catch (Exception ignored) {}
                }

                // 4. Crear Transform (Posición Nueva + Rotación Vieja)
                // Constructor: X, Y, Z, Yaw, Pitch, Roll
                Transform destinationTransform = new Transform(
                        loc.x,
                        loc.y,
                        loc.z,
                        yaw,
                        pitch,
                        0f
                );

                // 5. Crear y Aplicar Teleport
                Teleport teleport = new Teleport(player.getWorld(), destinationTransform);

                // Hacemos el cast a (Component) por si acaso
                ((com.hypixel.hytale.component.Store) store).addComponent(entityRef, Teleport.getComponentType(), (Component) teleport);

                player.sendMessage(Message.raw("§b¡Teletransportado a '" + homeName + "'!"));

            } catch (Exception e) {
                player.sendMessage(Message.raw("§cError técnico al teletransportar: " + e.getMessage()));
                e.printStackTrace();
            }
        });
    }
}