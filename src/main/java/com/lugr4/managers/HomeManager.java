package com.lugr4.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;

// IMPORTS VITALES (Ambos tipos de Transform para evitar errores)
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.protocol.Transform;
import com.hypixel.hytale.component.ComponentType;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class HomeManager {
    private static final HomeManager instance = new HomeManager();
    public static HomeManager getInstance() { return instance; }

    private Map<String, Map<String, HomeLocation>> homes = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String FILE_PATH = "homes.json";

    // Constructor: Carga el JSON al iniciar
    private HomeManager() {
        loadFromFile();
    }

    /**
     * Guarda la casa de un jugador leyendo su posición real.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean setHome(Player player, String homeName) {
        World world = player.getWorld();
        if (world == null) return false;

        try {
            var entityRef = player.getReference();
            var store = entityRef.getStore();

            // --- PLAN A: Usar la clase oficial TransformComponent ---
            try {
                TransformComponent comp = store.getComponent(entityRef, TransformComponent.getComponentType());
                if (comp != null) {
                    var pos = comp.getPosition();
                    saveHomeRaw(player.getDisplayName(), homeName, pos.getX(), pos.getY(), pos.getZ(), world.getName());
                    return true;
                }
            } catch (Exception ignored) {}

            // --- PLAN B: Buscar manualmente el componente Transform (Protocolo) ---
            ComponentType type = new ComponentType();
            Object comp = store.getComponent(entityRef, type);

            if (comp instanceof Transform) {
                Transform t = (Transform) comp;
                // Intentamos leer position o getPosition()
                if (t.position != null) {
                    saveHomeRaw(player.getDisplayName(), homeName, t.position.x, t.position.y, t.position.z, world.getName());
                    return true;
                }
            }

            System.out.println("[HomeManager] Error: No se encontró ningún componente de posición válido.");

        } catch (Exception e) {
            System.out.println("[HomeManager] Excepción: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Guarda una casa usando un objeto Location (Para tests de consola)
     */
    public void saveHome(String playerName, String homeName, HomeLocation loc) {
        if (loc != null) {
            saveHomeRaw(playerName, homeName, loc.x, loc.y, loc.z, loc.worldId);
        }
    }

    /**
     * Lógica interna para guardar en el mapa y en el disco
     */
    public void saveHomeRaw(String playerName, String homeName, double x, double y, double z, String worldId) {
        homes.computeIfAbsent(playerName, k -> new HashMap<>());
        homes.get(playerName).put(homeName, new HomeLocation(x, y, z, worldId));
        saveToFile();
    }

    /**
     * Obtiene una casa específica
     */
    public HomeLocation getHome(String playerName, String homeName) {
        if (!homes.containsKey(playerName)) return null;
        return homes.get(playerName).get(homeName);
    }

    /**
     * Obtiene todas las casas (Para el comando /home list)
     */
    public Map<String, Map<String, HomeLocation>> getAllHomes() {
        return this.homes;
    }

    /**
     * EL MÉTODO QUE FALTABA: Borrar una casa
     */
    public boolean deleteHome(String playerName, String homeName) {
        if (homes.containsKey(playerName)) {
            var playerHomes = homes.get(playerName);
            if (playerHomes.containsKey(homeName)) {
                playerHomes.remove(homeName);

                // Si el jugador se queda sin casas, podemos borrar su entrada del mapa para limpiar
                if (playerHomes.isEmpty()) {
                    homes.remove(playerName);
                }

                saveToFile(); // Importante: Guardar cambios en el JSON
                return true;
            }
        }
        return false;
    }

    // --- PERSISTENCIA JSON ---

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(homes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Map<String, HomeLocation>>>(){}.getType();
            Map<String, Map<String, HomeLocation>> loadedData = gson.fromJson(reader, type);
            if (loadedData != null) {
                this.homes = loadedData;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase de datos
    public static class HomeLocation {
        public double x, y, z;
        public String worldId;
        public HomeLocation(double x, double y, double z, String worldId) {
            this.x = x; this.y = y; this.z = z; this.worldId = worldId;
        }
    }
}