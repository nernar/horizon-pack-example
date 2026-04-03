package com.mojang.minecraftpe.platforms;

import android.os.Build;
import android.view.View;

public abstract class Platform {
    public abstract void onAppStart(View view);

    public abstract void onViewFocusChanged(boolean z);

    public abstract void onVolumePressed();

    public static Platform createPlatform() {
        return Build.VERSION.SDK_INT >= 19 ? new Platform44() : new Platform23();
    }
}
