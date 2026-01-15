package com.lugr4.commands.home.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.lugr4.managers.HomeManager;

// IMPORTS OFICIALES (Basados en FancyCore)
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
// Si Vector3d te da error, intenta: import org.joml.Vector3d;

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
        String target = (context.sender() instanceof Player) ? ((Player)context.sender()).getDisplayName() : "Luis";

        HomeManager.HomeLocation loc = HomeManager.getInstance().getHome(target, homeName);

        if (loc == null) {
            context.sender().sendMessage(Message.raw("§cNo existe la casa '" + homeName + "'"));
            return CompletableFuture.completedFuture(null);
        }

        if (!(context.sender() instanceof Player)) {
            context.sender().sendMessage(Message.raw("§b[Consola] " + target + " iría a: " + loc.x + ", " + loc.y + ", " + loc.z));
            return CompletableFuture.completedFuture(null);
        }

        Player player = (Player) context.sender();

        player.getWorld().execute(() -> {
            try {
                var entityRef = player.getReference();
                var store = entityRef.getStore();

                // 1. Obtenemos componente de rotación
                HeadRotation headRot = store.getComponent(entityRef, HeadRotation.getComponentType());

                // Usamos 'var' para que Java adivine el tipo (Quaternionf) sin importarlo
                var currentRotation = (headRot != null) ? headRot.getRotation() : null;

                // 2. Preparamos el Vector de posición (usando Vector3d como en FancyCore)
                Vector3d positionVector = new Vector3d(loc.x, loc.y, loc.z);

                Transform destinationTransform;

                // 3. LÓGICA INTELIGENTE:
                // Si tenemos rotación, usamos el constructor de 2 argumentos.
                // Si NO tenemos rotación (raro), usamos el constructor de 1 argumento (solo posición).
                if (currentRotation != null) {
                    destinationTransform = new Transform(positionVector, currentRotation);
                } else {
                    // Si falla, prueba a poner: new Transform(positionVector, new org.joml.Quaternionf())
                    destinationTransform = new Transform(positionVector);
                }

                // 4. Teletransporte
                Teleport teleport = new Teleport(player.getWorld(), destinationTransform);
                store.addComponent(entityRef, Teleport.getComponentType(), teleport);

                context.sender().sendMessage(Message.raw("§b¡Has vuelto a '" + homeName + "'!"));

            } catch (Exception e) {
                context.sender().sendMessage(Message.raw("§cError técnico: " + e.getMessage()));
                e.printStackTrace();
            }
        });

        return CompletableFuture.completedFuture(null);
    }
}