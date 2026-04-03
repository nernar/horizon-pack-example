package com.mojang.minecraftpe;

import android.content.DialogInterface;

final class d implements DialogInterface.OnClickListener {
    private MainActivity a;

    d(MainActivity mainActivity) {
        this.a = mainActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.a.f = 0;
        dialogInterface.cancel();
    }
}
