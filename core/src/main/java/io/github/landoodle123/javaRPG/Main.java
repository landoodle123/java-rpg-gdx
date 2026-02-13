package io.github.landoodle123.javaRPG;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.*;

import static com.badlogic.gdx.Gdx.graphics;
import static io.github.landoodle123.javaRPG.npc.npcRectangle;
import static io.github.landoodle123.javaRPG.player.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch spriteBatch;
    private Sprite npc;
    private Texture npcTexture;
    private FitViewport viewport;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        npcTexture = new Texture("guy.png");
        npc = new Sprite(npcTexture);
        npc.setSize(1, 1);
        try{charTexture = new Texture("player.png");} catch (Exception e) {
            System.out.println("Error occurred loading player.png, loaded guy.png instead.");
            charTexture = new Texture("guy.png");
        } //TODO: make player character
        playerCharacter = new Sprite(charTexture);
        playerCharacter.setSize(1, 1);
        viewport = new FitViewport(8,8);
        playerRectangle = new Rectangle();
        npcRectangle = new Rectangle();
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

    private void input() {
        float speed = 1f;
        float delta = graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerCharacter.setX(playerCharacter.getX() + speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerCharacter.setX(playerCharacter.getX() - speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerCharacter.setY(playerCharacter.getY() + speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerCharacter.setY(playerCharacter.getY() - speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            player.use();
        }
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float playerHeight = playerCharacter.getWidth();
        float playerWidth = playerCharacter.getWidth();
        float npcWidth = npc.getWidth();
        float npcHeight = npc.getWidth();
        playerRectangle.setX(MathUtils.clamp(playerCharacter.getX(), 0, worldWidth - playerWidth));
        playerRectangle.setY(MathUtils.clamp(playerCharacter.getY(), 0, worldHeight - playerHeight));
        npcRectangle.setX(MathUtils.clamp(npc.getX(), 0, worldWidth - npcWidth));
        npcRectangle.setY(MathUtils.clamp(npc.getY(), 0, worldHeight - npcHeight));
    }

    private void draw() {
        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();


        npc.draw(spriteBatch); // Sprites have their own draw method
        playerCharacter.draw(spriteBatch);

        spriteBatch.end();
    }

    @Override
    public void dispose() {
       spriteBatch.dispose();
       npcTexture.dispose();
       charTexture.dispose();
    }
}
