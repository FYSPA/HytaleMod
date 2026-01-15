package com.lugr4.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;

// Imports basados en tu PlayerInfoHandler
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.math.vector.Vector3d;

import javax.annotation.Nonnull;

public class PositionCommand extends CommandBase {

    public PositionCommand() {
        super("pos", "Muestra tus coordenadas exactas (Debug)");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) {
            context.sender().sendMessage(Message.raw("La consola no tiene coordenadas."));
            return;
        }

        Player player = (Player) context.sender();

        // === CORRECCIÓN VITAL: CAMBIO DE HILO ===
        // Movemos la ejecución al hilo del mundo para poder leer los componentes
        player.getWorld().execute(() -> {
            try {
                var entityRef = player.getReference();
                var store = entityRef.getStore();

                // Ahora sí es seguro leer el Store
                TransformComponent transform = (TransformComponent) store.getComponent(entityRef, TransformComponent.getComponentType());

                if (transform != null) {
                    Vector3d position = transform.getPosition();

                    if (position != null) {
                        String msg = String.format("§aTu posición es: X=%.2f, Y=%.2f, Z=%.2f",
                                position.x, position.y, position.z);

                        context.sender().sendMessage(Message.raw(msg));
                    } else {
                        context.sender().sendMessage(Message.raw("Error: getPosition() es null."));
                    }
                } else {
                    context.sender().sendMessage(Message.raw("§cError: TransformComponent no encontrado."));
                }

            } catch (Exception e) {
                context.sender().sendMessage(Message.raw("§cExcepción: " + e.getMessage()));
                e.printStackTrace();
            }
        });
    }
}