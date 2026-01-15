package com.lugr4.commands.home.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.lugr4.managers.HomeManager;
import com.lugr4.config.ConfigHome;

// IMPORTS
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.math.vector.Transform;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GoHomeSubCommand extends AbstractCommand {

    private final RequiredArg<String> homeNameArg;

    public GoHomeSubCommand() {
        super("tp", "Vuelve a una casa");
        this.homeNameArg = withRequiredArg("nombre", "Nombre de la casa", ArgTypes.STRING);
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        String homeName = homeNameArg.get(context);
        String targetName = (context.sender() instanceof Player) ? ((Player)context.sender()).getDisplayName() : "Luis";

        HomeManager.HomeLocation loc = HomeManager.getInstance().getHome(targetName, homeName);

        if (loc == null) {
            context.sender().sendMessage(Message.raw("§cNo existe la casa '" + homeName + "'"));
            return CompletableFuture.completedFuture(null);
        }

        if (!(context.sender() instanceof Player)) {
            context.sender().sendMessage(Message.raw("§b[Consola] Destino: " + loc.x + ", " + loc.y + ", " + loc.z));
            return CompletableFuture.completedFuture(null);
        }

        Player player = (Player) context.sender();
        ConfigHome config = ConfigHome.get();

        // --- LÓGICA DE DELAY VISIBLE ---
        // Verificamos si está activo en la config
        if (config.isDelayEnabled() && config.getDelaySeconds() > 0) {
            int segundos = config.getDelaySeconds();

            context.sender().sendMessage(Message.raw("§ePreparando teletransporte en " + segundos + " segundos. ¡No te muevas!"));

            // Ejecutamos en un hilo aparte para hacer la cuenta regresiva
            CompletableFuture.runAsync(() -> {
                try {
                    // BUCLE DE CUENTA REGRESIVA
                    for (int i = segundos; i > 0; i--) {
                        // Enviamos mensaje (usando execute para volver al hilo principal un momento)
                        final int remaining = i;
                        player.getWorld().execute(() -> {
                            // Verificamos si sigue online
                            if (player.getReference() != null && player.getReference().isValid()) {
                                player.sendMessage(Message.raw("§7Teletransportando en " + remaining + "..."));
                            }
                        });

                        // Esperamos 1 segundo
                        TimeUnit.SECONDS.sleep(1);
                    }

                    // AL TERMINAR LA CUENTA: Teletransportamos
                    player.getWorld().execute(() -> {
                        if (player.getReference() == null || !player.getReference().isValid()) return;
                        realizarTeletransporte(player, loc, homeName);
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } else {
            // Sin delay
            player.getWorld().execute(() -> {
                realizarTeletransporte(player, loc, homeName);
            });
        }

        return CompletableFuture.completedFuture(null);
    }

    private void realizarTeletransporte(Player player, HomeManager.HomeLocation loc, String homeName) {
        try {
            var entityRef = player.getReference();
            var store = entityRef.getStore();

            // 1. Rotación
            HeadRotation headRot = store.getComponent(entityRef, HeadRotation.getComponentType());
            float yaw = 0f;
            float pitch = 0f;

            if (headRot != null) {
                try {
                    var rot = headRot.getRotation();
                    yaw = rot.getYaw();
                    pitch = rot.getPitch();
                } catch (Exception e) {}
            }

            // 2. Transform (6 argumentos)
            Transform destinationTransform = new Transform(
                    loc.x, loc.y, loc.z,
                    yaw, pitch, 0f
            );

            // 3. Teleport
            Teleport teleport = new Teleport(player.getWorld(), destinationTransform);
            store.addComponent(entityRef, Teleport.getComponentType(), teleport);

            player.sendMessage(Message.raw("§b¡Has vuelto a '" + homeName + "'!"));

        } catch (Exception e) {
            player.sendMessage(Message.raw("§cError técnico: " + e.getMessage()));
            e.printStackTrace();
        }
    }
}