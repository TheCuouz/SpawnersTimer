package me.cuouz.spawnerstimer.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LimitedSpawnDuration implements Listener {

    private DatabaseManager dbManager;
    private String expiredMessage;
    private String remainingTimeMessage;
    private long defaultDuration;

    public LimitedSpawnDuration(DatabaseManager dbManager, String expiredMessage, String remainingTimeMessage, long defaultDuration) {
        this.dbManager = dbManager;
        this.expiredMessage = expiredMessage;
        this.remainingTimeMessage = remainingTimeMessage;
        this.defaultDuration = defaultDuration;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();

        if (itemInHand != null && itemInHand.getType() == Material.MOB_SPAWNER) {
            ItemMeta itemMeta = itemInHand.getItemMeta();

            if (itemMeta != null) {
                List<String> lore = itemMeta.getLore();

                if (lore != null) {
                    for (int i = 0; i < lore.size(); i++) {
                        String line = lore.get(i);
                        if (line.startsWith("Expira: ")) {
                            String expirationDateStr = line.replace("Expira: ", "");
                            long expirationDate = Long.parseLong(expirationDateStr);

                            if (System.currentTimeMillis() > expirationDate) {
                                player.sendMessage(expiredMessage);
                                player.getInventory().remove(itemInHand);
                                dbManager.clearPlayerSpawnerLocations(player.getUniqueId());
                            } else {
                                long remainingTime = expirationDate - System.currentTimeMillis();
                                String remainingTimeString = formatRemainingTime(remainingTime);
                                player.sendMessage(remainingTimeMessage.replace("%time%", remainingTimeString));
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private String formatRemainingTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days + "d " + (hours % 24) + "h " + (minutes % 60) + "m";
    }
}
