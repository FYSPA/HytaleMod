package com.lugr4.commands.playertp.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.lugr4.managers.TpaManager;

// Imports de FancyCore para mover al jugador
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class TpAcceptSubCommand extends AbstractCommand {

    public TpAcceptSubCommand() {
        super("tpaccept", "Acepta una solicitud de teletransporte");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) return CompletableFuture.completedFuture(null);

        Player acceptor = (Player) context.sender(); // El que acepta (Destino)
        String acceptorName = acceptor.getDisplayName();

        // Verificar si tiene solicitudes
        if (!TpaManager.getInstance().hasRequest(acceptorName)) {
            acceptor.sendMessage(Message.raw("§cNo tienes ninguna solicitud pendiente."));
            return CompletableFuture.completedFuture(null);
        }

        String requesterName = TpaManager.getInstance().getRequester(acceptorName);

        // Buscar al que pidió el TP (Requester)
        Player requester = null;
        for (Player p : acceptor.getWorld().getName()) {
            if (p.getDisplayName().equalsIgnoreCase(requesterName)) {
                requester = p;
                break;
            }
        }

        if (requester == null) {
            acceptor.sendMessage(Message.raw("§cEl jugador " + requesterName + " ya no está conectado."));
            TpaManager.getInstance().removeRequest(acceptorName);
            return CompletableFuture.completedFuture(null);
        }

        // === INICIO DEL TELETRANSPORTE ===
        // Movemos al REQUESTER hacia el ACCEPTOR

        Player finalRequester = requester;
        Player finalAcceptor = acceptor;

        // Ejecutamos en el mundo
        acceptor.getWorld().execute(() -> {
            try {
                // 1. Obtener la posición del que ACEPTA (Destino)
                var acceptorRef = finalAcceptor.getReference();
                var acceptorStore = acceptorRef.getStore();

                TransformComponent accTrans = acceptorStore.getComponent(acceptorRef, TransformComponent.getComponentType());
                HeadRotation accRot = acceptorStore.getComponent(acceptorRef, HeadRotation.getComponentType());

                if (accTrans == null) return;

                // Rotación: Usamos la del destino o identidad
                var destRot = (accRot != null) ? accRot.getRotation() : new com.hypixel.hytale.math.vector.Quaternionf(0,0,0,1);

                // 2. Preparar el componente Teleport para el que SOLICITÓ
                var reqRef = finalRequester.getReference();
                var reqStore = reqRef.getStore();

                Transform destinationTransform = new Transform(
                        accTrans.getPosition(), // Posición del destino
                        destRot                 // Rotación del destino
                );

                Teleport teleport = new Teleport(finalAcceptor.getWorld(), destinationTransform);

                // 3. Aplicar teleport al solicitante
                reqStore.addComponent(reqRef, Teleport.getComponentType(), teleport);

                // Mensajes
                finalRequester.sendMessage(Message.raw("§aSolicitud aceptada. Teletransportando..."));
                finalAcceptor.sendMessage(Message.raw("§aHas aceptado a " + requesterName));

                // Limpiar solicitud
                TpaManager.getInstance().removeRequest(acceptorName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return CompletableFuture.completedFuture(null);
    }
}