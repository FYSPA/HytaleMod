package com.lugr4.commands.home.sub;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.lugr4.ui.HomeMenuGui;

import java.util.concurrent.CompletableFuture;

public class MenuCreateSubCommand extends AbstractAsyncCommand {

    public MenuCreateSubCommand() {
        super("create", "Abrir menú de creación de casas");
    }

    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext context) {
        if (context.sender() instanceof Player player) {
            player.getWorld().execute(() -> {
                try {
                    var store = player.getReference().getStore();
                    UUIDComponent uuidComp = (UUIDComponent) store.getComponent(player.getReference(), UUIDComponent.getComponentType());

                    if (uuidComp != null) {
                        PlayerRef ref = Universe.get().getPlayer(uuidComp.getUuid());
                        if (ref != null) {
                            // 1. Creamos el menú
                            HomeMenuGui menu = new HomeMenuGui(ref);
                            // 2. Lo ponemos en la pestaña CREAR
                            menu.openCreateTab();
                            // 3. Lo abrimos
                            player.getPageManager().openCustomPage(player.getReference(), store, menu);
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            });
        }
        return CompletableFuture.completedFuture(null);
    }
}