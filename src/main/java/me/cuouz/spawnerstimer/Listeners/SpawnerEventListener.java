package me.cuouz.spawnerstimer.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SpawnerEventListener implements Listener {

    private Plugin plugin;
    private Map<CreatureSpawner, Date> spawnerExpirationMap;

    public SpawnerEventListener(Plugin plugin) {
        this.plugin = plugin;
        this.spawnerExpirationMap = new HashMap<>();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof CreatureSpawner) {
            CreatureSpawner spawner = (CreatureSpawner) event.getClickedBlock().getState();
            if (!spawnerExpirationMap.containsKey(spawner)) {
                spawnerExpirationMap.put(spawner, calculateExpiration());
            }

            Date expirationDate = spawnerExpirationMap.get(spawner);
            long remainingTime = calculateRemainingTime(expirationDate);
            player.sendMessage(ChatColor.YELLOW + "Remaining time for the spawner: " + remainingTime + " seconds");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof CreatureSpawner) {
            CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
            spawnerExpirationMap.remove(spawner);
        }
    }

    public void showRemainingTime(Player player) {
        for (CreatureSpawner spawner : spawnerExpirationMap.keySet()) {
            Date expirationDate = spawnerExpirationMap.get(spawner);
            long remainingTime = calculateRemainingTime(expirationDate);
            player.sendMessage(ChatColor.YELLOW + "Remaining time for a spawner: " + remainingTime + " seconds");
        }
    }

    private Date calculateExpiration() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 4); // Add 4 hours to the current time
        return calendar.getTime();
    }

    private long calculateRemainingTime(Date expirationDate) {
        Date now = new Date();
        long remainingTimeInSeconds = (expirationDate.getTime() - now.getTime()) / 1000;
        return Math.max(0, remainingTimeInSeconds); // Ensure the remaining time is not negative
    }
}
