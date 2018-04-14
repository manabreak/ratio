package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class LevelShader extends ShaderProgram {

    private static final String SHADER_VERTEX = "attribute vec4 a_position;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "attribute vec3 a_normal;\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec2 v_texCoords;\n" +
            "varying vec3 v_normal;\n" +
            "\n" +
            "void main() {\n" +
            "    v_texCoords = a_texCoord0;\n" +
            "    v_normal = a_normal;\n" +
            "    gl_Position = u_projTrans * a_position;\n" +
            "}";
    private static final String SHADER_FRAGMENT = "varying vec2 v_texCoords;\n" +
            "varying vec3 v_normal;\n" +
            "\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform vec3 u_lightDirection;\n" +
            "uniform float u_ambientIntensity;\n" +
            "uniform float u_level;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_FragColor = texture2D(u_texture, v_texCoords);\n" +
            "    \n" +
            "    vec3 light = u_lightDirection;\n" +
            "    light = normalize(light);\n" +
            "    float d = max(u_ambientIntensity, dot(v_normal, -light));\n" +
            "    gl_FragColor *= d * u_level;\n" +
            "}";

    public LevelShader() {
        super(SHADER_VERTEX, SHADER_FRAGMENT);
    }
}
