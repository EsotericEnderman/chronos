package net.slqmy.chronos.util;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChunkUtil {

    public static @NotNull List<Block> getChunkBlocks(@NotNull Chunk chunk) {
        List<Block> chunkBlocks = new ArrayList<>();

        final int minX = 0;
        final int maxX = 15;

        final int minZ = 0;
        final int maxZ = 15;

        final int minY = chunk.getWorld().getMinHeight();
        final int maxY = chunk.getWorld().getMaxHeight();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    chunkBlocks.add(chunk.getBlock(x, y, z));
                }
            }
        }

        return chunkBlocks;
    }
}
