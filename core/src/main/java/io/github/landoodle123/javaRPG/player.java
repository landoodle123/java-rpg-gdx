package io.github.landoodle123.javaRPG;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.*;

public class player {
    public static Sprite playerCharacter;
    public static Texture charTexture;
    static Rectangle playerRectangle;

    public static void use() {
        //TODO: Add logic for use, either picking up an item or interacting with an npc or door
        if (playerRectangle.overlaps(npc.npcRectangle)) {
            npc.talk();
        } else {
            System.out.println("Neither overlapping npc or door");
        }
    }
    public void attack() {
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            //TODO: Add logic for attack, run swinging a sword.
        }
    }
}
