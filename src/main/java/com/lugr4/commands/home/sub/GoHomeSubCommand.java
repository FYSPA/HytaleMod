package com.lugr4.commands.home.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.lugr4.managers.HomeManager;
import com.lugr4.config.ConfigHome;
import com.lugr4.utils.TeleportUtils; // Tu utilidad

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class GoHomeSubCommand extends AbstractCommand {

    private final RequiredArg<String> homeNameArg;

    public GoHomeSubCommand() {
        super("tp", "Vuelve a una casa");
        this.homeNameArg = withRequiredArg("nombre", "Nombre de la casa", ArgTypes.STRING);
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        String homeName = homeNameArg.get(context);
        String targetName = (context.sender() instanceof Player) ? ((Player)context.sender()).getDisplayName() : "Luis";

        // Verificamos si existe antes de hacer nada
        HomeManager.HomeLocation loc = HomeManager.getInstance().getHome(targetName, homeName);

        if (loc == null) {
            context.sender().sendMessage(Message.raw("§cNo existe la casa '" + homeName + "'"));
            return CompletableFuture.completedFuture(null);
        }

        if (!(context.sender() instanceof Player)) {
            context.sender().sendMessage(Message.raw("§b[Consola] Destino: " + loc.x + ", " + loc.y + ", " + loc.z));
            return CompletableFuture.completedFuture(null);
        }

        Player player = (Player) context.sender();
        ConfigHome config = ConfigHome.get();

        if (config.isDelayEnabled() && config.getDelaySeconds() > 0) {

            TeleportUtils.teleportToHome(player, homeName);
        } else {
            TeleportUtils.teleportToHome(player, homeName);
        }

        return CompletableFuture.completedFuture(null);
    }

}