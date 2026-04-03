package com.mojang.minecraftpe;

import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import java.util.ArrayList;

final class j {
    private PreferenceActivity a;
    private ArrayList b = new ArrayList();

    public j(PreferenceActivity preferenceActivity) {
        this.a = preferenceActivity;
    }

    private Preference a(String str) {
        return this.a.findPreference(str);
    }

    public final void a() {
        System.err.println("ERR: " + this.b.size());
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < this.b.size()) {
                ((PreferenceGroup) this.b.get(i2)).removePreference((Preference) this.b.get(i2 + 1));
                i = i2 + 2;
            } else {
                return;
            }
        }
    }

    public final void a(SharedPreferences sharedPreferences, String str) {
        Preference a2 = a(str);
        if (a2 instanceof CheckBoxPreference) {
            if (str.equals("gfx_lowquality")) {
                boolean z = sharedPreferences.getBoolean("gfx_lowquality", false);
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) a("gfx_fancygraphics");
                if (checkBoxPreference != null) {
                    checkBoxPreference.setEnabled(!z);
                    if (z) {
                        checkBoxPreference.setChecked(false);
                    }
                }
            }
            if (str.equals("gfx_fancygraphics")) {
                System.err.println("zzzz");
                CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) a2;
                System.err.println("Is PowerVR? : " + MainActivity.a());
                if (MainActivity.a()) {
                    checkBoxPreference2.setSummary("Experimental on this device!");
                }
            }
            if (a2.getKey().equals("ctrl_usetouchscreen")) {
                this.b.add((PreferenceCategory) a("category_graphics"));
                this.b.add(a2);
                a2.setEnabled(false);
                a2.setDefaultValue(true);
            }
        }
    }

    public final void a(Preference preference) {
        a(PreferenceManager.getDefaultSharedPreferences(this.a), preference.getKey());
    }
}
