package com.lugr4.commands.home.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.UUIDComponent; // Necesario para buscar el PlayerRef
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.lugr4.managers.HomeManager;
import com.lugr4.ui.HomeMenuGui; // Importamos tu clase de Menú

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ListHomeSubCommand extends AbstractCommand {

    public ListHomeSubCommand() {
        super("list", "Open the house menu");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {

        // --- CASO 1: ERES LA CONSOLA (DOCKER) ---
        // La consola no tiene interfaz, así que le mostramos texto plano
        if (!(context.sender() instanceof Player)) {
            var allData = HomeManager.getInstance().getAllHomes();

            if (allData.isEmpty()) {
                context.sender().sendMessage(Message.raw("[Docker] The database is empty."));
                return CompletableFuture.completedFuture(null);
            }

            context.sender().sendMessage(Message.raw("=== GLOBAL HOUSE DATABASE ==="));

            // Recorremos TODOS los jugadores para mostrarlo en la terminal
            allData.forEach((player, homes) -> {
                context.sender().sendMessage(Message.raw("Player: " + player));

                homes.forEach((homeName, loc) -> {
                    String coords = String.format("(X:%d Y:%d Z:%d)", (int)loc.x, (int)loc.y, (int)loc.z);
                    context.sender().sendMessage(Message.raw(" - " + homeName + " " + coords));
                });
            });
            context.sender().sendMessage(Message.raw("============================="));
            return CompletableFuture.completedFuture(null);
        }

        // --- CASO 2: ERES UN JUGADOR (ABRIR MENÚ) ---
        Player player = (Player) context.sender();

        // Ejecutamos en el hilo del mundo para acceder a los componentes de forma segura
        player.getWorld().execute(() -> {
            try {
                var entityRef = player.getReference();
                var store = entityRef.getStore();

                // 1. Obtenemos el componente UUID para identificar al jugador
                UUIDComponent uuidComp = (UUIDComponent) store.getComponent(entityRef, UUIDComponent.getComponentType());

                if (uuidComp != null) {
                    // 2. Buscamos la referencia en el Universo
                    PlayerRef playerRef = Universe.get().getPlayer(uuidComp.getUuid());

                    if (playerRef != null) {
                        // 3. ABRIMOS EL MENÚ DE LA LISTA
                        // Usamos la clase HomeMenuGui que creamos antes
                        player.getPageManager().openCustomPage(entityRef, store, new HomeMenuGui(playerRef));

                        context.sender().sendMessage(Message.raw("Opening house menu..."));
                    } else {
                        context.sender().sendMessage(Message.raw("Error: Could not find player reference."));
                    }
                } else {
                    context.sender().sendMessage(Message.raw("Error: UUID component missing."));
                }

            } catch (Exception e) {
                context.sender().sendMessage(Message.raw("Error opening menu: " + e.getMessage()));
                e.printStackTrace();
            }
        });

        return CompletableFuture.completedFuture(null);
    }
}