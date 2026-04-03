package com.mojang.minecraftpe;

import android.content.DialogInterface;

final class f implements DialogInterface.OnCancelListener {
    private MainActivity a;

    f(MainActivity mainActivity) {
        this.a = mainActivity;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.a.f = 0;
    }
}
