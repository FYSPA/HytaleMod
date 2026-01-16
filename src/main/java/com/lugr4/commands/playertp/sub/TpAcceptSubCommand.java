package com.lugr4.commands.playertp.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.lugr4.managers.TpaManager;
import com.lugr4.utils.PlayerUtils;

import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.component.Component;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class TpAcceptSubCommand extends AbstractCommand {

    public TpAcceptSubCommand() {
        super("accept", "Accepts a TPA application");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) return CompletableFuture.completedFuture(null);

        Player acceptor = (Player) context.sender();
        // CAMBIO: getName() -> getDisplayName()
        String acceptorName = acceptor.getDisplayName();

        if (!TpaManager.getInstance().hasRequest(acceptorName)) {
            acceptor.sendMessage(Message.raw("You have no pending requests."));
            return CompletableFuture.completedFuture(null);
        }

        String requesterName = TpaManager.getInstance().getRequester(acceptorName);
        PlayerRef requesterRef = PlayerUtils.getOnlinePlayer(requesterName);

        if (requesterRef == null) {
            acceptor.sendMessage(Message.raw(requesterName + "It is no longer connected."));
            TpaManager.getInstance().removeRequest(acceptorName);
            return CompletableFuture.completedFuture(null);
        }

        acceptor.sendMessage(Message.raw("Accepting request..."));
        requesterRef.sendMessage(Message.raw("Application accepted! Teleporting..."));

        // Ejecutamos en el hilo del mundo del ACEPTADOR
        acceptor.getWorld().execute(() -> {
            try {
                var acceptorEntityRef = acceptor.getReference();
                var acceptorStore = acceptorEntityRef.getStore();

                TransformComponent accTrans = acceptorStore.getComponent(acceptorEntityRef, TransformComponent.getComponentType());

                if (accTrans == null) {
                    acceptor.sendMessage(Message.raw("Error: Your position could not be read."));
                    return;
                }

                // Obtener referencia del solicitante
                var reqEntityRef = requesterRef.getReference();
                if (reqEntityRef == null || !reqEntityRef.isValid()) return;
                var reqStore = reqEntityRef.getStore();

                // Crear Transform
                var pos = accTrans.getPosition();
                // Constructor de Transform (X, Y, Z, Yaw, Pitch, Roll)
                Transform destinationTransform = new Transform(
                        pos.getX(), pos.getY(), pos.getZ(),
                        0f, 0f, 0f
                );

                Teleport teleport = new Teleport(acceptor.getWorld(), destinationTransform);

                ((com.hypixel.hytale.component.Store) reqStore).addComponent(reqEntityRef, Teleport.getComponentType(), (Component) teleport);

                TpaManager.getInstance().removeRequest(acceptorName);

            } catch (Exception e) {
                acceptor.sendMessage(Message.raw("Technical error: " + e.getMessage()));
                e.printStackTrace();
            }
        });

        return CompletableFuture.completedFuture(null);
    }
}