package me.manabreak.ratio.plugins.objects;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import me.manabreak.ratio.test.GdxTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ObjectEditorTest extends GdxTest {

    private ObjectEditorUi view;
    private ObjectEditorPresenter p;

    @Before
    public void setUp() {
        p = new ObjectEditorPresenter();
        view = new ObjectEditorUi(p);
        p.attachView(view);
    }

    @Test
    public void testClickToSelect() {
        p.createObject();
        p.createObject();

        for (GameObject object : p.getObjects()) {
            assertFalse(object.isSelected());
        }

        p.objectClicked(p.getObjects().get(0));
        assertTrue(p.getObjects().get(0).isSelected());
        assertFalse(p.getObjects().get(1).isSelected());
    }

    @Test
    public void testObjectCreationDeletion() {
        assertEquals(0, p.getObjects().size());
        p.createObject();
        assertEquals(1, p.getObjects().size());

        p.removeObject(p.getObjects().get(0));
        assertEquals(0, p.getObjects().size());

        p.createObject();
        p.setSelection(p.getObjects().get(0));

        p.onKeyEvent(Input.Keys.FORWARD_DEL);
        assertEquals(0, p.getObjects().size());
    }

    @Test
    public void testBasicPropertyEditing() {
        p.createObject();

        final GameObject obj = p.getObjects().get(0);
        p.setSelection(obj);

        p.typeChanged(obj, "Spawn");
        assertEquals("Spawn", obj.getType());

        p.xChanged(obj, "5.0");
        assertEquals(5.0f, obj.getX(), 0.001f);

        p.yChanged(obj, "6.0");
        assertEquals(6.0f, obj.getY(), 0.001f);

        p.zChanged(obj, "7.0");
        assertEquals(7.0f, obj.getZ(), 0.001f);

        p.widthChanged(obj, "8.0");
        assertEquals(8.0f, obj.getSizeX(), 0.001f);

        p.heightChanged(obj, "9.0");
        assertEquals(9.0f, obj.getSizeY(), 0.001f);

        p.depthChanged(obj, "10.0");
        assertEquals(10.0f, obj.getSizeZ(), 0.001f);

        p.colorChanged(obj, Color.CHARTREUSE);
        assertEquals(Color.CHARTREUSE, obj.getColor());
    }

    @Test
    public void testCreateCustomProperties() {
        p.createObject();
        final GameObject obj = p.getObjects().get(0);

        assertFalse(obj.getProperties().hasProperty("somebool"));
        assertFalse(obj.getProperty("somebool", false));

        // No object selected; shouldn't affect the obj
        p.createBooleanProperty("somebool");
        assertFalse(obj.getProperties().hasProperty("somebool"));
        assertFalse(obj.getProperty("somebool", false));

        p.objectClicked(obj);

        // Boolean
        p.createBooleanProperty("somebool");
        assertTrue(obj.getProperties().hasProperty("somebool"));
        assertTrue(obj.getProperty("somebool", false));
        p.changePropertyValue("somebool", false);
        assertFalse(obj.getProperty("somebool", true));

        // Integer
        p.createIntProperty("someint");
        assertTrue(obj.getProperties().hasProperty("someint"));
        assertEquals(0, (int) obj.getProperty("someint", 42));
        p.changePropertyValue("someint", 11);
        assertEquals(11, (int) obj.getProperty("someint", 42));

        // Double
        p.createDoubleProperty("somedouble");
        assertTrue(obj.getProperties().hasProperty("somedouble"));
        assertEquals(0.0, obj.getProperty("somedouble", 3.14), 0.0001);
        p.changePropertyValue("somedouble", 0.707);
        assertEquals(0.707, obj.getProperty("somedouble", 3.14), 0.0001);

        // String
        p.createStringProperty("somestr");
        assertTrue(obj.getProperties().hasProperty("somestr"));
        assertEquals("", obj.getProperty("somestr", "foo"));
        p.changePropertyValue("somestr", "bar");
        assertEquals("bar", obj.getProperty("somestr", "foo"));
    }
}