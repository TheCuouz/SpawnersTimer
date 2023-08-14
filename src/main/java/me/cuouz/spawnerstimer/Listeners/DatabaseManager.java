package me.cuouz.spawnerstimer.Listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {

    private Connection connection;
    private Plugin plugin;
    private Map<CreatureSpawner, Date> spawnerExpirationMap;

    public DatabaseManager(String databasePath, Plugin plugin) {
        this.plugin = plugin;
        try {
            Class.forName("org.sqlite.JDBC");

            File databaseFile = new File(databasePath);
            if (!databaseFile.exists()) {
                databaseFile.getParentFile().mkdirs();
                databaseFile.createNewFile();
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            createTable();
            spawnerExpirationMap = new HashMap<>();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try {
            String query = "CREATE TABLE IF NOT EXISTS spawner_locations (player_uuid TEXT, location_x INT, location_y INT, location_z INT)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveSpawnerLocation(UUID playerUUID, Location location) {
        try {
            String query = "INSERT INTO spawner_locations (player_uuid, location_x, location_y, location_z) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, playerUUID.toString());
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Location> getSpawnerLocations(UUID playerUUID) {
        List<Location> spawnerLocations = new ArrayList<>();
        try {
            String query = "SELECT location_x, location_y, location_z FROM spawner_locations WHERE player_uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int x = resultSet.getInt("location_x");
                int y = resultSet.getInt("location_y");
                int z = resultSet.getInt("location_z");
                World world = plugin.getServer().getWorlds().get(0);
                Location location = new Location(world, x, y, z);
                spawnerLocations.add(location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spawnerLocations;
    }

    public void clearPlayerSpawnerLocations(UUID playerUUID) {
        try {
            String query = "DELETE FROM spawner_locations WHERE player_uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, playerUUID.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Puedes agregar otros métodos aquí si es necesario
}
