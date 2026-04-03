package com.mojang.minecraftpe;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;

final class g extends GLSurfaceView {
    private GLRenderer a;
    private MainActivity b;

    public g(Context context, MainActivity mainActivity) {
        super(context);
        this.b = mainActivity;
        this.a = new GLRenderer(mainActivity);
    }

    public final void a() {
        setRenderer(this.a);
    }

    public final void onPause() {
        this.a.a = true;
    }

    public final void onResume() {
        this.a.a = false;
    }

    public final void surfaceCreated(SurfaceHolder surfaceHolder) {
        System.out.println("w,h: " + this.b.getScreenWidth() + "," + this.b.getScreenHeight());
        super.surfaceCreated(surfaceHolder);
    }
}
