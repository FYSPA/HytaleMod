package com.lugr4.commands.home.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.lugr4.managers.HomeManager;
import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class DelHomeSubCommand extends AbstractCommand {
    private final RequiredArg<String> homeNameArg;

    public DelHomeSubCommand() {
        super("del", "Borra una casa");
        this.homeNameArg = withRequiredArg("nombre", "Nombre de la casa", ArgTypes.STRING);
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        String homeName = homeNameArg.get(context);

        // Obtenemos el nombre (sea jugador o "Luis" para consola)
        String target = (context.sender() instanceof Player) ? ((Player)context.sender()).getDisplayName() : "Luis";

        boolean deleted = HomeManager.getInstance().deleteHome(target, homeName);

        if (deleted) {
            context.sender().sendMessage(Message.raw("§e[Manager] La casa '" + homeName + "' ha sido eliminada."));
        } else {
            context.sender().sendMessage(Message.raw("§cError: No tenías ninguna casa llamada '" + homeName + "'."));
        }

        return CompletableFuture.completedFuture(null);
    }
}