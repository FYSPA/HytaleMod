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
        super("tpa", "Request teleportation from a player");
        this.targetArg = withRequiredArg("jugador", "Player's name", ArgTypes.STRING);
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) {
            context.sender().sendMessage(Message.raw("The console cannot request TPA."));
            return CompletableFuture.completedFuture(null);
        }

        Player sender = (Player) context.sender();
        String targetName = targetArg.get(context);

        if (targetName.equalsIgnoreCase(sender.getDisplayName())) {
            sender.sendMessage(Message.raw("You cannot send a request to yourself."));
            return CompletableFuture.completedFuture(null);
        }

        // 1. Buscamos la referencia
        PlayerRef targetRef = PlayerUtils.getOnlinePlayer(targetName);

        if (targetRef == null) {
            sender.sendMessage(Message.raw("Player not found or disconnected."));
            return CompletableFuture.completedFuture(null);
        }


        TpaManager.getInstance().createRequest(sender.getDisplayName(), targetRef.getUsername());
        sender.sendMessage(Message.raw("§aSolicitud enviada a " + targetRef.getUsername()));

        targetRef.sendMessage(Message.raw("=================================="));
        targetRef.sendMessage(Message.raw(sender.getDisplayName() + " wants to come towards you."));
        targetRef.sendMessage(Message.raw("Type '/request accept' to accept the request."));
        targetRef.sendMessage(Message.raw("Or reject with: '/request deny'"));
        targetRef.sendMessage(Message.raw("=================================="));

        return CompletableFuture.completedFuture(null);
    }
}