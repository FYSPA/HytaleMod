package com.lugr4.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;

// Imports del sistema ECS y Matemáticas
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.math.vector.Vector3d;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class HomeManager {
    private static final HomeManager instance = new HomeManager();
    public static HomeManager getInstance() { return instance; }

    private Map<String, Map<String, HomeLocation>> homes = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 1. DEFINICIÓN DE CARPETAS Y ARCHIVOS
    // Esto creará: servidor/mods/WayHomeConfig/homes.json
    private final File DATA_FOLDER = new File("mods/WayHomeConfig");
    private final File HOMES_FILE = new File(DATA_FOLDER, "homes.json");

    private HomeManager() {
        // Aseguramos que la carpeta exista al iniciar
        if (!DATA_FOLDER.exists()) {
            DATA_FOLDER.mkdirs();
        }
        loadFromFile();
    }

    public boolean setHome(Player player, String homeName) {
        World world = player.getWorld();
        if (world == null) return false;

        try {
            var entityRef = player.getReference();
            var store = entityRef.getStore();

            // 1. OBTENER COMPONENTE
            TransformComponent transform = (TransformComponent) store.getComponent(entityRef, TransformComponent.getComponentType());

            if (transform != null) {
                // 2. OBTENER POSICIÓN
                Vector3d position = transform.getPosition();

                if (position != null) {
                    // 3. GUARDAR
                    saveHomeRaw(player.getDisplayName(), homeName, position.x, position.y, position.z, world.getName());
                    return true;
                }
            } else {
                System.out.println("[HomeManager] Error: TransformComponent es null.");
            }

        } catch (Exception e) {
            System.out.println("[HomeManager] Excepción: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void saveHome(String playerName, String homeName, HomeLocation loc) {
        if (loc != null) saveHomeRaw(playerName, homeName, loc.x, loc.y, loc.z, loc.worldId);
    }

    public void saveHomeRaw(String playerName, String homeName, double x, double y, double z, String worldId) {
        homes.computeIfAbsent(playerName, k -> new HashMap<>());
        homes.get(playerName).put(homeName, new HomeLocation(x, y, z, worldId));
        saveToFile();
    }

    public HomeLocation getHome(String playerName, String homeName) {
        if (!homes.containsKey(playerName)) return null;
        return homes.get(playerName).get(homeName);
    }

    public Map<String, Map<String, HomeLocation>> getAllHomes() { return this.homes; }

    public boolean deleteHome(String playerName, String homeName) {
        if (homes.containsKey(playerName)) {
            homes.get(playerName).remove(homeName);
            saveToFile();
            return true;
        }
        return false;
    }

    // --- PERSISTENCIA DE DATOS ---

    private void saveToFile() {
        // Seguridad extra: crear carpeta si se borró mientras el server corría
        if (!DATA_FOLDER.exists()) DATA_FOLDER.mkdirs();

        try (Writer writer = new FileWriter(HOMES_FILE)) {
            gson.toJson(homes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        if (!HOMES_FILE.exists()) return;

        try (Reader reader = new FileReader(HOMES_FILE)) {
            Type type = new TypeToken<Map<String, Map<String, HomeLocation>>>(){}.getType();
            Map<String, Map<String, HomeLocation>> loadedData = gson.fromJson(reader, type);
            if (loadedData != null) this.homes = loadedData;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class HomeLocation {
        public double x, y, z;
        public String worldId;
        public HomeLocation(double x, double y, double z, String worldId) {
            this.x = x; this.y = y; this.z = z; this.worldId = worldId;
        }
    }

    // Dentro de HomeManager.java

    public void trimHomes(String playerName, int limit) {
        if (homes.containsKey(playerName)) {
            Map<String, HomeLocation> playerHomes = homes.get(playerName);

            // Mientras el jugador tenga más casas que el nuevo límite...
            while (playerHomes.size() > limit && !playerHomes.isEmpty()) {
                // Buscamos la "última" casa (la primera que devuelva el iterador)
                String lastKey = playerHomes.keySet().iterator().next();
                playerHomes.remove(lastKey);
            }
            saveToFile(); // Guardamos los cambios en el JSON
        }
    }
}