package com.mojang.minecraftpe;

import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class GLRenderer implements GLSurfaceView.Renderer {
    public boolean a = false;
    private MainActivity b;

    public GLRenderer(MainActivity mainActivity) {
        this.b = mainActivity;
    }

    private static native void nativeOnSurfaceChanged(int i, int i2);

    private static native void nativeOnSurfaceCreated();

    private static native void nativeUpdate();

    public void onDrawFrame(GL10 gl10) {
        if (!this.a) {
            this.b.c();
            nativeUpdate();
            return;
        }
        this.b.b();
    }

    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        if (i2 > i) {
            System.out.println("surfchanged ERROR. dimensions: " + i + ", " + i2);
            nativeOnSurfaceChanged(i2, i);
            return;
        }
        nativeOnSurfaceChanged(i, i2);
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        nativeOnSurfaceCreated();
    }
}
