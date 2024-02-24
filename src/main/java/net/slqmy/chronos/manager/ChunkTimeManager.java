package net.slqmy.chronos.manager;

import net.slqmy.chronos.ChronosPlugin;
import net.slqmy.chronos.enums.PassedTimeCalculationMode;
import net.slqmy.chronos.util.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChunkTimeManager {

    private static final long TIME_SCALE = 100L;

    private final ChronosPlugin plugin;

    public ChunkTimeManager(ChronosPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateChunk(Chunk chunk) {
        Long timePassed = getChunkTimePassed(chunk);

        if (timePassed == null) {
            return;
        }

        List<Block> chunkBlocks = ChunkUtil.getChunkBlocks(chunk);

        for (Block block : chunkBlocks) {
            updateBlock(block, timePassed);
        }
    }

    public void updateBlock(@NotNull Block block, long timePassedMilliseconds) {
        timePassedMilliseconds *= TIME_SCALE;

        long timePassedSeconds = timePassedMilliseconds / 1000;
        long timePassedMinutes = timePassedSeconds / 60;

        YamlConfiguration configuration = (YamlConfiguration) plugin.getConfig();

        Material blockType = block.getType();
        BlockData blockData = block.getBlockData();

        if (blockData instanceof Ageable ageableBlockData) {
            int maximumAge = ageableBlockData.getMaximumAge();

            String blockName = blockType.toString().toLowerCase();
            double growthDuration = configuration.getDouble("growth-durations-minutes." + blockName, -1.0D);

            if (growthDuration == -1.0D) {
                return;
            }

            int newAge = Math.min((int) Math.floor(ageableBlockData.getAge() + timePassedMinutes * configuration.getDouble("growth-durations-minutes." + blockName) / maximumAge), maximumAge);

            Bukkit.getLogger().info("newAge = " + newAge);

            ageableBlockData.setAge(newAge);
            block.setBlockData(ageableBlockData);
        }
    }

    @Nullable
    public Long getChunkLastLoadedTime(@NotNull Chunk chunk) {
        PersistentDataContainer dataContainer = chunk.getPersistentDataContainer();

        return dataContainer.get(plugin.getChunkLastLoadedTimeKey(), PersistentDataType.LONG);
    }

    public Long getChunkTimePassed(Chunk chunk) {
        YamlConfiguration configuration = (YamlConfiguration) plugin.getConfig();
        PassedTimeCalculationMode timePassedCalculationMode = PassedTimeCalculationMode.valueOf(configuration.getString("passed-time-calculation-mode"));

        Long lastLoadedTime = getChunkLastLoadedTime(chunk);

        switch (timePassedCalculationMode) {
            case CHUNK_LAST_LOADED -> {
                if (lastLoadedTime == null) {
                    return null;
                }

                return System.currentTimeMillis() - lastLoadedTime;
            }

            case WORLD_GENERATED -> {
                long worldTimeMilliseconds = (chunk.getWorld().getFullTime() / 20L) * 1000L;

                Bukkit.getLogger().info("worldTimeMilliseconds = " + worldTimeMilliseconds);

                return lastLoadedTime == null ? worldTimeMilliseconds : lastLoadedTime;
            }
        }

        return null;
    }
}
