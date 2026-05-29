package dev.dipper.snakeGame.world;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import dev.dipper.snakeGame.SnakeGame;

public class WorldManager {
    private World world;
    private SnakeGame plugin;
    private String worldName = "snake_world";

    public WorldManager(SnakeGame plugin) {
        this.plugin = plugin;
        createWorld();
    }

    public void createWorld() {
        plugin.getLogger().info("Creating snake world...");

        WorldCreator create = new WorldCreator(worldName);
        create.generator(new VoidGenerator());
        world = create.createWorld();

        if (world != null) {
            plugin.getLogger().info("World object is not null!");

            world.setTime(6000);
            world.setStorm(false);

            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

            plugin.getLogger().info("Snake world " + worldName + " created successfully");
        } else {
            plugin.getLogger().severe("Failed to create snake world " + worldName);
        }
    }

    public World getWorld() {
        return world;
    }
}
