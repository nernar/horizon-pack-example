package com.mojang.minecraftpe;

import android.content.DialogInterface;
import android.widget.EditText;

final class e implements DialogInterface.OnClickListener {
    private MainActivity a;

    e(MainActivity mainActivity) {
        this.a = mainActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        int size = this.a.h.size();
        this.a.g = new String[size];
        for (int i2 = 0; i2 < size; i2++) {
            this.a.g[i2] = ((EditText) this.a.h.get(i2)).getText().toString();
        }
        this.a.f = 1;
    }
}
