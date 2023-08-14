package me.cuouz.spawnerstimer;

import me.cuouz.spawnerstimer.Commands.SpawnerCommand;
import me.cuouz.spawnerstimer.Listeners.DatabaseManager;
import me.cuouz.spawnerstimer.Listeners.SpawnerEventListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SpawnersTimer extends JavaPlugin {

    private DatabaseManager dbManager;
    private SpawnerEventListener spawnerEventListener;

    @Override
    public void onEnable() {
        if (!getConfig().contains("initialized")) {
            getConfig().set("initialized", false);
            saveConfig();
        }

        boolean initialized = getConfig().getBoolean("initialized");
        if (initialized) {
            getLogger().warning("SpawnersTimer ya está habilitado.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        dbManager = new DatabaseManager(new File(getDataFolder(), "database.db").getAbsolutePath(), this);
        spawnerEventListener = new SpawnerEventListener(this);

        getServer().getPluginManager().registerEvents(spawnerEventListener, this);
        getCommand("st").setExecutor(new SpawnerCommand(this));

        getConfig().set("initialized", true);
        saveConfig();

        getLogger().info("SpawnersTimer se ha habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        if (dbManager != null) {
            dbManager.closeConnection();
        }
        getLogger().info("SpawnersTimer se ha deshabilitado.");
    }

    public SpawnerEventListener getSpawnerEventListener() {
        return spawnerEventListener;
    }
}
