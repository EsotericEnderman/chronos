package net.slqmy.chronos;

import net.slqmy.chronos.listener.ChunkLoadListener;
import net.slqmy.chronos.listener.ChunkUnloadListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChronosPlugin extends JavaPlugin {

    private  NamespacedKey chunkLastLoadedTimeKey;

    public NamespacedKey getChunkLastLoadedTimeKey() {
        return chunkLastLoadedTimeKey;
    }

    @Override
    public void onEnable() {
        chunkLastLoadedTimeKey = new NamespacedKey(this, "last_loaded_time");

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new ChunkUnloadListener(this), this);
        pluginManager.registerEvents(new ChunkLoadListener(this), this);
    }
}
