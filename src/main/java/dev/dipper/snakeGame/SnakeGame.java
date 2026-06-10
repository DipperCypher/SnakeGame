package dev.dipper.snakeGame;

import org.bukkit.plugin.java.JavaPlugin;

import dev.dipper.snakeGame.command.SnakeCommand;
import dev.dipper.snakeGame.events.SnakeListener;
import dev.dipper.snakeGame.manager.SnakeManager;

public final class SnakeGame extends JavaPlugin {
    private SnakeManager snakeManager;

    @Override
    public void onEnable() {
        snakeManager = new SnakeManager(this);

        getServer().getPluginManager().registerEvents(new SnakeListener(snakeManager, this), this);
        getCommand("snake").setExecutor(new SnakeCommand(snakeManager));
    }
}
