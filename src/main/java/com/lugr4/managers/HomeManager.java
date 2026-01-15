package com.lugr4.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;

// IMPORTS BASADOS EN TU CÓDIGO NUEVO
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
    private final String FILE_PATH = "homes.json";

    private HomeManager() { loadFromFile(); }

    public boolean setHome(Player player, String homeName) {
        World world = player.getWorld();
        if (world == null) return false;

        try {
            var entityRef = player.getReference();
            var store = entityRef.getStore();

            // 1. OBTENER COMPONENTE (Igual que en tu PlayerInfoHandler)
            TransformComponent transform = (TransformComponent) store.getComponent(entityRef, TransformComponent.getComponentType());

            if (transform != null) {
                // 2. OBTENER POSICIÓN (Vector3d)
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

    // ... (El resto de métodos saveHome, getHome, deleteHome, etc. SE MANTIENEN IGUAL) ...

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

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) { gson.toJson(homes, writer); }
        catch (IOException e) { e.printStackTrace(); }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Map<String, HomeLocation>>>(){}.getType();
            Map<String, Map<String, HomeLocation>> loadedData = gson.fromJson(reader, type);
            if (loadedData != null) this.homes = loadedData;
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static class HomeLocation {
        public double x, y, z;
        public String worldId;
        public HomeLocation(double x, double y, double z, String worldId) {
            this.x = x; this.y = y; this.z = z; this.worldId = worldId;
        }
    }
}