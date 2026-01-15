package com.lugr4.commands.playertp.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.lugr4.managers.TpaManager;
import com.lugr4.utils.PlayerUtils;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class TpaSubCommand extends AbstractCommand {

    private final RequiredArg<String> targetArg;

    public TpaSubCommand() {
        super("tpa", "Solicita teletransporte a un jugador");
        this.targetArg = withRequiredArg("jugador", "Nombre del jugador", ArgTypes.STRING);
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) {
            context.sender().sendMessage(Message.raw("§cLa consola no puede pedir TPA."));
            return CompletableFuture.completedFuture(null);
        }

        Player sender = (Player) context.sender();
        String targetName = targetArg.get(context);

        // CAMBIO: getName() -> getDisplayName()
//        if (targetName.equalsIgnoreCase(sender.getDisplayName())) {
//            sender.sendMessage(Message.raw("§cNo puedes enviarte solicitud a ti mismo."));
//            return CompletableFuture.completedFuture(null);
//        }

        // 1. Buscamos la referencia
        PlayerRef targetRef = PlayerUtils.getOnlinePlayer(targetName);

        if (targetRef == null) {
            sender.sendMessage(Message.raw("§cJugador no encontrado o desconectado."));
            return CompletableFuture.completedFuture(null);
        }

        // 2. Guardamos la solicitud
        // CAMBIO: getName() -> getDisplayName()
        // Usamos getUsername() para el targetRef porque es una referencia
        TpaManager.getInstance().createRequest(sender.getDisplayName(), targetRef.getUsername());

        // 3. Enviamos mensajes
        sender.sendMessage(Message.raw("§aSolicitud enviada a " + targetRef.getUsername()));

        targetRef.sendMessage(Message.raw("§6=================================="));
        // CAMBIO: getName() -> getDisplayName()
        targetRef.sendMessage(Message.raw("§e" + sender.getDisplayName() + " §7quiere ir hacia ti."));
        targetRef.sendMessage(Message.raw("§7Escribe: §b/home tpaccept §7para aceptar."));
        targetRef.sendMessage(Message.raw("§7O rechaza con: §c/home tpdeny"));
        targetRef.sendMessage(Message.raw("§6=================================="));

        return CompletableFuture.completedFuture(null);
    }
}