package dev.dipper.snakeGame;

import org.bukkit.plugin.java.JavaPlugin;

import dev.dipper.snakeGame.command.SnakeCommand;
import dev.dipper.snakeGame.manager.SnakeManager;
import dev.dipper.snakeGame.move.HotBarListener;

public final class SnakeGame extends JavaPlugin {
    public SnakeManager snakeManager;

    @Override
    public void onEnable() {
        snakeManager = new SnakeManager(this);

        getServer().getPluginManager().registerEvents(new HotBarListener(snakeManager, this), this);

        getCommand("start").setExecutor(new SnakeCommand(snakeManager));
    }
}
