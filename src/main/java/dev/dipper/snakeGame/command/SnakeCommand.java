package dev.dipper.snakeGame.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.dipper.snakeGame.manager.SnakeManager;
import net.md_5.bungee.api.ChatColor;

public class SnakeCommand implements CommandExecutor {
    public SnakeManager manager;

    public SnakeCommand(SnakeManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("snake.start")) {
            player.sendMessage(ChatColor.RED + "You don't have access");
            return true;
        }

        manager.startGame(player);
        player.sendMessage(ChatColor.AQUA + "Game Started, Have fun ;)");
        return true;
    }
}
