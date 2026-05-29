package dev.dipper.snakeGame.events;

import dev.dipper.snakeGame.SnakeGame;
import dev.dipper.snakeGame.manager.SnakeManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SnakeListener implements Listener {
    public SnakeManager smanager;
    public SnakeGame plugin;

    public SnakeListener(SnakeManager smanager, SnakeGame plugin) {
        this.smanager = smanager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!smanager.getStarted()) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String rawName = meta.getDisplayName();

        if (rawName.equals(smanager.getLeftItem())) {
            smanager.setDirection(smanager.getDirection().turnLeft());
            event.setCancelled(true);
            return;
        }

        if (rawName.equals(smanager.getRightItem())) {
            smanager.setDirection(smanager.getDirection().turnRight());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!smanager.getStarted()) return;
        event.setCancelled(true);
    }
}
