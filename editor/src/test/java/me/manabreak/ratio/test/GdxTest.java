package me.manabreak.ratio.test;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static org.mockito.Mockito.mock;

public abstract class GdxTest {
    // This is our "test" application
    private static Application application;

    // Before running any tests, initialize the application with the headless backend
    @BeforeClass
    public static void init() {
        // Note that we don't need to implement any of the listener's methods
        application = new HeadlessApplication(new ApplicationListener() {
            @Override
            public void create() {
            }

            @Override
            public void resize(int width, int height) {
            }

            @Override
            public void render() {
            }

            @Override
            public void pause() {
            }

            @Override
            public void resume() {
            }

            @Override
            public void dispose() {
            }
        });

        // Use Mockito to mock the OpenGL methods since we are running headlessly
        Gdx.gl20 = mock(GL20.class);
        Gdx.gl = Gdx.gl20;
        VisUI.load();
        final Skin skin = VisUI.getSkin();

        // Fake the icons style for tests
        final VisTextButton.VisTextButtonStyle style = skin.get("default", VisTextButton.VisTextButtonStyle.class);
        skin.add("icons-small", style);
    }

    // After we are done, clean up the application
    @AfterClass
    public static void cleanUp() {
        // Exit the application first
        application.exit();
        VisUI.dispose(true);
        application = null;
    }
}