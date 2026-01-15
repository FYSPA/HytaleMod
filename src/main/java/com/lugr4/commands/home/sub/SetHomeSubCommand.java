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

public class SetHomeSubCommand extends AbstractCommand {

    private final RequiredArg<String> homeNameArg;

    public SetHomeSubCommand() {
        super("set", "Guarda tu posición actual");
        this.homeNameArg = withRequiredArg("nombre", "Nombre de la casa", ArgTypes.STRING);
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        String homeName = homeNameArg.get(context);

        // --- CASO 1: CONSOLA (DOCKER) ---
        if (!(context.sender() instanceof Player)) {
            // Simulamos datos
            HomeManager.HomeLocation fake = new HomeManager.HomeLocation(100.0, 64.0, 100.0, "world");
            HomeManager.getInstance().saveHome("Luis", homeName, fake);

            context.sender().sendMessage(Message.raw("§e[Docker] Casa '" + homeName + "' simulada guardada para Luis."));
            context.sender().sendMessage(Message.raw("§7Coordenadas: " + fake.x + ", " + fake.y + ", " + fake.z));
            return CompletableFuture.completedFuture(null);
        }

        // --- CASO 2: JUGADOR REAL ---
        Player player = (Player) context.sender();

        // Intentamos guardar
        boolean exito = HomeManager.getInstance().setHome(player, homeName);

        if (exito) {
            // Recuperamos lo guardado para confirmarlo en el chat
            var loc = HomeManager.getInstance().getHome(player.getDisplayName(), homeName);

            context.sender().sendMessage(Message.raw("§a¡Casa '" + homeName + "' establecida con éxito!"));
            // AQUÍ VES LA REALIDAD:
            context.sender().sendMessage(Message.raw("§7Ubicación exacta: X:" + String.format("%.2f", loc.x) +
                    " Y:" + String.format("%.2f", loc.y) +
                    " Z:" + String.format("%.2f", loc.z)));
        } else {
            context.sender().sendMessage(Message.raw("§cError: No se pudo leer tu componente TransformComponent."));
        }

        return CompletableFuture.completedFuture(null);
    }
}