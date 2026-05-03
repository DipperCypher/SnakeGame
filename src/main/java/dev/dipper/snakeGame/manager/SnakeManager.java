package dev.dipper.snakeGame.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import dev.dipper.snakeGame.SnakeGame;
import dev.dipper.snakeGame.direction.Direction;
import net.md_5.bungee.api.ChatColor;

public class SnakeManager {
    public final String TURN_LEFT_ITEM_NAME = ChatColor.GREEN + "Snake Turn Left";
    public final String TURN_RIGHT_ITEM_NAME = ChatColor.GREEN + "Snake Turn Right";

    private final Material WALL_MATERIAL = Material.BLACK_CONCRETE;
    private final Material BOARD_MATERIAL = Material.LIGHT_BLUE_CONCRETE;
    private final Material SNAKE_HEAD_MATERIAL = Material.LIME_CONCRETE;
    private final Material SNAKE_TAIL_MATERIAL = Material.GREEN_CONCRETE;
    private final Material FOOD_MATERIAL = Material.RED_CONCRETE;

    public List<Location> snake = new ArrayList<>();
    public Direction direction = Direction.UP;
    public Location food;
    public boolean started;
    public BukkitTask task;
    public SnakeGame plugin;

    public int boardSize = 17;
    public Location origin;

    public SnakeManager(SnakeGame plugin) {
        this.plugin = plugin;
    }

    // Main handel starting the game
    public void startGame(Player player) {
        if (task != null) task.cancel();

        started = true;
        origin = player.getLocation().getBlock().getLocation().add(0, -1, 0);

        snake.clear();
        snake.add(origin.clone().add(7, 0, 7));
        snake.add(origin.clone().add(6, 0, 7));
        snake.add(origin.clone().add(5, 0, 7));

        direction = Direction.UP;
        giveControlItems(player);
        spawnFood();
        drawBoard();

        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tickTask, 0L, 9L);
    }

    // Main handel giving control items to player
    public void giveControlItems(Player player) {
        player.getInventory().setItem(0, createControlItem(Material.LIME_DYE, TURN_LEFT_ITEM_NAME));
        player.getInventory().setItem(1, createControlItem(Material.GREEN_DYE, TURN_RIGHT_ITEM_NAME));
    }

    // Main handel creating control items
    private ItemStack createControlItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        return item;
    }

    // Main handel the game tick
    private void tickTask() {
        Location head = snake.get(0).clone().add(direction.dx, 0, direction.dz);

        if (isWall(head) || containSnake(head)) {
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "Game Over Score: " + (snake.size() - 3));
            }

            started = false;
            task.cancel();
            return;
        }

        snake.add(0, head);

        if (sameBlock(head, food)) {
            spawnFood();
        } else {
            Location tail = snake.remove(snake.size() - 1);
            tail.getBlock().setType(BOARD_MATERIAL);
        }

        drawBoard();

    }

    // Main handel drawing the board
    public void drawBoard() {
        for (int x = 0; x < boardSize; x++) {
            for (int z = 0; z < boardSize; z++) {
                Location block = origin.clone().add(x, 0, z);
                boolean border = x == 0 || z == 0 || x == boardSize -1 || z == boardSize -1;

                if (border) {
                    block.getBlock().setType(WALL_MATERIAL);
                } else {
                    block.getBlock().setType(BOARD_MATERIAL);
                }
            }
        }

        for (int i = 0; i < snake.size(); i++) {
            Material material = i == 0 ? SNAKE_HEAD_MATERIAL : SNAKE_TAIL_MATERIAL;
            snake.get(i).getBlock().setType(material);
        }

        food.getBlock().setType(FOOD_MATERIAL);
    }

    // Main handel spawning food
    public void spawnFood() {
        Random random = new Random();

        do {
            int x = random.nextInt(boardSize - 2) + 1;
            int z = random.nextInt(boardSize - 2) + 1;
            food = origin.clone().add(x, 0, z);
        } while (containSnake(food));
    }

    // Main handel checking if the location is wall
    public boolean isWall(Location location) {
        int x = location.getBlockX() - origin.getBlockX();
        int z = location.getBlockZ() - origin.getBlockZ();
        return x <= 0 || z <= 0 || x >= boardSize -1 || z >= boardSize -1;
    }

    // Main handel checking if the location contain snake
    public boolean containSnake(Location location) {
        return snake.stream().anyMatch(part -> sameBlock(part, location));
    }

    // Main handel checking if two location is the same block
    public boolean sameBlock(Location a, Location b) {
        return a.getBlockX() == b.getBlockX() 
        && a.getBlockY() == b.getBlockY() 
        && a.getBlockZ() == b.getBlockZ();
    }

    // Getters
    public List<Location> getSnake() {
        return snake;
    }
    
    // Getters
    public boolean getStarted() {
        return started;
    }

}
