package com.lugr4.commands.playertp.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.lugr4.managers.TpaManager;
import com.lugr4.utils.PlayerUtils;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class TpDenySubCommand extends AbstractCommand {

    public TpDenySubCommand() {
        super("deny", "Rechaza una solicitud de TPA");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) return CompletableFuture.completedFuture(null);

        Player acceptor = (Player) context.sender();
        // CAMBIO: getName() -> getDisplayName()
        String acceptorName = acceptor.getDisplayName();

        if (!TpaManager.getInstance().hasRequest(acceptorName)) {
            acceptor.sendMessage(Message.raw("No tienes solicitudes para rechazar."));
            return CompletableFuture.completedFuture(null);
        }

        String requesterName = TpaManager.getInstance().getRequester(acceptorName);
        PlayerRef requesterRef = PlayerUtils.getOnlinePlayer(requesterName);

        if (requesterRef != null) {
            requesterRef.sendMessage(Message.raw(acceptorName + " ha rechazado tu solicitud."));
        }

        acceptor.sendMessage(Message.raw("Has rechazado la solicitud de " + requesterName));
        TpaManager.getInstance().removeRequest(acceptorName);

        return CompletableFuture.completedFuture(null);
    }
}