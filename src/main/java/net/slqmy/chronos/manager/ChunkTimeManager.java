package net.slqmy.chronos.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.slqmy.chronos.ChronosPlugin;
import net.slqmy.chronos.enums.PassedTimeCalculationMode;
import net.slqmy.chronos.util.ChunkUtil;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ChunkTimeManager {

    private static final long TIME_SCALE = 100L;

    private final ChronosPlugin plugin;

    public ChunkTimeManager(ChronosPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateChunk(Chunk chunk) {
        Long timePassed = getChunkAge(chunk);

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

        org.bukkit.block.BlockState blockState = block.getState();

        Class<?> blockStateClass = blockState.getClass();

        try {
            Method blockStateClassGetHandleMethod = blockStateClass.getMethod("getHandle");

            BlockState nmsBlockState = (BlockState) blockStateClassGetHandleMethod.invoke(blockState);

            World blockWorld = block.getWorld();
            Class<?> worldClass = blockWorld.getClass();

            Method worldClassGetHandleMethod = worldClass.getMethod("getHandle");
            ServerLevel serverLevel = (ServerLevel) worldClassGetHandleMethod.invoke(blockWorld);

            long timePassedTicks = (timePassedMilliseconds / 1000) * 20;

            for (int tick = 0; tick < timePassedTicks; tick++) {
                nmsBlockState.tick(serverLevel, new BlockPos(block.getX(), block.getY(), block.getZ()), serverLevel.getRandom());
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Long getChunkLastLoadedTime(@NotNull Chunk chunk) {
        PersistentDataContainer dataContainer = chunk.getPersistentDataContainer();

        return dataContainer.get(plugin.getChunkLastLoadedTimeKey(), PersistentDataType.LONG);
    }

    public Long getChunkAge(Chunk chunk) {
        YamlConfiguration configuration = (YamlConfiguration) plugin.getConfig();
        PassedTimeCalculationMode timePassedCalculationMode = PassedTimeCalculationMode.valueOf(configuration.getString("passed-time-calculation-mode"));

        switch (timePassedCalculationMode) {
            case CHUNK_LAST_LOADED -> {
                Long lastLoadedTime = getChunkLastLoadedTime(chunk);

                if (lastLoadedTime == null) {
                    return null;
                }

                return System.currentTimeMillis() - lastLoadedTime;
            }

            case WORLD_GENERATED -> {
                return (chunk.getWorld().getFullTime() / 20L) * 1000L;
            }
        }

        return null;
    }
}
