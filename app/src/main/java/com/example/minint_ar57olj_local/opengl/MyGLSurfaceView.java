package com.example.minint_ar57olj_local.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer renderer;
    public MyGLSurfaceView(Context context) {
        super(context);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.images);
        String vertexShader = loadShader(R.raw.vertex_shader);
        String fragmentShader = loadShader(R.raw.fragment_shader);

        setEGLContextClientVersion(3);
        renderer = new MyGLRenderer(bmp,vertexShader,fragmentShader);
        setRenderer(renderer);
    }

    private String loadShader(int fileID) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = getResources().openRawResource(fileID);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String eachLine;
        try {
            eachLine = bufferedReader.readLine();
        while (eachLine!=null) {
            stringBuilder.append(eachLine);
            eachLine = bufferedReader.readLine();
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
