package com.lugr4.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
// IMPORTANTE: Usamos la clase oficial descubierta en FancyCore
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;

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

    private HomeManager() {
        loadFromFile();
    }

    public boolean setHome(Player player, String homeName) {
        World world = player.getWorld();
        if (world == null) return false;

        try {
            var entityRef = player.getReference();
            var store = entityRef.getStore();

            // Usamos TransformComponent, la forma oficial de Hytale
            TransformComponent transform = store.getComponent(entityRef, TransformComponent.getComponentType());

            if (transform != null) {
                // Obtenemos la posición exacta (Vector3d)
                var pos = transform.getPosition();

                // Guardamos los datos
                saveHomeRaw(player.getDisplayName(), homeName, pos.getX(), pos.getY(), pos.getZ(), world.getName());
                return true;
            }
        } catch (Exception e) {
            // Si quieres loguear errores, usa App.getPluginLogger().severe(...)
            e.printStackTrace();
        }
        return false;
    }

    public void saveHome(String playerName, String homeName, HomeLocation loc) {
        if (loc != null) {
            saveHomeRaw(playerName, homeName, loc.x, loc.y, loc.z, loc.worldId);
        }
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

    public static class HomeLocation {
        public double x, y, z;
        public String worldId;
        public HomeLocation(double x, double y, double z, String worldId) {
            this.x = x; this.y = y; this.z = z; this.worldId = worldId;
        }
    }

    public Map<String, Map<String, HomeLocation>> getAllHomes() {
        return this.homes;
    }

    public boolean deleteHome(String playerName, String homeName) {
        if (homes.containsKey(playerName)) {
            var playerHomes = homes.get(playerName);
            if (playerHomes.containsKey(homeName)) {
                playerHomes.remove(homeName); // Borramos del mapa
                saveToFile(); // Guardamos el cambio en JSON
                return true;
            }
        }
        return false;
    }
}