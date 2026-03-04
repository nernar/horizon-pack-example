package com.mojang.minecraftpe.platforms;

import android.annotation.TargetApi;
import android.os.Handler;
import android.view.View;

@TargetApi(19)
public class Platform44 extends Platform23 {
    private Runnable decorViewSettings;
    private View decoreView;
    private Handler eventHandler = new Handler();

    @Override // com.mojang.minecraftpe.platforms.Platform23, com.mojang.minecraftpe.platforms.Platform
    public void onVolumePressed() {
    }

    @Override // com.mojang.minecraftpe.platforms.Platform23, com.mojang.minecraftpe.platforms.Platform
    public void onAppStart(View view) {
        this.decoreView = view;
        this.decoreView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() { // from class: com.mojang.minecraftpe.platforms.Platform44.1
            @Override // android.view.View.OnSystemUiVisibilityChangeListener
            public void onSystemUiVisibilityChange(int visibility) {
                Platform44.this.eventHandler.postDelayed(Platform44.this.decorViewSettings, 500L);
            }
        });
        this.decorViewSettings = new Runnable() { // from class: com.mojang.minecraftpe.platforms.Platform44.2
            @Override // java.lang.Runnable
            public void run() {
                Platform44.this.decoreView.setSystemUiVisibility(5894);
            }
        };
        this.eventHandler.post(this.decorViewSettings);
    }

    @Override // com.mojang.minecraftpe.platforms.Platform23, com.mojang.minecraftpe.platforms.Platform
    public void onViewFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            this.eventHandler.postDelayed(this.decorViewSettings, 500L);
        }
    }
}
