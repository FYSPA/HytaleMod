package com.lugr4.managers;

import java.util.HashMap;
import java.util.Map;

public class TpaManager {
    private static final TpaManager instance = new TpaManager();
    public static TpaManager getInstance() { return instance; }

    // Mapa: <NombreDestino, NombreSolicitante>
    // Guardamos los nombres reales (Username) para evitar errores con colores o nicks
    private final Map<String, String> pendingRequests = new HashMap<>();

    public void createRequest(String senderName, String targetName) {
        pendingRequests.put(targetName, senderName);
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