package com.lugr4.managers;

import java.util.HashMap;
import java.util.Map;

public class TpaManager {
    private static final TpaManager instance = new TpaManager();
    public static TpaManager getInstance() { return instance; }

    // Mapa: <NombreDestino, NombreSolicitante>
    // Ejemplo: Luis recibe solicitud de Pedro -> <"Luis", "Pedro">
    private final Map<String, String> pendingRequests = new HashMap<>();

    public void createRequest(String senderName, String targetName) {
        pendingRequests.put(targetName, senderName);
        // Aquí podrías añadir un Timer para que expire en 60 segundos
    }

    public String getRequester(String targetName) {
        return pendingRequests.get(targetName);
    }

    public void removeRequest(String targetName) {
        pendingRequests.remove(targetName);
    }

    public boolean hasRequest(String targetName) {
        return pendingRequests.containsKey(targetName);
    }
}