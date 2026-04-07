package io.github.landoodle123.javaRPG;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.*;
import org.json.*;

import javax.swing.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

import static com.badlogic.gdx.Gdx.graphics;
import static io.github.landoodle123.javaRPG.npc.npcRectangle;
import static io.github.landoodle123.javaRPG.player.*;
import static io.github.landoodle123.javaRPG.npc.*;

public class Main extends ApplicationAdapter {

    static class Enemy {
        float x, y;
        float speedMultiplier = 1.5f;
        int health;
        boolean alive = true;
        Sprite sprite;
        Rectangle rectangle;

        // Enemy steps toward the player once every MOVE_INTERVAL seconds
        float moveTimer = 0f;
        static final float MOVE_INTERVAL = 0.55f;

        Enemy(float startX, float startY, Texture texture) {
            this.x = startX;
            this.y = startY;
            this.health = 15 + playerSword;   // scales with current sword level
            sprite = new Sprite(texture);
            sprite.setSize(1f, 1f);
            sprite.setPosition(x, y);
            rectangle = new Rectangle(x, y, 1f, 1f);
        }

        /** Advance pathfinding timer; move one step when interval elapses. */
        void update(float delta) {
            if (!alive) return;
            moveTimer += delta;
            if (moveTimer >= MOVE_INTERVAL * speedMultiplier) {
                moveTimer = 0f;
                stepTowardPlayer();
            }
            sprite.setPosition(x, y);
            rectangle.setPosition(x, y);
        }

        void draw(SpriteBatch batch) {
            if (alive) sprite.draw(batch);
        }

        // -----------------------------------------------------------------
        // Grid A* pathfinding
        // -----------------------------------------------------------------

        /**
         * Move the enemy one grid cell closer to the player.
         * The world is 8x8 integer cells; we snap positions to nearest int.
         */
        private void stepTowardPlayer() {
            int ex = Math.round(x);
            int ey = Math.round(y);
            int px = Math.round(playerCharacter.getX());
            int py = Math.round(playerCharacter.getY());
            if (ex == px && ey == py) return;

            int[] next = aStarNextStep(ex, ey, px, py);
            if (next != null) {
                x = next[0];
                y = next[1];
            }
        }

        /**
         * Grid A* on an 8x8 board.
         * Returns [nx, ny] of the first step from (sx,sy) toward (gx,gy),
         * or null when no path exists.
         */
        private static int[] aStarNextStep(int sx, int sy, int gx, int gy) {
            final int SIZE = 8;
            boolean[][] closed  = new boolean[SIZE][SIZE];
            int[][][]   parent  = new int[SIZE][SIZE][2];
            int[][]     gCost   = new int[SIZE][SIZE];
            int[][]     fCost   = new int[SIZE][SIZE];

            for (int[] row : gCost)  Arrays.fill(row, Integer.MAX_VALUE);
            for (int[] row : fCost)  Arrays.fill(row, Integer.MAX_VALUE);
            for (int[][] plane : parent) for (int[] cell : plane) Arrays.fill(cell, -1);

            gCost[sx][sy] = 0;
            fCost[sx][sy] = manhattan(sx, sy, gx, gy);

            List<int[]> open = new ArrayList<>();
            open.add(new int[]{sx, sy});

            int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

            while (!open.isEmpty()) {
                // Pop lowest fCost node
                int bi = 0;
                for (int i = 1; i < open.size(); i++) {
                    if (fCost[open.get(i)[0]][open.get(i)[1]] <
                        fCost[open.get(bi)[0]][open.get(bi)[1]]) bi = i;
                }
                int[] cur = open.remove(bi);
                int cx = cur[0], cy = cur[1];

                if (cx == gx && cy == gy) {
                    // Walk parent chain back to find the first step
                    int tx = cx, ty = cy;
                    while (true) {
                        int ppx = parent[tx][ty][0];
                        int ppy = parent[tx][ty][1];
                        if (ppx == sx && ppy == sy) return new int[]{tx, ty};
                        if (ppx == -1) return null;
                        tx = ppx; ty = ppy;
                    }
                }

                closed[cx][cy] = true;

                for (int[] d : dirs) {
                    int nx = cx + d[0];
                    int ny = cy + d[1];
                    if (nx < 0 || nx >= SIZE || ny < 0 || ny >= SIZE) continue;
                    if (closed[nx][ny]) continue;
                    // The goal cell (player position) is passable even if it looks like a wall
                    if (isWall(nx, ny) && !(nx == gx && ny == gy)) continue;

                    int ng = gCost[cx][cy] + 1;
                    if (ng < gCost[nx][ny]) {
                        parent[nx][ny][0] = cx;
                        parent[nx][ny][1] = cy;
                        gCost[nx][ny] = ng;
                        fCost[nx][ny] = ng + manhattan(nx, ny, gx, gy);
                        open.add(new int[]{nx, ny});
                    }
                }
            }
            return null;
        }

