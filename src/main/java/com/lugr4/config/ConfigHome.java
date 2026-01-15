package com.lugr4.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class ConfigHome {
    private static ConfigHome instance;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final File DATA_FOLDER = new File("mods/WayHomeConfig");
    private static final File CONFIG_FILE = new File(DATA_FOLDER, "home_config.json");

    // --- OPCIONES ---
    public int defaultMaxHomes = 3;
    public int vipMaxHomes = 10;
    public boolean allowCrossWorld = true;

    // NUEVAS OPCIONES DE DELAY
    public boolean teleportDelayOn = false; // Interruptor general
    public int teleportDelay = 3;           // Segundos

    private ConfigHome() {}

    public static ConfigHome get() {
        if (instance == null) instance = load();
        return instance;
    }

    private static ConfigHome load() {
        if (!DATA_FOLDER.exists()) DATA_FOLDER.mkdirs();

        if (CONFIG_FILE.exists()) {
            try (Reader reader = new FileReader(CONFIG_FILE)) {
                return gson.fromJson(reader, ConfigHome.class);
            } catch (IOException e) { e.printStackTrace(); }
        }

        ConfigHome newConfig = new ConfigHome();
        newConfig.save();
        return newConfig;
    }

    public void save() {
        if (!DATA_FOLDER.exists()) DATA_FOLDER.mkdirs();
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(this, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Getters
    public int getMaxHomes() { return defaultMaxHomes; }
    public boolean isDelayEnabled() { return teleportDelayOn; }
    public int getDelaySeconds() { return teleportDelay; }

    public boolean isCrossWorldAllowed() {
        return allowCrossWorld;
    }
}