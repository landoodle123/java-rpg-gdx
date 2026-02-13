package io.github.landoodle123.javaRPG;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.*;
import io.github.landoodle123.javaRPG.npc.*;

public class player {
    public static Sprite playerCharacter;
    public static Texture charTexture;
    private Integer swordClass = 0;
    private String name;
    private Integer health = 100;
    static Rectangle playerRectangle;

    public static void use() {
        //TODO: Add logic for use, either picking up an item or interacting with an npc or door
        if (playerRectangle.overlaps(npc.npcRectangle)) {
            npc.talk();
        }
    }
    public void attack() {
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            //TODO: Add logic for attack, run swinging a sword.
        }
    }
}
