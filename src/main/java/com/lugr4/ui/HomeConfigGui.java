package com.lugr4.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player; // IMPORTANTE
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.lugr4.config.ConfigHome;
import com.lugr4.managers.HomeManager; // IMPORTANTE
import javax.annotation.Nonnull;

public class HomeConfigGui extends InteractiveCustomUIPage<HomeConfigGui.GuiData> {

    public HomeConfigGui(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, GuiData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        cmd.append("home_config.ui");
        ConfigHome config = ConfigHome.get();

        // Actualizamos los valores numéricos
        cmd.set("#MaxHomesLabel.Text", String.valueOf(config.defaultMaxHomes));
        cmd.set("#DelayValueLabel.Text", config.teleportDelay + "s");

        // --- LÓGICA DE COLOR PROFESIONAL ---
        if (config.teleportDelayOn) {
            cmd.set("#DelayStatusLabel.Text", "ON");
            cmd.set("#DelayStatusLabel.Style.TextColor", "#00FF00"); // Verde brillante
        } else {
            cmd.set("#DelayStatusLabel.Text", "OFF");
            cmd.set("#DelayStatusLabel.Style.TextColor", "#FF0000"); // Rojo brillante
        }

        // Registramos los eventos (se mantienen igual)
        events.addEventBinding(CustomUIEventBindingType.Activating, "#BtnAddMax", EventData.of("Action", "add_limit"));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#BtnSubMax", EventData.of("Action", "sub_limit"));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#BtnAddDelay", EventData.of("Action", "add_delay"));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#BtnSubDelay", EventData.of("Action", "sub_delay"));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#BtnToggleDelay", EventData.of("Action", "toggle_delay"));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#BtnClose", EventData.of("Action", "close"));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull GuiData data) {
        ConfigHome config = ConfigHome.get();
        // Obtenemos al jugador para saber su nombre si hay que borrar casas
        Player player = store.getComponent(ref, Player.getComponentType());

        // 3. Lógica de los botones
        switch (data.action) {
            case "add_limit":
                config.defaultMaxHomes++;
                break;
            case "sub_limit":
                if (config.defaultMaxHomes > 1) {
                    config.defaultMaxHomes--;
                    // Si bajamos el límite, borramos la casa que sobre
                    HomeManager.getInstance().trimHomes(player.getDisplayName(), config.defaultMaxHomes);
                }
                break;
            case "add_delay":
                config.teleportDelay++;
                break;
            case "sub_delay":
                if (config.teleportDelay > 0) config.teleportDelay--;
                break;
            case "toggle_delay":
                config.teleportDelayOn = !config.teleportDelayOn;
                break;
            case "close":
                this.close();
                return;
        }

        config.save();
        this.rebuild(); // Refresca la UI
    }

    public static class GuiData {
        public static final BuilderCodec<GuiData> CODEC = BuilderCodec.<GuiData>builder(GuiData.class, GuiData::new)
                .addField(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action)
                .build();
        private String action;
    }
}