        private static int manhattan(int x1, int y1, int x2, int y2) {
            return Math.abs(x1 - x2) + Math.abs(y1 - y2);
        }

        /** True when the integer grid cell (cx, cy) is occupied by a wall. */
        private static boolean isWall(int cx, int cy) {
            for (Rectangle wr : wallRectangles) {
                if ((int) wr.getX() == cx && (int) wr.getY() == cy) return true;
            }
            return false;
        }
    }

    // =========================================================================
    // Fields
    // =========================================================================

    private SpriteBatch spriteBatch;
    BitmapFont font;
    Label.LabelStyle label1style;
    Label label1;

    private static Sprite npc;
    private Texture npcTexture;

    private FitViewport viewport;

    private static Sprite swordUpgrade;
    static Rectangle swordUpgradeRectangle;

    Texture backgroundTexture;
    Texture wallTexture;
    private static Sprite wall;
    static ArrayList<Rectangle> wallRectangles = new ArrayList<>();

    Texture doorTexture;
    static Sprite door;
    static Rectangle doorRectangle;

    // Enemy
    private Texture enemyTexture;
    static ArrayList<Enemy> enemies = new ArrayList<>();

    // Map
    static String[] maps = {"main.json", "dungeon.json", "main.json", "random.json"};
    static Integer currentLoadedMap = 0;

    // Game state
    static Integer playerHealth     = 100;
    static Integer playerSword      = 1;
    static Integer npcHealth        = 10;
    static Boolean npcAlive         = true;
    static Boolean swordUpgradeAvail = true;

    // Enemies are spawned exactly once per map load.
    // The instance flag is used in draw(); the static flag lets the static
    // changeMap() method signal the next frame that a spawn is needed.
    private boolean       pendingEnemySpawn       = true;
    private static boolean pendingEnemySpawnStatic = false;

    // Dialogue / threading
    static JFrame   f;
    public static Boolean stopt1 = false;
    static ExecutorService executor = Executors.newFixedThreadPool(3);

    /**
     * Single-threaded scheduler: once per second, check every living enemy
     * against the player rectangle and deal 7-15 damage on overlap.
     * Runs entirely off the render thread — no blocking.
     */
    private static final ScheduledExecutorService damageScheduler =
        Executors.newSingleThreadScheduledExecutor();

    // -------------------------
    // Runnables
    // -------------------------
    public static Runnable runTalk = () -> {
        try {
            while (!stopt1) {
                talk(dialogueOptions[ThreadLocalRandom.current().nextInt(0, 3)]);
            }
        } catch (InterruptedException e) {
            System.out.println("Failed with exception: " + e);
        }
    };

    // -------------------------
    // Lifecycle
    // -------------------------
    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        npcTexture = new Texture("guy.png");
        npc = new Sprite(npcTexture);
        npc.setSize(1, 1);
        npc.setY(2);

        try {
            font = new BitmapFont();
        } catch (Exception e) {
            System.out.println("font loading failed with exception: " + e);
        }
        try {
            label1style = new Label.LabelStyle();
            label1style.font = font;
            label1style.fontColor = Color.BLACK;
            label1 = new Label("HP: " + playerHealth.toString(), label1style);
            label1.setPosition(0, 8);
            label1.setAlignment(Align.center);
            label1.setSize(label1.getWidth(), 1);
        } catch (Exception e) {
            System.out.println("Label failed with exception: " + e);
        }

        Texture swordUpgradeTexture = new Texture("swordupgrade.png");
        swordUpgrade = new Sprite(swordUpgradeTexture);
        swordUpgrade.setSize(1, 1);

