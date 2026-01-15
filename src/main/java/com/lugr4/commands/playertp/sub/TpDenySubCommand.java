package com.lugr4.commands.playertp.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.lugr4.managers.TpaManager;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class TpDenySubCommand extends AbstractCommand {

    public TpDenySubCommand() {
        super("tpdeny", "Rechaza una solicitud de teletransporte");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) return CompletableFuture.completedFuture(null);
        Player acceptor = (Player) context.sender();
        String acceptorName = acceptor.getDisplayName();

        if (!TpaManager.getInstance().hasRequest(acceptorName)) {
            acceptor.sendMessage(Message.raw("§cNo tienes solicitudes para rechazar."));
            return CompletableFuture.completedFuture(null);
        }

        String requesterName = TpaManager.getInstance().getRequester(acceptorName);

        // Avisar al que pidió (si sigue online)
        for (Player p : acceptor.getWorld().getPlayers()) {
            if (p.getDisplayName().equalsIgnoreCase(requesterName)) {
                p.sendMessage(Message.raw("§c" + acceptorName + " rechazó tu solicitud de teletransporte."));
                break;
            }
        }

        acceptor.sendMessage(Message.raw("§eHas rechazado la solicitud de " + requesterName));
        TpaManager.getInstance().removeRequest(acceptorName);

        return CompletableFuture.completedFuture(null);
    }
}