package com.lugr4.ui.controllers;

import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.lugr4.managers.HomeManager;
import com.lugr4.ui.HomeMenuGui; // Para cerrar el menú
import com.lugr4.utils.TeleportUtils;

import java.util.Map;

public class HomeListController {

    public static void build(Ref<EntityStore> ref, UICommandBuilder cmd, UIEventBuilder events, Store<EntityStore> store) {
        // Inyectamos la estructura visual
        cmd.append("#PageContainer", "Pages/page_homelist.ui");

        Player player = store.getComponent(ref, Player.getComponentType());
        var myHomes = HomeManager.getInstance().getAllHomes().get(player.getDisplayName());

        if (myHomes == null || myHomes.isEmpty()) return;

        int index = 0;
        for (Map.Entry<String, HomeManager.HomeLocation> entry : myHomes.entrySet()) {
            String homeName = entry.getKey();
            var loc = entry.getValue();

            // ID Único
            String rowId = "#HomeListScroll[" + index + "]";

            cmd.append("#HomeListScroll", "Components/row_home.ui");
            cmd.set(rowId + " #HomeName.Text", homeName);

            String coords = String.format("X:%.0f Y:%.0f Z:%.0f", loc.x, loc.y, loc.z);
            cmd.set(rowId + " #HomeCoords.Text", coords);

            events.addEventBinding(CustomUIEventBindingType.Activating, rowId + " #BtnTP", EventData.of("Action", "tp:" + homeName));
            events.addEventBinding(CustomUIEventBindingType.Activating, rowId + " #BtnDel", EventData.of("Action", "del:" + homeName));

            index++;
        }
    }

    // Retorna true si hay que recargar la pantalla (rebuild)
    public static boolean handle(String action, Player player, HomeMenuGui menu) {
        if (action.startsWith("tp:")) {
            String homeName = action.split(":")[1];
            menu.closeMenu();
            TeleportUtils.teleportToHome(player, homeName);
            return false;
        }

        if (action.startsWith("del:")) {
            String homeName = action.split(":")[1];
            HomeManager.getInstance().deleteHome(player.getDisplayName(), homeName);
            return true; // Necesitamos rebuild para actualizar la lista
        }

        return false;
    }
}