package com.mojang.minecraftpe.platforms;

import android.annotation.TargetApi;
import android.os.Handler;
import android.view.View;

@TargetApi(19)
public class Platform19 extends Platform9 {
    private Runnable decorViewSettings;
    private View decoreView;
    private Handler eventHandler;

    public Platform19(boolean initEventHandler) {
        if (initEventHandler) {
            this.eventHandler = new Handler();
        }
    }

    @Override // com.mojang.minecraftpe.platforms.Platform9, com.mojang.minecraftpe.platforms.Platform
    public void onVolumePressed() {
    }

    @Override // com.mojang.minecraftpe.platforms.Platform9, com.mojang.minecraftpe.platforms.Platform
    public void onAppStart(View view) {
        if (this.eventHandler != null) {
            this.decoreView = view;
            this.decoreView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() { // from class: com.mojang.minecraftpe.platforms.Platform19.1
                @Override // android.view.View.OnSystemUiVisibilityChangeListener
                public void onSystemUiVisibilityChange(int visibility) {
                    Platform19.this.eventHandler.postDelayed(Platform19.this.decorViewSettings, 500L);
                }
            });
            this.decorViewSettings = new Runnable() { // from class: com.mojang.minecraftpe.platforms.Platform19.2
                @Override // java.lang.Runnable
                public void run() {
                    Platform19.this.decoreView.setSystemUiVisibility(5894);
                }
            };
            this.eventHandler.post(this.decorViewSettings);
        }
    }

    @Override // com.mojang.minecraftpe.platforms.Platform9, com.mojang.minecraftpe.platforms.Platform
    public void onViewFocusChanged(boolean hasFocus) {
        if (this.eventHandler != null && hasFocus) {
            this.eventHandler.postDelayed(this.decorViewSettings, 500L);
        }
    }
}
