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
        // Definimos el argumento obligatorio para evitar el error "Expected 0, actual 1"
        this.homeNameArg = withRequiredArg("nombre", "Nombre de la casa", ArgTypes.STRING);
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        String homeName = homeNameArg.get(context);

        // --- CASO 1: CONSOLA (DOCKER) ---
        // Esto sirve para tus pruebas desde la terminal
        if (!(context.sender() instanceof Player)) {
            HomeManager.HomeLocation fake = new HomeManager.HomeLocation(100.0, 64.0, 100.0, "world");
            HomeManager.getInstance().saveHome("Luis", homeName, fake);

            context.sender().sendMessage(Message.raw("§e[Docker] Casa de prueba '" + homeName + "' guardada para Luis."));
            context.sender().sendMessage(Message.raw("§7(Coordenadas simuladas: 100, 64, 100)"));
            return CompletableFuture.completedFuture(null);
        }

        // --- CASO 2: JUGADOR REAL ---
        Player player = (Player) context.sender();

        // ¡IMPORTANTE! Solución al error "Assert not in thread!"
        // Ejecutamos la lógica dentro del hilo del mundo para poder leer los componentes con seguridad.
        player.getWorld().execute(() -> {

            // Llamamos al Manager para que lea el TransformComponent y guarde el JSON
            boolean exito = HomeManager.getInstance().setHome(player, homeName);

            if (exito) {
                // Recuperamos lo guardado para confirmar coordenadas exactas al usuario
                var loc = HomeManager.getInstance().getHome(player.getDisplayName(), homeName);

                context.sender().sendMessage(Message.raw("§a¡Casa '" + homeName + "' establecida con éxito!"));

                if (loc != null) {
                    String coords = String.format("X:%.1f Y:%.1f Z:%.1f", loc.x, loc.y, loc.z);
                    context.sender().sendMessage(Message.raw("§7Ubicación: " + coords));
                }
            } else {
                context.sender().sendMessage(Message.raw("§cError: No se pudo leer tu posición (TransformComponent)."));
            }
        });

        return CompletableFuture.completedFuture(null);
    }
}