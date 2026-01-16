package com.lugr4.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.lugr4.ui.controllers.HomeConfigController;
import com.lugr4.ui.controllers.HomeListController;
import com.lugr4.ui.controllers.HomeCreateController; // Importar el nuevo controlador

import javax.annotation.Nonnull;

enum MenuPage { LIST, CONFIG, CREATE }

public class HomeMenuGui extends InteractiveCustomUIPage<HomeMenuGui.GuiData> {

    private MenuPage currentPage = MenuPage.LIST;

    // IMPORTANTE: Variable para guardar el texto mientras escribes
    public String tempHomeName = "";

    public HomeMenuGui(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, GuiData.CODEC);
    }

    public void openCreateTab() {
        this.currentPage = MenuPage.CREATE;
    }

    public void openConfigTab() {
        this.currentPage = MenuPage.CONFIG;
    }

    public void closeMenu() {
        this.close();
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        // 1. Layout Maestro
        cmd.append("Common/layout_master.ui");

        events.addEventBinding(CustomUIEventBindingType.Activating, "#NavList", EventData.of("Action", "nav_list"));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#NavConfig", EventData.of("Action", "nav_config"));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#BtnClose", EventData.of("Action", "close"));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#NavCreate", EventData.of("Action", "nav_create"));

        // 2. Delegar contenido
        if (currentPage == MenuPage.LIST) {
            HomeListController.build(ref, cmd, events, store);
        } else if (currentPage == MenuPage.CONFIG) {
            HomeConfigController.build(cmd, events);
        } else if (currentPage == MenuPage.CREATE) {
            HomeCreateController.build(cmd, events);
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull GuiData data) {
        String action = data.action;
        Player player = store.getComponent(ref, Player.getComponentType());
        boolean needRebuild = false;

        // --- NAVEGACIÓN GLOBAL ---
        if ("close".equals(action)) {
            this.close();
            return;
        }
        else if ("nav_list".equals(action)) {
            this.currentPage = MenuPage.LIST;
            needRebuild = true;
        }
        else if ("nav_config".equals(action)) {
            this.currentPage = MenuPage.CONFIG;
            needRebuild = true;
        } else if ("nav_create".equals(action)) {
            this.currentPage = MenuPage.CREATE;
            needRebuild = true;
        }

        // --- DELEGAR A CONTROLADORES (Si no es navegación) ---
        else {
            if (currentPage == MenuPage.LIST) {
                // Pasamos 'this' para que el controlador pueda cerrar el menú
                needRebuild = HomeListController.handle(action, player, this);
            }
            else if (currentPage == MenuPage.CONFIG) {
                needRebuild = HomeConfigController.handle(action, player);
            }
            else if (currentPage == MenuPage.CREATE) {
                // Pasamos 'data' (para el input) y 'this' (para la variable temp)
                boolean close = HomeCreateController.handle(data, player, this);
                if (close) {
                    this.close();
                    return; // Si cerramos, no hacemos rebuild
                }
                // Si estamos escribiendo (return false), no necesitamos rebuild visual inmediato
            }
        }

        // Refrescar solo si es necesario (cambio de página o dato actualizado)
        if (needRebuild) {
            this.rebuild();
        }
    }

    public static class GuiData {
        public static final BuilderCodec<GuiData> CODEC = BuilderCodec.<GuiData>builder(GuiData.class, GuiData::new)
                .addField(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action)
                // Campo para el Input de Texto
                .addField(new KeyedCodec<>("InputValue", Codec.STRING), (d, s) -> d.inputValue = s, d -> d.inputValue)
                .build();

        public String action;
        public String inputValue;
    }
}