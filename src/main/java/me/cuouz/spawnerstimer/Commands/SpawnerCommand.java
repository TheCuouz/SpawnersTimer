package me.cuouz.spawnerstimer.Commands;


import me.cuouz.spawnerstimer.Listeners.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import me.cuouz.spawnerstimer.SpawnersTimer;

public class SpawnerCommand implements CommandExecutor {

    private Plugin plugin;

    public SpawnerCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true; // Si el comando fue ejecutado por la consola u otro origen que no sea jugador, simplemente retornamos
        }

        Player player = (Player) sender;
        if (args.length == 1 && args[0].equalsIgnoreCase("check")) {
            SpawnerEventListener spawnerEventListener = getSpawnerEventListener();
            if (spawnerEventListener != null) {
                spawnerEventListener.showRemainingTime(player); // Llamamos al método para mostrar el tiempo restante
            }
            return true;
        }

        return false; // El comando no fue reconocido
    }

    private SpawnerEventListener getSpawnerEventListener() {
        if (plugin instanceof SpawnersTimer) {
            return ((SpawnersTimer) plugin).getSpawnerEventListener();
        }
        return null;
    }

}
