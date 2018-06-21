package com.example.minint_ar57olj_local.opengl;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Triangle triangle;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        GLES30.glClearColor(0f, 0f, 0f, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

        triangle = new Triangle();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        triangle.draw();
    }

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type GLES30.GL_VERTEX_SHADER
        // or a fragment shader type GLES30.GL_FRAGMENT_SHADER
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader,shaderCode);
        GLES30.glCompileShader(shader);

        return shader;
    }
}
