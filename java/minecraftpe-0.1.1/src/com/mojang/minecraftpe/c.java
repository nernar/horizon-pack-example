package com.mojang.minecraftpe;

final class c implements Runnable {
    private MainActivity a;

    c(MainActivity mainActivity) {
        this.a = mainActivity;
    }

    public final void run() {
        this.a.finish();
    }
}
