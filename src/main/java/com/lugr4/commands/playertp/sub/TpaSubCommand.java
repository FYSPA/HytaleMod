package com.lugr4.commands.playertp.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.Universe; // Importante para búsqueda global
import com.lugr4.managers.TpaManager;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class TpaSubCommand extends AbstractCommand {

    private final RequiredArg<String> targetArg;

    public TpaSubCommand() {
        super("tpa", "Solicita teletransporte a un jugador");
        this.targetArg = withRequiredArg("jugador", "Nombre del jugador destino", ArgTypes.STRING);
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) {
            context.sender().sendMessage(Message.raw("§cLa consola no puede pedir TPA."));
            return CompletableFuture.completedFuture(null);
        }

        Player sender = (Player) context.sender();
        String targetName = targetArg.get(context);

        if (targetName.equalsIgnoreCase(sender.getDisplayName())) {
            sender.sendMessage(Message.raw("§cNo puedes enviarte TPA a ti mismo."));
            return CompletableFuture.completedFuture(null);
        }

        // === BUSCADOR DE JUGADORES GLOBAL ===
        Player targetPlayer = null;

        // Opción A: Intentar usar el método directo del Universo (si existe en tu versión)
        // targetPlayer = Universe.get().getPlayer(targetName);

        // Opción B: Búsqueda manual (Infalible, funciona en todas las versiones)
        // Recorremos todos los mundos y todos los jugadores para encontrarlo
        if (targetPlayer == null) {
            for (var world : Universe.get().getWorlds()) {
                for (var p : world.getPlayers()) {
                    if (p.getDisplayName().equalsIgnoreCase(targetName)) {
                        targetPlayer = p;
                        break;
                    }
                }
                if (targetPlayer != null) break;
            }
        }
        // ====================================

        if (targetPlayer == null) {
            sender.sendMessage(Message.raw("§cEl jugador '" + targetName + "' no está conectado."));
            return CompletableFuture.completedFuture(null);
        }

        // Crear la solicitud en el Manager
        TpaManager.getInstance().createRequest(sender.getDisplayName(), targetPlayer.getDisplayName());

        // Avisar a los dos
        sender.sendMessage(Message.raw("§aSolicitud enviada a " + targetPlayer.getDisplayName()));

        targetPlayer.sendMessage(Message.raw("§6=================================="));
        targetPlayer.sendMessage(Message.raw("§e" + sender.getDisplayName() + " §7quiere ir hacia ti."));
        targetPlayer.sendMessage(Message.raw("§7Escribe: §b/home tpaccept"));
        targetPlayer.sendMessage(Message.raw("§7O rechaza con: §c/home tpdeny"));
        targetPlayer.sendMessage(Message.raw("§6=================================="));

        return CompletableFuture.completedFuture(null);
    }
}