package io.github.landoodle123.javaRPG;
import java.util.concurrent.ThreadLocalRandom;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class npc {
    private static final String[] possibleNames = {"Jeremy", "Jim bob", "Larry", "THE FOG IS COMING", "Jeramiah", "Gordon Freeman", "EVIL John", "GOOD John", "Consumer of Grass"};
    private Integer health = 10;
    private static final Integer nameNumber = ThreadLocalRandom.current().nextInt(0, 9);
    private static String name = possibleNames[nameNumber];
    static Rectangle npcRectangle;

    public static final String[] dialogueOptions = {"Salutations, my name is " + name + ", nice to meet you", "Greetings traveller. My name is " + name + ", and I totally do not eat kidneys!", "THE FOG IS COMING THE FOG IS COMING THE FOG IS COMING! anyway my name is " + name + " so make sure you dodge the incoming fog"};

    public static void talk() {
        System.out.println("npc talk initiated - if no dialog box shows up there is an error");
        int dialogueSelection = ThreadLocalRandom.current().nextInt(0, 3);
        Stage stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Dialog dialog = new Dialog("Conversation", skin) {
        };
        dialog.text(dialogueOptions[dialogueSelection]);
        dialog.button("Goodbye");
        dialog.show(stage);
    }
}
