package me.manabreak.ratio.plugins.objects;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ObjectShader extends ShaderProgram {

    private static final String VERTEX = "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec3 v_normal;\n" +
            "varying vec4 v_color;\n" +
            "\n" +
            "void main() {\n" +
            "    v_normal = a_normal;\n" +
            "    v_color = a_color;\n" +
            "    gl_Position = u_projTrans * a_position;\n" +
            "}";
    private static final String FRAGMENT = "varying vec3 v_normal;\n" +
            "varying vec4 v_color;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_FragColor = v_color;\n" +
            "    \n" +
            "    vec3 light = vec3(0.2, -0.8, -0.6);\n" +
            "    light = normalize(light);\n" +
            "    float d = max(0.2, dot(v_normal, -light));\n" +
            "    gl_FragColor *= d;\n" +
            "}";

    public ObjectShader() {
        super(VERTEX, FRAGMENT);
    }
}
