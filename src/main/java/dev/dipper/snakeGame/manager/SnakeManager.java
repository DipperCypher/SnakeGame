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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import dev.dipper.snakeGame.SnakeGame;
import dev.dipper.snakeGame.direction.Direction;
import net.md_5.bungee.api.ChatColor;

public class SnakeManager {
    private final String TURN_LEFT_ITEM_NAME = ChatColor.GREEN + "Snake Turn Left";
    private final String TURN_RIGHT_ITEM_NAME = ChatColor.GREEN + "Snake Turn Right";

    private final Material WALL_MATERIAL = Material.BLACK_CONCRETE;
    private final Material BOARD_MATERIAL = Material.LIGHT_BLUE_CONCRETE;
    private final Material SNAKE_HEAD_MATERIAL = Material.LIME_CONCRETE;
    private final Material SNAKE_TAIL_MATERIAL = Material.GREEN_CONCRETE;
    private final Material FOOD_MATERIAL = Material.RED_CONCRETE;

    private List<Location> snake = new ArrayList<>();
    private Direction direction = Direction.UP;
    private Location food;
    private boolean started;
    private BukkitTask gameTask;
    private SnakeGame plugin;
    private BukkitTask countdownTask;
    private int countDown = 5;

    private int boardSize = 21;
    private Location origin;

    public SnakeManager(SnakeGame plugin) {
        this.plugin = plugin;
    }

    public void startGame(Player player) {
        if (gameTask != null) gameTask.cancel();
        if (countdownTask != null) countdownTask.cancel();
        started = true;

        Location playerLoc = player.getLocation();
        Location ground = playerLoc.clone().add(0, 20, 0);

        Location sky = ground.clone().add(0, 100, 0);
        player.setAllowFlight(true);
        player.setFlying(true);

        Location lookDown = sky.clone();
        lookDown.setPitch(90);
        player.teleport(lookDown);

        int center = (boardSize - 1) / 2;
        origin = player.getLocation().clone().subtract(center, 5, center);

        snake.clear();
        snake.add(origin.clone().add(7, 0, 7));
        snake.add(origin.clone().add(6, 0, 7));
        snake.add(origin.clone().add(5, 0, 7));

        direction = Direction.UP;
        giveControlItems(player);
        spawnFood();
        drawBoard();
        startCountDown(player);
    }

    private void startCountDown(Player player) {
        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (countDown == 0) {
                    player.sendMessage(ChatColor.GREEN + "GOOO");
                    gameTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> tickTask(player), 0L, 8L);
                    countDown = 5;
                    countdownTask.cancel();
                    return;
                }

                player.sendMessage(ChatColor.AQUA + "Starting in... " + countDown);
                countDown--;
            }
        }.runTaskTimer(plugin, 0L, 9L);
    }

    private ItemStack createControlItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        return item;
    }

    private void tickTask(Player player) {
        Location head = snake.get(0).clone().add(direction.dx, 0, direction.dz);

        if (isWall(head) || containSnake(head)) {
            gameOver(player);
            return;
        }

        snake.add(0, head);
        boolean ate = sameBlock(head, food);

        if (!ate) {
            Location tail = snake.remove(snake.size() - 1);
            tail.getBlock().setType(BOARD_MATERIAL);
        } else {
            spawnFood();
        }

        drawBoard();
    }

    private void drawBoard() {
        for (int x = 0; x < boardSize; x++) {
            for (int z = 0; z < boardSize; z++) {
                Location block = origin.clone().add(x, 0, z);
                boolean border = x == 0 || z == 0 || x == boardSize -1 || z == boardSize -1;

                block.getBlock().setType(border ? WALL_MATERIAL : BOARD_MATERIAL);
            }
        }

        for (int i = 0; i < snake.size(); i++) {
            Material material = (i == 0) ? SNAKE_HEAD_MATERIAL : SNAKE_TAIL_MATERIAL;
            snake.get(i).getBlock().setType(material);
        }

        if (food != null) {
            food.getBlock().setType(FOOD_MATERIAL);
        }
    }

    private void spawnFood() {
        Random random = new Random();

        do {
            int x = random.nextInt(boardSize - 2) + 1;
            int z = random.nextInt(boardSize - 2) + 1;
            food = origin.clone().add(x, 0, z);
        } while (containSnake(food));
    }

    private boolean isWall(Location location) {
        int x = location.getBlockX() - origin.getBlockX();
        int z = location.getBlockZ() - origin.getBlockZ();
        return x <= 0 || z <= 0 || x >= boardSize -1 || z >= boardSize -1;
    }

    private void gameOver(Player player) {
        started = false;
        gameTask.cancel();
        countdownTask.cancel();

        player.setFlying(false);
        player.setAllowFlight(false);
        player.sendMessage(ChatColor.RED + "Game Over Score: " + (snake.size() - 3));
    }

    private void giveControlItems(Player player) {
        player.getInventory().setItem(0, createControlItem(Material.LIME_DYE, TURN_LEFT_ITEM_NAME));
        player.getInventory().setItem(1, createControlItem(Material.GREEN_DYE, TURN_RIGHT_ITEM_NAME));
    }

    public boolean containSnake(Location location) {
        return snake.stream().anyMatch(part -> sameBlock(part, location));
    }

    public boolean sameBlock(Location a, Location b) {
        return a.getBlockX() == b.getBlockX() 
        && a.getBlockY() == b.getBlockY() 
        && a.getBlockZ() == b.getBlockZ();
    }

    public List<Location> getSnake() {
        return snake;
    }
    
    public boolean getStarted() {
        return started;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getLeftItem() {
        return TURN_LEFT_ITEM_NAME;
    }

    public String getRightItem() {
        return TURN_RIGHT_ITEM_NAME;
    }
}
