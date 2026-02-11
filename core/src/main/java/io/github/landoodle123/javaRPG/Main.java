package io.github.landoodle123.javaRPG;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;

import static com.badlogic.gdx.Gdx.graphics;

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
        viewport = new FitViewport(8,8);
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
            npc.setX(npc.getX() + speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            npc.setX(npc.getX() - speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            npc.setY(npc.getY() + speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            npc.setY(npc.getY() - speed * delta);
        }
    }

    private void logic() {

    }

    private void draw() {
        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();


        npc.draw(spriteBatch); // Sprites have their own draw method

        spriteBatch.end();
    }

    @Override
    public void dispose() {
       spriteBatch.dispose();
       npcTexture.dispose();
    }
}
