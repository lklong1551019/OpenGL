package com.example.minint_ar57olj_local.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer renderer;
    public MyGLSurfaceView(Context context) {
        super(context);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.images);

        setEGLContextClientVersion(3);
        renderer = new MyGLRenderer(bmp);
        setRenderer(renderer);
    }

}
