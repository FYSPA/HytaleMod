package com.lugr4.ui.controllers;

import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.lugr4.config.ConfigHome;
import com.lugr4.managers.HomeManager;

public class HomeConfigController {

    public static void build(UICommandBuilder cmd, UIEventBuilder events) {
        cmd.append("#PageContainer", "Pages/page_config.ui");

        ConfigHome config = ConfigHome.get();

        // Textos
        cmd.set("#MaxHomesLabel.Text", String.valueOf(config.defaultMaxHomes));
        cmd.set("#DelayValueLabel.Text", config.teleportDelay + "s");

        // Lógica de colores (Limpia y separada aquí)
        if (config.teleportDelayOn) {
            cmd.set("#DelayStatusLabel.Text", "ON");
            cmd.set("#DelayStatusLabel.Style.TextColor", "#00FF00");
        } else {
            cmd.set("#DelayStatusLabel.Text", "OFF");
            cmd.set("#DelayStatusLabel.Style.TextColor", "#FF0000");
        }

        // Bindings
        bind(events, "#BtnAddMax", "add_limit");
        bind(events, "#BtnSubMax", "sub_limit");
        bind(events, "#BtnAddDelay", "add_delay");
        bind(events, "#BtnSubDelay", "sub_delay");
        bind(events, "#BtnToggleDelay", "toggle_delay");
    }

    // Helper para escribir menos
    private static void bind(UIEventBuilder events, String id, String action) {
        events.addEventBinding(CustomUIEventBindingType.Activating, id, EventData.of("Action", action));
    }

    public static boolean handle(String action, Player player) {
        ConfigHome config = ConfigHome.get();
        boolean changed = false;

        switch (action) {
            case "add_limit":
                config.defaultMaxHomes++;
                changed = true;
                break;
            case "sub_limit":
                if (config.defaultMaxHomes > 1) {
                    config.defaultMaxHomes--;
                    HomeManager.getInstance().trimHomes(player.getDisplayName(), config.defaultMaxHomes);
                    changed = true;
                }
                break;
            case "add_delay":
                config.teleportDelay++;
                changed = true;
                break;
            case "sub_delay":
                if (config.teleportDelay > 0) config.teleportDelay--;
                changed = true;
                break;
            case "toggle_delay":
                config.teleportDelayOn = !config.teleportDelayOn;
                changed = true;
                break;
        }

        if (changed) {
            config.save();
        }
        return changed; // Si cambió algo, pedimos rebuild
    }
}