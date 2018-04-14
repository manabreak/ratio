package me.manabreak.ratio;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.io.File;

public class EditorLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = 1600;
        config.height = 900;
        // config.x = 2000;

        File f = new File("ui");
        if (f.exists() && f.isDirectory() && f.listFiles().length > 0) {
            TexturePacker.Settings s = new TexturePacker.Settings();
            s.filterMag = Texture.TextureFilter.Linear;
            s.filterMin = Texture.TextureFilter.Linear;
            s.maxWidth = 1024;
            s.maxHeight = 1024;
            s.paddingX = 3;
            s.paddingY = 3;
            s.edgePadding = true;
            s.alias = false;
            s.bleed = true;
            s.duplicatePadding = true;
            s.useIndexes = false;

            // TexturePacker.process(s, "images", "android/assets/graphics", "game");
            TexturePacker.process(s, "ui", "editor/assets/graphics", "editor");
        } else {
            System.out.println("No images to pack");
        }

        new LwjglApplication(new EditorGame(), config);
    }
}
