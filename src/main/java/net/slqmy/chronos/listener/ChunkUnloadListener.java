package net.slqmy.chronos.listener;

import net.slqmy.chronos.ChronosPlugin;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ChunkUnloadListener implements Listener {

    private final ChronosPlugin plugin;

    public ChunkUnloadListener(ChronosPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkUnload(@NotNull ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();

        PersistentDataContainer dataContainer = chunk.getPersistentDataContainer();

        long lastLoadedTime = System.currentTimeMillis();

        dataContainer.set(plugin.getChunkLastLoadedTimeKey(), PersistentDataType.LONG, lastLoadedTime);
    }
}
