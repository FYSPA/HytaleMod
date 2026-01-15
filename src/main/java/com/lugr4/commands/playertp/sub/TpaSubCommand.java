package com.lugr4.commands.playertp.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.lugr4.managers.TpaManager;
import com.lugr4.utils.PlayerUtils; // <--- USAMOS TU UTILIDAD

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

        // === CORRECCIÓN: Usamos PlayerUtils ===
        Player targetPlayer = PlayerUtils.getOnlinePlayer(targetName);

        if (targetPlayer == null) {
            sender.sendMessage(Message.raw("§cEl jugador '" + targetName + "' no está conectado."));
            return CompletableFuture.completedFuture(null);
        }

        // Crear solicitud
        TpaManager.getInstance().createRequest(sender.getDisplayName(), targetPlayer.getDisplayName());

        // Avisar
        sender.sendMessage(Message.raw("§aSolicitud enviada a " + targetPlayer.getDisplayName()));
        targetPlayer.sendMessage(Message.raw("§6=================================="));
        targetPlayer.sendMessage(Message.raw("§e" + sender.getDisplayName() + " §7quiere ir hacia ti."));
        targetPlayer.sendMessage(Message.raw("§7Escribe: §b/home tpaccept"));
        targetPlayer.sendMessage(Message.raw("§7O rechaza con: §c/home tpdeny"));
        targetPlayer.sendMessage(Message.raw("§6=================================="));

        return CompletableFuture.completedFuture(null);
    }
}