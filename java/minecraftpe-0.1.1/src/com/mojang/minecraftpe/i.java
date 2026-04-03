package com.mojang.minecraftpe;

import android.preference.Preference;

final class i extends a {
    private MainMenuOptionsActivity a;

    i(MainMenuOptionsActivity mainMenuOptionsActivity) {
        this.a = mainMenuOptionsActivity;
    }

    final void a(Preference preference) {
        MainMenuOptionsActivity.b(preference);
        this.a.a.a(preference);
    }
}
