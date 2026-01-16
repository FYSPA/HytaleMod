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
        super("deny", "Rejects a TPA application");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) return CompletableFuture.completedFuture(null);

        Player acceptor = (Player) context.sender();
        // CAMBIO: getName() -> getDisplayName()
        String acceptorName = acceptor.getDisplayName();

        if (!TpaManager.getInstance().hasRequest(acceptorName)) {
            acceptor.sendMessage(Message.raw("You have no requests to decline."));
            return CompletableFuture.completedFuture(null);
        }

        String requesterName = TpaManager.getInstance().getRequester(acceptorName);
        PlayerRef requesterRef = PlayerUtils.getOnlinePlayer(requesterName);

        if (requesterRef != null) {
            requesterRef.sendMessage(Message.raw(acceptorName + " has rejected your application."));
        }

        acceptor.sendMessage(Message.raw("You have rejected the request to " + requesterName));
        TpaManager.getInstance().removeRequest(acceptorName);

        return CompletableFuture.completedFuture(null);
    }
}