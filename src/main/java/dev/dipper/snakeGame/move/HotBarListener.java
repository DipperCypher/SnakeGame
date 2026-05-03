package dev.dipper.snakeGame.move;

import dev.dipper.snakeGame.SnakeGame;
import dev.dipper.snakeGame.manager.SnakeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HotBarListener implements Listener {
    public SnakeManager manager;
    public SnakeGame plugin;

    public HotBarListener(SnakeManager manager, SnakeGame plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!manager.started) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        if (meta.getDisplayName().equals(manager.TURN_LEFT_ITEM_NAME)) {
            manager.direction = manager.direction.turnLeft();
            event.setCancelled(true);
            return;
        }

        if (meta.getDisplayName().equals(manager.TURN_RIGHT_ITEM_NAME)) {
            manager.direction = manager.direction.turnRight();
            event.setCancelled(true);
        }
    }
}
