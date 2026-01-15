package com.lugr4.commands.playertp.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.lugr4.managers.TpaManager;
import com.lugr4.utils.PlayerUtils; // <--- USAMOS TU UTILIDAD

// Imports de movimiento
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.component.Component; // Para el cast final

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class TpAcceptSubCommand extends AbstractCommand {

    public TpAcceptSubCommand() {
        super("tpaccept", "Acepta una solicitud de teletransporte");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) return CompletableFuture.completedFuture(null);

        Player acceptor = (Player) context.sender();
        String acceptorName = acceptor.getDisplayName();

        if (!TpaManager.getInstance().hasRequest(acceptorName)) {
            acceptor.sendMessage(Message.raw("§cNo tienes ninguna solicitud pendiente."));
            return CompletableFuture.completedFuture(null);
        }

        String requesterName = TpaManager.getInstance().getRequester(acceptorName);

        // === CORRECCIÓN: Buscar al solicitante con PlayerUtils ===
        Player requester = PlayerUtils.getOnlinePlayer(requesterName);

        if (requester == null) {
            acceptor.sendMessage(Message.raw("§cEl jugador " + requesterName + " ya no está conectado."));
            TpaManager.getInstance().removeRequest(acceptorName);
            return CompletableFuture.completedFuture(null);
        }

        // Variables finales para la lambda
        Player finalRequester = requester;
        Player finalAcceptor = acceptor;

        acceptor.getWorld().execute(() -> {
            try {
                // 1. Obtener posición del destino (Acceptor)
                var acceptorRef = finalAcceptor.getReference();
                var acceptorStore = acceptorRef.getStore();

                TransformComponent accTrans = acceptorStore.getComponent(acceptorRef, TransformComponent.getComponentType());
                HeadRotation accRot = acceptorStore.getComponent(acceptorRef, HeadRotation.getComponentType());

                if (accTrans == null) return;

                // 2. Obtener la rotación existente para no usar Quaternionf manual
                var destRot = (accRot != null) ? accRot.getRotation() : null;

                // 3. Preparar Teleport
                var reqRef = finalRequester.getReference();
                var reqStore = reqRef.getStore();

                // Usamos el constructor inteligente (si hay rotación la usa, si no, usa solo posición)
                Transform destinationTransform;
                if (destRot != null) {
                    destinationTransform = new Transform(accTrans.getPosition(), destRot);
                } else {
                    destinationTransform = new Transform(accTrans.getPosition());
                }

                Teleport teleport = new Teleport(finalAcceptor.getWorld(), destinationTransform);

                // 4. Aplicar (con el cast a Component para evitar error de bounds)
                ((com.hypixel.hytale.component.Store) reqStore).addComponent(reqRef, Teleport.getComponentType(), (Component) teleport);

                // Mensajes
                finalRequester.sendMessage(Message.raw("§aSolicitud aceptada. Teletransportando..."));
                finalAcceptor.sendMessage(Message.raw("§aHas aceptado a " + requesterName));

                TpaManager.getInstance().removeRequest(acceptorName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return CompletableFuture.completedFuture(null);
    }
}