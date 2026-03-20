package io.github.landoodle123.javaRPG;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.*;
import org.json.*;

import javax.swing.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

import static com.badlogic.gdx.Gdx.graphics;
import static io.github.landoodle123.javaRPG.npc.npcRectangle;
import static io.github.landoodle123.javaRPG.player.*;
import static io.github.landoodle123.javaRPG.npc.*;

public class Main extends ApplicationAdapter {

    // -------------------------
    // Fields
    // -------------------------
    private SpriteBatch spriteBatch;
    private static Sprite npc;
    private Texture npcTexture;
    private FitViewport viewport;
    private static Sprite swordUpgrade;
    private Texture swordUpgradeTexture;
    private Integer playerHealth;
    static Integer playerSword;
    Texture backgroundTexture;
    static Rectangle swordUpgradeRectangle;
    Texture wallTexture;
    Integer npcHealth;
    static Boolean npcAlive;
    private static Sprite wall;
    ArrayList<Sprite> walls = new ArrayList<>();
    ArrayList<Rectangle> wallRectangles = new ArrayList<>();
    static JFrame f;
    public static Boolean stopt1 = false;
    Rectangle wallRectangle;
    Integer numOfTotalWalls = 0;
    String currentLoadedMap = "main.json";
    static ExecutorService executor = Executors.newFixedThreadPool(3);

    // -------------------------
    // Runnables
    // -------------------------
    public static Runnable runTalk = () -> {
        try {
            while (!stopt1) {
                talk();
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

        swordUpgradeTexture = new Texture("swordupgrade.png");
        swordUpgrade = new Sprite(swordUpgradeTexture);
        swordUpgrade.setSize(1, 1);

        try {
            charTexture = new Texture("player.png");
        } catch (Exception e) {
            System.out.println("Error occurred loading player.png, loaded guy.png instead.");
            charTexture = new Texture("guy.png");
        }
        playerCharacter = new Sprite(charTexture);
        playerCharacter.setSize(0.85F, 0.85F);

        viewport = new FitViewport(8, 8);
        backgroundTexture = new Texture("grassbg.png");

        wallTexture = new Texture("wall.png");
        wall = new Sprite(wallTexture);
        wall.setSize(1, 1);

        playerHealth = 100;
        playerSword = 1;
        npcHealth = 10;
        npcAlive = true;
    }

    @Override
    public void render() {
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
        executor.shutdown();
    }

    // -------------------------
    // Input
    // -------------------------
    private void input() {
        float speed = 1f;
        float delta = graphics.getDeltaTime();

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

    // -------------------------
    // Logic
    // -------------------------
    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float playerHeight = playerCharacter.getWidth();
        float playerWidth = playerCharacter.getWidth();
        float npcWidth = npc.getWidth();
        float npcHeight = npc.getWidth();
    }

    // -------------------------
    // Drawing
    // -------------------------
    private void draw() throws Exception {
        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, 8, 8);

        Integer[][] board = parseBoard(currentLoadedMap);
        wallRectangles.clear();

        for (int row = 0; row < board.length; row++) {
            int currentX = 0;
            int currentY = 7 - row;

            for (Integer tile : board[row]) {
                switch (tile) {
                    case 0 -> {} // empty
                    case 1 -> { // wall
                        numOfTotalWalls++;
                        wall.setX(currentX);
                        wall.setY(currentY);
                        wall.draw(spriteBatch);
                        wallRectangles.add(new Rectangle(currentX, currentY, 1, 1));
                    }
                    case 2 -> {} // enemy (not implemented)
                    case 3 -> { // npc
                        npc.setX(currentX);
                        npc.setY(currentY);
                    }
                    case 4 -> { // sword upgrade
                        swordUpgrade.setX(currentX);
                        swordUpgrade.setY(currentY);
                    }
                    case 5 -> {} // door (not implemented)
                    default -> throw new Exception("Invalid tile value: " + tile + " at row " + row + ", col " + currentX);
                }
                currentX++;
            }
        }

        if (npcAlive) { npc.draw(spriteBatch); }
        playerCharacter.draw(spriteBatch);
        swordUpgrade.draw(spriteBatch);

        playerRectangle = new Rectangle(playerCharacter.getX(), playerCharacter.getY(), playerCharacter.getWidth(), playerCharacter.getHeight());
        npcRectangle = new Rectangle(npc.getX(), npc.getY(), npc.getWidth(), npc.getHeight());
        wallRectangle = new Rectangle(wall.getX(), wall.getY(), 1, 1);
        swordUpgradeRectangle = new Rectangle(swordUpgrade.getX(), swordUpgrade.getY(), swordUpgrade.getWidth(), swordUpgrade.getHeight());

        spriteBatch.end();
    }

    // -------------------------
    // Map Parsing
    // -------------------------
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
            if (playerSword < 10) {
                playerSword++;
                System.out.println("playerSword level is " + playerSword);
            } else {
                System.out.println("sword is at max level");
            }
        } else {
            System.out.println("no overlap");
        }
    }

    public void attack() throws InterruptedException {
        if (playerRectangle.overlaps(npcRectangle)) {
            System.out.println("NPC hit");
            spriteBatch.begin();
            charTexture = new Texture("stabbystab.png");
            playerCharacter.draw(spriteBatch);
            TimeUnit.SECONDS.sleep(1);
            charTexture = new Texture("player.png");
            playerCharacter.draw(spriteBatch);
            spriteBatch.end();
            if (npcAlive) {
                npcHealth = npcHealth - playerSword;
                if (npcHealth <= 0) {
                    npcAlive = false;
                    System.out.println("npc died lol skill issue");
                }
            }
        }
    }

    public static void talk() throws InterruptedException {
        JDialog d = new JDialog(f, "Conversation");
        int dialogueSelection = ThreadLocalRandom.current().nextInt(0, 3);
        JLabel l = new JLabel(String.format("<html><body style='width: 350px; align: center'><p>%s</p></body></html>", dialogueOptions[dialogueSelection]));
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
