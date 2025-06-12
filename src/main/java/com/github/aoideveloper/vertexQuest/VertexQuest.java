package com.github.aoideveloper.vertexQuest;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * VertexQuest Entry class.
 */
public final class VertexQuest extends JavaPlugin {
    public static Plugin plugin;
    public static Server server;

    @Override
    public void onEnable() {
        plugin = this;
        server = getServer();
    }
}
