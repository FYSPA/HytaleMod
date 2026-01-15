package com.lugr4.commands.home.sub;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.lugr4.ui.HomeConfigGui;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;

import java.util.concurrent.CompletableFuture;

public class ConfigSubCommand extends AbstractAsyncCommand {

    public ConfigSubCommand() {
        super("config", "Abre el menu de configuracion visual");
    }

    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        if (commandContext.sender() instanceof Player player) {
            var ref = player.getReference();
            if (ref != null && ref.isValid()) {
                var store = ref.getStore();
                World world = store.getExternalData().getWorld();

                // Abrimos la página en el hilo del mundo (World Thread)
                return CompletableFuture.runAsync(() -> {
                    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                    if (playerRef != null) {
                        player.getPageManager().openCustomPage(ref, store, new HomeConfigGui(playerRef));
                    }
                }, world);
            }
        }
        return CompletableFuture.completedFuture(null);
    }
}