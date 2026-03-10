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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
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
    Integer playerHealth;
    static Integer playerSword;
    Texture backgroundTexture;
    static Rectangle swordUpgradeRectangle;
    Texture wallTexture;
    private static Sprite wall;
    ArrayList<Sprite> walls = new ArrayList<Sprite>();
    ArrayList<Rectangle> wallRectangles = new ArrayList<Rectangle>();
    static JFrame f;
    public static Boolean stopt1 = false;
    Rectangle wallRectangle;
    Integer numOfTotalWalls = 0;
    static ExecutorService executor = Executors.newFixedThreadPool(3);
    public static Runnable runTalk = new Runnable() {

        @Override
        public void run() {
            try {
                while (!stopt1) {
                    talk();
                }
            } catch (InterruptedException e) {
                System.out.println("Failed with exception: " + e);
            }
        }
    };
    //ShapeRenderer npcShape = new ShapeRenderer();
    //ShapeRenderer playerShape = new ShapeRenderer();
    /**static Thread t1 = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!stopt1) {
                try {
                    talk();
                } catch (Exception e) {
                    System.out.println("Talk failed with exception " + e);
                }
            }
        }
    });**/

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        npcTexture = new Texture("guy.png");
        npc = new Sprite(npcTexture);
        npc.setSize(1, 1);
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


    }

    /**public void wall() {
        Boolean[] row1 = {false, true, false, true, false, true, false, true};
        Boolean[] row2 = {true, false, true, false, true, false, true, false};
        Boolean[] row3 = {false, true, false, true, false, true, false, true};
        Boolean[] row4 = {true, false, true, false, true, false, true, false};
        Boolean[] row5 = {false, true, false, true, false, true, false, true};
        Boolean[] row6 = {true, false, true, false, true, false, true, false};
        Boolean[] row7 = {false, true, false, true, false, true, false, true};
        Boolean[] row8 = {false, false, true, false, true, false, true, false};
        Integer currentX = 0;
        Integer currentY = 7;


        for(Boolean individualWall : row1) {
            if (individualWall) {
                numOfTotalWalls++;
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row2) {
            if (individualWall) {
                numOfTotalWalls++;
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row3) {
            if (individualWall) {
                numOfTotalWalls++;
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row4) {
            if (individualWall) {
                numOfTotalWalls++;
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row5) {
            if (individualWall) {
                numOfTotalWalls++;
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row6) {
            if (individualWall) {
                numOfTotalWalls++;
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row7) {
            if (individualWall) {
                numOfTotalWalls++;
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }
        }
        currentX = 0;
        for(Boolean individualWall : row8) {
            if (individualWall) {
                numOfTotalWalls++;
                wall.setX(currentX);
                wall.setY(currentY);
                currentX++;
            }
        }
    }**/

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
        //TODO: Add logic for use, either picking up an item or interacting with an npc or door
        /**if (playerRectangle.overlaps(npcRectangle) && t1.getState() == Thread.State.NEW) {
            t1.start();
        } else if (playerRectangle.overlaps(npcRectangle) && t1.getState() == Thread.State.TERMINATED) {
            System.out.println("Thread was terminated");
            try {t1.start();} catch (Exception e) {
                System.out.println("Thread failed to start with exception: " + e);
            }
        } else {
            if (t1.getState() != Thread.State.NEW) {
                System.out.println("thread already running or in broken state");
                System.out.println(t1.getState() + " = t1 state");
            } else {
                System.out.println("Player not overlapping anything.");
                System.out.println(t1.getState() + " = t1 state");
            }
        }**/
        if (playerRectangle.overlaps(npcRectangle)) {
            try {
                executor.submit(runTalk);
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

        //playerRectangle.setX(MathUtils.clamp(playerCharacter.getX(), 0, worldWidth - playerWidth));
        //playerRectangle.setY(MathUtils.clamp(playerCharacter.getY(), 0, worldHeight - playerHeight));
        //npcRectangle.setX(MathUtils.clamp(npc.getX(), 0, worldWidth - npcWidth));
        //npcRectangle.setY(MathUtils.clamp(npc.getY(), 0, worldHeight - npcHeight));



    }

    private void draw() {
        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, 8, 8);
        /**wall();
        for(int j = 0; j < numOfTotalWalls; j++) {
            System.out.println("drawing walls");
            for (int k = 0; k < numOfTotalWalls; k++) {
                walls.add(wall);
            }

            for(int l = 0; l < walls.size(); l++) {
                wall.draw(spriteBatch);
                spriteBatch.draw(wallTexture, 1, 1, 1, 1);
            }
            wall();
        }**/

        Boolean[] row1 = {false, true, false, true, false, true, false, true};
        Boolean[] row2 = {true, false, true, false, true, false, true, false};
        Boolean[] row3 = {false, true, false, true, false, true, false, true};
        Boolean[] row4 = {true, false, true, false, true, false, true, false};
        Boolean[] row5 = {false, true, false, true, false, true, false, true};
        Boolean[] row6 = {false, false, false, false, true, false, true, false};
        Boolean[] row7 = {false, false, false, true, false, true, false, true};
        Boolean[] row8 = {false, false, false, false, true, false, true, false};
        Integer currentX = 0;
        Integer currentY = 7;


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


        npc.draw(spriteBatch); // Sprites have their own draw method
        playerCharacter.draw(spriteBatch);
        playerRectangle = new Rectangle(playerCharacter.getX(), playerCharacter.getY(), playerCharacter.getWidth(), playerCharacter.getHeight());
        npcRectangle = new Rectangle(npc.getX(), npc.getY(), npc.getWidth(), npc.getHeight());
        wallRectangle = new Rectangle(wall.getX(), wall.getY(), 1, 1);
        swordUpgrade.draw(spriteBatch);
        swordUpgrade.setX(2);
        swordUpgrade.setY(1);
        swordUpgradeRectangle = new Rectangle(swordUpgrade.getX(), swordUpgrade.getY(), swordUpgrade.getWidth(), swordUpgrade.getHeight());

        /**npcShape.begin(ShapeRenderer.ShapeType.Line);
        npcShape.setColor(Color.BLACK);
        npcShape.rect(npc.getX(), npc.getY(), npc.getWidth(), npc.getHeight());
        playerShape.begin(ShapeRenderer.ShapeType.Line);
        playerShape.setColor(Color.BLACK);
        npcShape.rect(playerCharacter.getX(), playerCharacter.getY(), playerCharacter.getWidth(), playerCharacter.getHeight());
        **/


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
