package net.slqmy.chronos.listener;

import net.slqmy.chronos.ChronosPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ChunkLoadListener implements Listener {

    private final ChronosPlugin plugin;

    public ChunkLoadListener(ChronosPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(@NotNull ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        PersistentDataContainer dataContainer = chunk.getPersistentDataContainer();

        Long lastLoadedTime = dataContainer.get(plugin.getChunkLastLoadedTimeKey(), PersistentDataType.LONG);

        if (lastLoadedTime == null) {
            return;
        }

        long timePassed = System.currentTimeMillis() - lastLoadedTime;

        Bukkit.getLogger().info("Time passed since chunk " + chunk + " was previously loaded: " + (timePassed / 1000) + " seconds." );
    }
}
