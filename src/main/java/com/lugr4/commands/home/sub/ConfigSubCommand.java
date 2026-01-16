package com.lugr4.commands.home.sub;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.lugr4.ui.HomeMenuGui;

import java.util.concurrent.CompletableFuture;

public class ConfigSubCommand extends AbstractAsyncCommand {

    public ConfigSubCommand() {
        super("config", "Open the visual settings menu");
    }

    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        if (commandContext.sender() instanceof Player player) {
            var ref = player.getReference();
            if (ref != null && ref.isValid()) {
                var store = ref.getStore();
                World world = store.getExternalData().getWorld();

                // Ejecutamos en el hilo del mundo (World Thread)
                return CompletableFuture.runAsync(() -> {
                    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

                    if (playerRef != null) {
                        // 1. Instanciamos el Menú Maestro
                        HomeMenuGui menu = new HomeMenuGui(playerRef);

                        // 2. Le decimos que muestre la configuración directamente
                        menu.openConfigTab();

                        // 3. Abrimos el menú
                        player.getPageManager().openCustomPage(ref, store, menu);
                    }
                }, world);
            }
        }
        return CompletableFuture.completedFuture(null);
    }
}