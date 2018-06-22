package com.example.minint_ar57olj_local.opengl;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
    private final int program;
    private FloatBuffer vertexBuffer;
    static final int COORDS_PER_VERTEX = 3;
    static float triaangleCoords[] = { // in counter clockwise order
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // left
            0.5f, -0.311004243f, 0.0f // right
    };
    float color[] = {0.6f, 0.77f, 0.22f, 1f};

    public Triangle() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                // number of coordinate values * 4 bytes per float
                triaangleCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the byte buffer
        vertexBuffer = byteBuffer.asFloatBuffer();
        // add the coordinates to the float buffer
        vertexBuffer.put(triaangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
        // create empty OpenGL ES Program
        program = GLES30.glCreateProgram();

        // add the vertex shader to the program
        GLES30.glAttachShader(program, vertexShader);

        // add the fragment shader to the program
        GLES30.glAttachShader(program, fragmentShader);

        // create OpenGL ES program executables
        GLES30.glLinkProgram(program);
    }

    private final String vertexShaderCode =
            // this matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as the modifier of gl_Position
                    // Note that the uMVPMatrix factor must be first in order
                    // for the matrix multiplication product to be correct
                    " gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    " gl_FragColor = vColor;" +
                    "}";

    private int mMVPMatrixHandle;
    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = triaangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public void draw(float[] mMVPMatrix) {
        // add program to OpenGL ES environment
        GLES30.glUseProgram(program);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES30.glGetAttribLocation(program, "vPosition");

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX
                , GLES30.GL_FLOAT, false
                , vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        colorHandle = GLES30.glGetUniformLocation(program, "vColor");

        // set color for drawing the triangle
        GLES30.glUniform4fv(colorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(program, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // draw the triangle
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount);

        // disable vertex array
        GLES30.glDisableVertexAttribArray(positionHandle);
    }
}
