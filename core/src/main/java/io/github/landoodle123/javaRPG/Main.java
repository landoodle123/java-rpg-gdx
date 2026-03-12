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

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

import static com.badlogic.gdx.Gdx.graphics;
import static io.github.landoodle123.javaRPG.npc.npcRectangle;
import static io.github.landoodle123.javaRPG.player.*;
import static io.github.landoodle123.javaRPG.npc.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
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
    ArrayList<Sprite> walls = new ArrayList<Sprite>();
    ArrayList<Rectangle> wallRectangles = new ArrayList<>();
    static JFrame f;
    public static Boolean stopt1 = false;
    Rectangle wallRectangle;
    Integer numOfTotalWalls = 0;
    static ExecutorService executor = Executors.newFixedThreadPool(3);
    public static Runnable runTalk = () -> {
        try {
            while (!stopt1) {
                talk();
            }
        } catch (InterruptedException e) {
            System.out.println("Failed with exception: " + e);
        }
    };

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
        try{charTexture = new Texture("player.png");} catch (Exception e) {
            System.out.println("Error occurred loading player.png, loaded guy.png instead."); //who at oracle thought that "System.out.println()" was a good name for a function used as commonly as printing to stdio
            charTexture = new Texture("guy.png");
        } //TODO: make player character
        playerCharacter = new Sprite(charTexture);
        playerCharacter.setSize(0.85F, 0.85F);
        viewport = new FitViewport(8,8);
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
        draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }
    public static void use() {
        //TODO: add door logic
        if (playerRectangle.overlaps(npcRectangle)) {
            try {
                if(npcAlive) {executor.submit(runTalk);}
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
    private void input() {
        float speed = 1f;
        float delta = graphics.getDeltaTime();
        //Boolean overlapOnce = true;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && playerCharacter.getX() < 7) {
            playerCharacter.setX(playerCharacter.getX() + speed * delta);
            for (Rectangle wallRectangle : wallRectangles) {
                if (playerRectangle.overlaps(wallRectangle) && /**overlapOnce &&**/ wallRectangle.getX() != 0 && wallRectangle.getY() != 0) {
                    playerCharacter.setX(Math.round(playerCharacter.getX()) - speed * delta);
                    System.out.println("overlaps");
                    System.out.println("coords = " + wallRectangle.getX() + wallRectangle.getY());
                    //overlapOnce = false;
                }
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && playerCharacter.getX() > 0) {
            playerCharacter.setX(playerCharacter.getX() - speed * delta);
            for (Rectangle wallRectangle : wallRectangles) {
                if (playerRectangle.overlaps(wallRectangle) && /**overlapOnce &&**/ wallRectangle.getX() != 0 && wallRectangle.getY() != 0) {
                    playerCharacter.setX(Math.round(playerCharacter.getX()) + speed * delta);
                    //overlapOnce = false;
                }
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && playerCharacter.getY() < 7) {
            playerCharacter.setY(playerCharacter.getY() + speed * delta);
            for (Rectangle wallRectangle : wallRectangles) {
                if (playerRectangle.overlaps(wallRectangle) && /**overlapOnce &&**/ wallRectangle.getX() != 0 && wallRectangle.getY() != 0) {
                    playerCharacter.setY(Math.round(playerCharacter.getY()) - speed * delta);
                    //overlapOnce = false;
                }
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && playerCharacter.getY() > 0) {
            playerCharacter.setY(playerCharacter.getY() - speed * delta);
            for (Rectangle wallRectangle : wallRectangles) {
                if (playerRectangle.overlaps(wallRectangle) && /**overlapOnce &&**/ wallRectangle.getX() != 0 && wallRectangle.getY() != 0) {
                    playerCharacter.setY(Math.round(playerCharacter.getY()) + speed * delta);
                    //overlapOnce = false;

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

            // create a label
            JLabel l = new JLabel(String.format("<html><body style='width: 350px; align: center'<p>%s</p></html>", dialogueOptions[dialogueSelection]));

            d.add(l);

            // setsize of dialog
            d.setSize(400, 400);

            d.setLocation(400, 400);

            // set visibility of dialog
            d.pack();
            d.setVisible(true);
            TimeUnit.SECONDS.sleep(15);
            d.setVisible(false);
            stopt1 = true;
            d.dispose();
            System.out.println("If this message is showing something has probably gone wrong.");
    }
    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float playerHeight = playerCharacter.getWidth();
        float playerWidth = playerCharacter.getWidth();
        float npcWidth = npc.getWidth();
        float npcHeight = npc.getWidth();



    }

    private void draw() {
        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, 8, 8);

        Boolean[] row1 = {false, true, false, true, false, true, false, true};
        Boolean[] row2 = {true, false, true, false, true, false, true, false};
        Boolean[] row3 = {false, true, false, true, false, true, false, true};
        Boolean[] row4 = {true, false, true, false, true, false, true, false};
        Boolean[] row5 = {false, true, false, true, false, true, false, true};
        Boolean[] row6 = {false, false, false, false, true, false, true, false};
        Boolean[] row7 = {false, false, false, true, false, true, false, true};
        Boolean[] row8 = {false, false, false, false, true, false, true, false};
        int currentX = 0;
        int currentY = 7;


        for(Boolean individualWall : row1) {
            currentY = 7;
            if (individualWall) {
                numOfTotalWalls++;
                wall.draw(spriteBatch);
                wallRectangle = new Rectangle(wall.getX(), wall.getY(), 1, 1);
                wallRectangles.add(wallRectangle);
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            } else {
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row2) {
            currentY = 6;
            if (individualWall) {
                numOfTotalWalls++;
                wall.draw(spriteBatch);
                wallRectangle = new Rectangle(wall.getX(), wall.getY(), 1, 1);
                wallRectangles.add(wallRectangle);
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }else {
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row3) {
            currentY = 5;
            if (individualWall) {
                numOfTotalWalls++;
                wall.draw(spriteBatch);
                wallRectangle = new Rectangle(wall.getX(), wall.getY(), 1, 1);
                wallRectangles.add(wallRectangle);
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }else {
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row4) {
            currentY = 4;
            if (individualWall) {
                numOfTotalWalls++;
                wall.draw(spriteBatch);
                wallRectangle = new Rectangle(wall.getX(), wall.getY(), 1, 1);
                wallRectangles.add(wallRectangle);
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }else {
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row5) {
            currentY = 3;
            if (individualWall) {
                numOfTotalWalls++;
                wall.draw(spriteBatch);
                wallRectangle = new Rectangle(wall.getX(), wall.getY(), 1, 1);
                wallRectangles.add(wallRectangle);
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }else {
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row6) {
            currentY = 2;
            if (individualWall) {
                numOfTotalWalls++;
                wall.draw(spriteBatch);
                wallRectangle = new Rectangle(wall.getX(), wall.getY(), 1, 1);
                wallRectangles.add(wallRectangle);
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }else {
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row7) {
            currentY = 1;
            if (individualWall) {
                numOfTotalWalls++;
                wall.draw(spriteBatch);
                wallRectangle = new Rectangle(wall.getX(), wall.getY(), 1, 1);
                wallRectangles.add(wallRectangle);
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }else {
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row8) {
            currentY = 0;
            if (individualWall) {
                numOfTotalWalls++;
                wall.draw(spriteBatch);
                wallRectangle = new Rectangle(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
                wallRectangles.add(wallRectangle);
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }else {
                currentX++;
            }
        }


        if (npcAlive) {npc.draw(spriteBatch);}
        playerCharacter.draw(spriteBatch);
        playerRectangle = new Rectangle(playerCharacter.getX(), playerCharacter.getY(), playerCharacter.getWidth(), playerCharacter.getHeight());
        npcRectangle = new Rectangle(npc.getX(), npc.getY(), npc.getWidth(), npc.getHeight());
        wallRectangle = new Rectangle(wall.getX(), wall.getY(), 1, 1);
        swordUpgrade.draw(spriteBatch);
        swordUpgrade.setX(2);
        swordUpgrade.setY(1);
        swordUpgradeRectangle = new Rectangle(swordUpgrade.getX(), swordUpgrade.getY(), swordUpgrade.getWidth(), swordUpgrade.getHeight());


        spriteBatch.end();
    }

    @Override
    public void dispose() {
       spriteBatch.dispose();
       npcTexture.dispose();
       charTexture.dispose();
       executor.shutdown();
    }
}
