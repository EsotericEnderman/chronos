package net.slqmy.chronos.listener;

import net.slqmy.chronos.ChronosPlugin;
import net.slqmy.chronos.manager.ChunkTimeManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

public class ChunkLoadListener implements Listener {

    private final ChronosPlugin plugin;

    public ChunkLoadListener(ChronosPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(@NotNull ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        ChunkTimeManager chunkTimeManager = plugin.getChunkTimeManager();

        Long timePassed = chunkTimeManager.getChunkTimePassed(chunk);

        if (timePassed == null) {
            return;
        }

        Bukkit.getLogger().info("Time passed since chunk " + chunk + " was previously loaded: " + (timePassed / 1000.0D) + " seconds." );

        chunkTimeManager.updateChunk(chunk);
    }
}