        try {
            charTexture = new Texture("player.png");
        } catch (Exception e) {
            System.out.println("player.png missing, using guy.png");
            charTexture = new Texture("guy.png");
        }
        playerCharacter = new Sprite(charTexture);
        playerCharacter.setSize(0.85f, 0.85f);

        viewport        = new FitViewport(8, 8);
        backgroundTexture = new Texture("grassbg.png");
        wallTexture     = new Texture("wall.png");
        wall            = new Sprite(wallTexture);
        wall.setSize(1, 1);

        doorTexture = new Texture("door.png");
        door = new Sprite(doorTexture);
        door.setSize(1, 1);

        enemyTexture = new Texture("evilman.png");

        // Non-blocking enemy damage — fires every second off the render thread
        damageScheduler.scheduleAtFixedRate(() -> {
            try {
                for (Enemy enemy : enemies) {
                    if (!enemy.alive) continue;
                    Rectangle pr = playerRectangle;   // read is effectively atomic
                    if (pr != null && enemy.rectangle.overlaps(pr)) {
                        int dmg = ThreadLocalRandom.current().nextInt(7, 16); // 7-15 inclusive
                        playerHealth -= dmg;
                        System.out.println("Enemy dealt " + dmg + " damage — player HP: " + playerHealth);
                        if (playerHealth <= 0) {
                            playerHealth = 0;
                            System.out.println("Player has died.");

                            try {
                                Gdx.app.postRunnable(respawnPlayer());
                            } catch (Exception e) {
                                System.out.println("Exception in respawnPlayer: " + e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Damage scheduler error: " + e);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
    @Override
    public void render() {
        // Sync the static spawn flag set by changeMap() into the instance field
        if (pendingEnemySpawnStatic) {
            pendingEnemySpawn       = true;
            pendingEnemySpawnStatic = false;
        }
        input();
        logic();
        try {
            draw();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        npcTexture.dispose();
        charTexture.dispose();
        enemyTexture.dispose();
        executor.shutdown();
        damageScheduler.shutdown();
    }

    // -------------------------
    // Input
    // -------------------------
    private void input() {
        float speed = 1f;
        float delta = graphics.getDeltaTime();
        if (playerHealth > 0) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && playerCharacter.getX() < 7) {
                playerCharacter.setX(playerCharacter.getX() + speed * delta);
                for (Rectangle wallRectangle : wallRectangles) {
                    if (playerRectangle.overlaps(wallRectangle) && wallRectangle.getX() != 0 && wallRectangle.getY() != 0) {
                        playerCharacter.setX(Math.round(playerCharacter.getX()) - speed * delta);
                        System.out.println("overlaps");
                        System.out.println("coords = " + wallRectangle.getX() + wallRectangle.getY());
                    }
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && playerCharacter.getX() > 0) {
                playerCharacter.setX(playerCharacter.getX() - speed * delta);
                for (Rectangle wallRectangle : wallRectangles) {
                    if (playerRectangle.overlaps(wallRectangle) && wallRectangle.getX() != 0 && wallRectangle.getY() != 0) {
                        playerCharacter.setX(Math.round(playerCharacter.getX()) + speed * delta);
                    }
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP) && playerCharacter.getY() < 7) {
                playerCharacter.setY(playerCharacter.getY() + speed * delta);
                for (Rectangle wallRectangle : wallRectangles) {
                    if (playerRectangle.overlaps(wallRectangle) && wallRectangle.getX() != 0 && wallRectangle.getY() != 0) {
                        playerCharacter.setY(Math.round(playerCharacter.getY()) - speed * delta);
                    }
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && playerCharacter.getY() > 0) {
                playerCharacter.setY(playerCharacter.getY() - speed * delta);
                for (Rectangle wallRectangle : wallRectangles) {
                    if (playerRectangle.overlaps(wallRectangle) && wallRectangle.getX() != 0 && wallRectangle.getY() != 0) {
                        playerCharacter.setY(Math.round(playerCharacter.getY()) + speed * delta);
                    }
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                System.out.println("space pressed");
                stopt1 = false;
                use();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
                System.out.println("moving to square 1, 2");
                playerCharacter.setX(1);
                playerCharacter.setY(2);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT)) {
                System.out.println("attacking");
                try {
                    attack();
                } catch (Exception e) {
                    System.out.println("attack failed with exception" + e);
                }
            }
        }
    }

    private void logic() {
        float delta = graphics.getDeltaTime();
        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }
    }

    public static Runnable respawnPlayer() throws InterruptedException {
        System.out.println("Respawning player...");

        // Reset stats
        playerHealth = 100;
        playerSword = 1;

        // Reset to first map
        changeMap(0);

        // Reset position (extra safety)
        playerCharacter.setPosition(0, 0);

        // Stop any dialogue
        stopt1 = true;
        talk("Player died");
        return null;
    }

    // =========================================================================
    // Drawing
    // =========================================================================

    private void draw() throws Exception {
        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, 8, 8);

        Integer[][] board = parseBoard(maps[currentLoadedMap]);
        wallRectangles.clear();

        // Collect tile-2 spawn positions only when a new map has just loaded
        List<int[]> spawnPoints = new ArrayList<>();

        for (int row = 0; row < board.length; row++) {
            int currentX = 0;
            int currentY = 7 - row;   // JSON row 0 = top of screen = y=7 in world space

            for (Integer tile : board[row]) {
                switch (tile) {
                    case 0 -> {}   // empty
                    case 1 -> {   // wall
                        wall.setX(currentX);
                        wall.setY(currentY);
                        wall.draw(spriteBatch);
                        wallRectangles.add(new Rectangle(currentX, currentY, 1, 1));
                    }
                    case 2 -> {   // enemy spawn point
                        // Record position; actual Enemy objects are created below
                        // only on the first frame after a map load.
                        if (pendingEnemySpawn) {
                            spawnPoints.add(new int[]{currentX, currentY});
                        }
                    }
                    case 3 -> {   // NPC
                        npc.setX(currentX);
                        npc.setY(currentY);
                    }
                    case 4 -> {   // sword upgrade
                        swordUpgrade.setX(currentX);
                        swordUpgrade.setY(currentY);
                    }
                    case 5 -> {   // door
                        door.setX(currentX);
                        door.setY(currentY);
                    }
                    default -> throw new Exception(
                        "Invalid tile value: " + tile + " at row " + row + ", col " + currentX);
                }
                currentX++;
            }
        }

        // Spawn enemies once per map load from every tile-2 position found above
        if (pendingEnemySpawn) {
            enemies.clear();
            for (int[] sp : spawnPoints) {
                enemies.add(new Enemy(sp[0], sp[1], enemyTexture));
            }
            pendingEnemySpawn = false;
            System.out.println("Spawned " + enemies.size() + " enemy/enemies for map: " + maps[currentLoadedMap]);
        }

        // Draw world objects
        if (npcAlive)          { npc.draw(spriteBatch); }
        door.draw(spriteBatch);
        if (swordUpgradeAvail) { swordUpgrade.draw(spriteBatch); }

        // Draw enemies (dead enemies are skipped inside draw())
        for (Enemy enemy : enemies) {
            enemy.draw(spriteBatch);
        }

        // Draw player on top
        if (playerHealth > 0) {
            playerCharacter.draw(spriteBatch);
        }

        // Rebuild collision rectangles every frame
        playerRectangle      = new Rectangle(playerCharacter.getX(), playerCharacter.getY(),
            playerCharacter.getWidth(), playerCharacter.getHeight());
        npcRectangle         = new Rectangle(npc.getX(), npc.getY(),
            npc.getWidth(), npc.getHeight());
        doorRectangle        = new Rectangle(door.getX(), door.getY(), 1, 1);
        swordUpgradeRectangle = new Rectangle(swordUpgrade.getX(), swordUpgrade.getY(),
            swordUpgrade.getWidth(), swordUpgrade.getHeight());
        spriteBatch.end();
        spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(
            0, 0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        ));

        spriteBatch.begin();
        font.draw(spriteBatch, "HP: " + playerHealth, 10, Gdx.graphics.getHeight() - 10);
        spriteBatch.end();
    }

    // =========================================================================
    // Map Parsing
    // =========================================================================

    public Integer[][] parseBoard(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONArray board = new JSONArray(content);
        Integer[][] rows = new Integer[board.length()][];

        for (int i = 0; i < board.length(); i++) {
            JSONArray jsonRow = board.getJSONArray(i);
            rows[i] = new Integer[jsonRow.length()];
            for (int j = 0; j < jsonRow.length(); j++) {
                rows[i][j] = jsonRow.getInt(j);
            }
        }
        return rows;
    }

    public static void changeMap(int newMapIndex) {
        if (newMapIndex >= maps.length) {
            System.out.println("No more maps to load.");
            return;
        }

        currentLoadedMap = newMapIndex;

        // Reset player to safe spawn position
        playerCharacter.setX(0);
        playerCharacter.setY(0);
        playerRectangle = new Rectangle(0, 0, playerCharacter.getWidth(), playerCharacter.getHeight());

        // Reset per-room game state
        npcAlive         = true;
        swordUpgradeAvail = true;
        npcHealth        = 10;

        // Clear stale wall data — draw() repopulates every frame
        wallRectangles.clear();

        // Clear enemies — draw() will re-spawn from tile-2 positions next frame
        enemies.clear();

        // Push old sprites off-screen to avoid one-frame ghost rendering
        npc.setPosition(-10, -10);
        swordUpgrade.setPosition(-10, -10);
        door.setPosition(-10, -10);

        npcRectangle          = new Rectangle(-10, -10, 1, 1);
        swordUpgradeRectangle = new Rectangle(-10, -10, 1, 1);
        doorRectangle         = new Rectangle(-10, -10, 1, 1);

        // Signal render() to set pendingEnemySpawn = true on the next frame.
        // (changeMap is static, so we use a static flag as a bridge.)
        pendingEnemySpawnStatic = true;

        stopt1 = true;
        System.out.println("Loaded map: " + maps[currentLoadedMap]);
    }
    // -------------------------
    // Actions
    // -------------------------
    public static void use() {
        if (playerRectangle.overlaps(npcRectangle)) {
            try {
                if (npcAlive) { executor.submit(runTalk); }
            } catch (Exception e) {
                System.out.println("Failed with exception: " + e);
            }
        } else if (playerRectangle.overlaps(swordUpgradeRectangle)) {
            if (playerSword < 10 && swordUpgradeAvail) {
                playerSword++;
                System.out.println("playerSword level is " + playerSword);
                swordUpgradeAvail = false;
            } else {
                System.out.println("sword is at max level or unavailable");
            }
        } else if (playerRectangle.overlaps(doorRectangle)) {
            changeMap(currentLoadedMap + 1);
            playerHealth = playerHealth + 20;
        } else {
            System.out.println("no overlap");
        }
    }

    public void attack() {
        // --- Hit NPC ---
        if (playerRectangle.overlaps(npcRectangle) && npcAlive) {
            System.out.println("NPC hit");
            spriteBatch.begin();
            charTexture = new Texture("stabbystab.png");
            playerCharacter.setTexture(charTexture);
            playerCharacter.draw(spriteBatch);
            charTexture = new Texture("player.png");
            playerCharacter.setTexture(charTexture);
            playerCharacter.draw(spriteBatch);
            spriteBatch.end();

            npcHealth -= playerSword;
            if (npcHealth <= 0) {
                npcAlive = false;
                System.out.println("NPC defeated.");
            }
        }

        // --- Hit enemies ---
        for (Enemy enemy : enemies) {
            if (!enemy.alive) continue;
            if (playerRectangle.overlaps(enemy.rectangle)) {
                enemy.health -= playerSword;
                System.out.println("Enemy hit — HP remaining: " + enemy.health);
                if (enemy.health <= 0) {
                    enemy.alive = false;
                    System.out.println("Enemy defeated.");
                }
            }
        }
    }

    public static void talk(String message) throws InterruptedException {
        JDialog d = new JDialog(f, "Conversation");
        int dialogueSelection = ThreadLocalRandom.current().nextInt(0, 3);
        JLabel l = new JLabel(String.format("<html><body style='width: 350px; align: center'><p>%s</p></body></html>", message));
        d.add(l);
        d.setSize(400, 400);
        d.setLocation(400, 400);
        d.pack();
        d.setVisible(true);
        TimeUnit.SECONDS.sleep(15);
        d.setVisible(false);
        stopt1 = true;
        d.dispose();
        System.out.println("If this message is showing something has probably gone wrong.");
    }
}
