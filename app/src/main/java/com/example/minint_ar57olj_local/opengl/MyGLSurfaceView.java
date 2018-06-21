package com.example.minint_ar57olj_local.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer renderer;
    public MyGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(3);
        renderer = new MyGLRenderer();
        setRenderer(renderer);
    }
}
