// GiveSpawnerCommand
package me.cuouz.spawnerstimer.Commands;

import me.cuouz.spawnerstimer.Listeners.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;

public class GiveSpawnerCommand implements CommandExecutor {

    private final Plugin plugin;
    private final DatabaseManager dbManager;
    private final long defaultDuration;

    public GiveSpawnerCommand(Plugin plugin, DatabaseManager dbManager, long defaultDuration) {
        this.plugin = plugin;
        this.dbManager = dbManager;
        this.defaultDuration = defaultDuration;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (args.length >= 2 && args[0].equalsIgnoreCase("givespawner")) {
            String targetPlayerName = args[1];
            String entityTypeString = args[2].toUpperCase();
            EntityType entityType = EntityType.valueOf(entityTypeString);

            Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

            if (targetPlayer != null && entityType != null) {
                // Dar spawner al jugador objetivo
                dbManager.givePlayerSpawner(targetPlayer.getUniqueId(), defaultDuration, entityType);
                player.sendMessage("Se ha dado un spawner de " + entityTypeString + " a " + targetPlayerName);
                return true;
            } else {
                player.sendMessage("Jugador o tipo de entidad inválidos.");
            }
        }

        return false;
    }
}
