package com.example.minint_ar57olj_local.opengl;

import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Triangle triangle;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private Bitmap bitmap;

    // region Buffers
    private static final float[] POSITION_MATRIX = {
            -1, -1, 1,  // X1,Y1,Z1
            1, -1, 1,  // X2,Y2,Z2
            -1, 1, 1,  // X3,Y3,Z3
            1, 1, 1,  // X4,Y4,Z4
    };

    private static final float TEXTURE_COORDS[] = {
            0, 1, // X1,Y1
            1, 1, // X2,Y2
            0, 0, // X3,Y3
            1, 0, // X4,Y4
    };

    private float[] rotationMatrix = new float[16];

    private String VERTEX_SHADER;
    private String FRAGMENT_SHADER;
    public MyGLRenderer(Bitmap bmp, String vertexShader, String fragmentShader) {
        bitmap = bmp;
        VERTEX_SHADER = vertexShader;
        FRAGMENT_SHADER = fragmentShader;
    }

    private float scale = 1;
    private int vPosition;
    private int vTexturePosition;
    private int uMVPMatrix;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        GLES30.glClearColor(0f, 0f, 0f, 1f);
        Matrix.setRotateM(rotationMatrix, 0, 0, 0, 0, 1f);

        int texture = createFBOTexture(bitmap.getWidth(), bitmap.getHeight());
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
        GLUtils.texSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, bitmap);

        // Then, we load the shaders into a program
        int iVShader, iFShader, iProgId;
        int[] link = new int[1];
        iVShader = loadShader(GLES30.GL_VERTEX_SHADER, VERTEX_SHADER);
        iFShader = loadShader(GLES30.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        iProgId = GLES30.glCreateProgram();
        GLES30.glAttachShader(iProgId, iVShader);
        GLES30.glAttachShader(iProgId, iFShader);
        GLES30.glLinkProgram(iProgId);

        GLES30.glGetProgramiv(iProgId, GLES30.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            throw new RuntimeException("Program couldn't be loaded");
        }
        GLES30.glDeleteShader(iVShader);
        GLES30.glDeleteShader(iFShader);
        GLES30.glUseProgram(iProgId);

        // Now that our program is loaded and in use, we'll retrieve the handles of the parameters
        // we pass to our shaders
        vPosition = GLES30.glGetAttribLocation(iProgId, "vPosition");
        vTexturePosition = GLES30.glGetAttribLocation(iProgId, "vTextureCoordinate");
        uMVPMatrix = GLES30.glGetUniformLocation(iProgId, "uMVPMatrix");
    }

    private int createFBOTexture(int width, int height) {
        int[] temp = new int[1];
        GLES30.glGenFramebuffers(1, temp, 0);
        int handleID = temp[0];
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, handleID);

        int fboTex = createTexture(width, height);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, fboTex, 0);

        if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("GL Framebuffer status incomplete");
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        return handleID;
    }

    private int createTexture(int width, int height) {
        int[] textureHandle = new int[1];
        GLES30.glGenTextures(1, textureHandle, 0);
        int textureID = textureHandle[0];
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        return textureID;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to coords
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //triangle = new Triangle();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        // Set the camera position ( view matrix )
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3,
                0f, 0f, 0f, 0f, 1f, 0f);

        // calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // combine with applied rotation
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, rotationMatrix, 0);

        // apply the scale to matrix
        Matrix.scaleM(mMVPMatrix, 0, scale, scale, scale);

        GLES30.glUniformMatrix4fv(uMVPMatrix, 1, false, mMVPMatrix, 0);

        FloatBuffer positionBuffer = ByteBuffer.allocateDirect(POSITION_MATRIX.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(POSITION_MATRIX);
        positionBuffer.position(0);

        GLES30.glVertexAttribPointer(vPosition, 3, GLES30.GL_FLOAT, false, 0, positionBuffer);
        GLES30.glEnableVertexAttribArray(vPosition);

        // We pass the buffer for the texture position
        FloatBuffer textureCoordsBuffer = ByteBuffer.allocateDirect(TEXTURE_COORDS.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(TEXTURE_COORDS);
        textureCoordsBuffer.position(0);
        GLES30.glVertexAttribPointer(vTexturePosition, 2, GLES30.GL_FLOAT, false, 0, textureCoordsBuffer);
        GLES30.glEnableVertexAttribArray(vTexturePosition);

        // We draw our square which will represent our logo
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glDisableVertexAttribArray(vPosition);
        GLES30.glDisableVertexAttribArray(vTexturePosition);

        // draw shape
        //triangle.draw(mMVPMatrix);
    }

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type GLES30.GL_VERTEX_SHADER
        // or a fragment shader type GLES30.GL_FRAGMENT_SHADER
        int shader = GLES30.glCreateShader(type);
        int[] compiled = new int[1];

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            throw new RuntimeException("Compilation failed: " + GLES30.glGetShaderInfoLog(shader));
        }

        return shader;
    }
}
