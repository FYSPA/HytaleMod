package com.lugr4.ui.controllers;

import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.Message;
import com.lugr4.managers.HomeManager;
import com.lugr4.ui.HomeMenuGui;

public class HomeCreateController {

    public static void build(UICommandBuilder cmd, UIEventBuilder events) {
        cmd.append("#PageContainer", "Pages/page_create.ui");

        // 1. ESCUCHAR ESCRITURA (ValueChanged)
        // Cada vez que cambie el texto, lo enviamos como "InputValue"
        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                "#NameInput",
                EventData.of("InputValue", "#NameInput.Value")
        );

        // 2. BOTÓN GUARDAR (Activating)
        // Solo enviamos la acción, ya tenemos el texto guardado
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#BtnSave",
                EventData.of("Action", "save_home")
        );
    }

    public static boolean handle(HomeMenuGui.GuiData data, Player player, HomeMenuGui menu) {

        // CASO A: El jugador está escribiendo
        if (data.inputValue != null) {
            menu.tempHomeName = data.inputValue; // Guardamos en la memoria del menú
            return false;
        }

        // CASO B: El jugador pulsó GUARDAR
        if ("save_home".equals(data.action)) {
            // Leemos de la memoria del menú
            String name = menu.tempHomeName;

            if (name == null || name.trim().isEmpty()) {
                player.sendMessage(Message.raw("§cDebes escribir un nombre."));
                return false;
            }

            boolean exito = HomeManager.getInstance().setHome(player, name);

            if (exito) {
                player.sendMessage(Message.raw("§aCasa '" + name + "' creada."));
                return true; // Cerramos
            } else {
                player.sendMessage(Message.raw("§cError al guardar."));
            }
        }

        return false;
    }
}