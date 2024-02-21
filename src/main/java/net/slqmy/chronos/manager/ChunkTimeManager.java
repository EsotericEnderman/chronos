package net.slqmy.chronos.manager;

import net.slqmy.chronos.ChronosPlugin;
import net.slqmy.chronos.util.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChunkTimeManager {

    private static final long TIME_INCREASE = 100L;

    private final ChronosPlugin plugin;

    public ChunkTimeManager(ChronosPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateChunk(Chunk chunk) {
        Long timeSinceChunkWasLastLoaded = getTimeSinceChunkWasLastLoaded(chunk);

        if (timeSinceChunkWasLastLoaded == null) {
            return;
        }

        List<Block> chunkBlocks = ChunkUtil.getChunkBlocks(chunk);

        for (Block block : chunkBlocks) {
            updateBlock(block, timeSinceChunkWasLastLoaded);
        }
    }

    public void updateBlock(@NotNull Block block, long timeSinceLastLoaded) {
        timeSinceLastLoaded *= TIME_INCREASE;

        switch (block.getType()) {
            case WHEAT -> {
                Bukkit.getLogger().info("Encountered wheat: " + block);
                Bukkit.getLogger().info("block.blockData = " + block.getBlockData());
                Bukkit.getLogger().info("block.blockData.class = " + block.getBlockData().getClass());

                Ageable wheatData = ((Ageable) block.getBlockData());

                Bukkit.getLogger().info("New age: " + Math.min((int) Math.floor(wheatData.getAge() + ((timeSinceLastLoaded / 1000.0D) / 60.0D) * (34.93D / 8.0D)), 7));

                wheatData.setAge(Math.min((int) Math.floor(wheatData.getAge() + ((timeSinceLastLoaded / 1000.0D) / 60.0D) * (34.93D / 8.0D)), 7));

                block.setBlockData(wheatData);
            }
        }
    }

    public Long getChunkLastLoadedTime(@NotNull Chunk chunk) {
        PersistentDataContainer dataContainer = chunk.getPersistentDataContainer();

        return dataContainer.get(plugin.getChunkLastLoadedTimeKey(), PersistentDataType.LONG);
    }

    public Long getTimeSinceChunkWasLastLoaded(Chunk chunk) {
        Long lastLoadedTime = getChunkLastLoadedTime(chunk);

        if (lastLoadedTime == null) {
            return null;
        }

        return System.currentTimeMillis() - lastLoadedTime;
    }
}
