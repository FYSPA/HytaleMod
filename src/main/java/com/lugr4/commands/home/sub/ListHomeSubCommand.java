package com.lugr4.commands.home.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.lugr4.managers.HomeManager;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ListHomeSubCommand extends AbstractCommand {

    public ListHomeSubCommand() {
        super("list", "Muestra la lista de tus casas guardadas");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        // Obtenemos todas las casas del Manager
        var allData = HomeManager.getInstance().getAllHomes();

        // --- CASO 1: ERES UN JUGADOR ---
        if (context.sender() instanceof Player) {
            Player player = (Player) context.sender();
            String playerName = player.getDisplayName();

            // Buscamos solo TUS casas
            Map<String, HomeManager.HomeLocation> myHomes = allData.get(playerName);

            if (myHomes == null || myHomes.isEmpty()) {
                context.sender().sendMessage(Message.raw("§cNo tienes ninguna casa guardada."));
                return CompletableFuture.completedFuture(null);
            }

            context.sender().sendMessage(Message.raw("§6=== Tus Casas (" + myHomes.size() + ") ==="));
            myHomes.forEach((homeName, loc) -> {
                // Formato: - Casa (X: 100, Y: 60, Z: 100)
                String coords = String.format("§7(X:%d Y:%d Z:%d)", (int)loc.x, (int)loc.y, (int)loc.z);
                context.sender().sendMessage(Message.raw("§e- " + homeName + " " + coords));
            });
        }

        // --- CASO 2: ERES LA CONSOLA (DOCKER) ---
        else {
            if (allData.isEmpty()) {
                context.sender().sendMessage(Message.raw("§c[Docker] La base de datos (homes.json) está vacía."));
                return CompletableFuture.completedFuture(null);
            }

            context.sender().sendMessage(Message.raw("§6=== DATABASE DE CASAS (GLOBAL) ==="));

            // Recorremos TODOS los jugadores
                allData.forEach((player, homes) -> {
                context.sender().sendMessage(Message.raw("§bJugador: " + player));

                homes.forEach((homeName, loc) -> {
                    String coords = String.format("(X:%d Y:%d Z:%d)", (int)loc.x, (int)loc.y, (int)loc.z);
                    context.sender().sendMessage(Message.raw("  §e- " + homeName + " §7" + coords));
                });
            });
            context.sender().sendMessage(Message.raw("§6=================================="));
        }

        return CompletableFuture.completedFuture(null);
    }
}