package com.mojang.minecraftpe.sound;

import android.content.Context;
import android.media.SoundPool;
import com.mojang.minecraftpe.R;
import com.zhekasmirnov.horizon.launcher.env.AssetPatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class a {
    private Context a;
    private Random b;
    private int c;
    private SoundPool d;
    private Map e = new HashMap();

    public a(Context context) {
        this.a = context;
        this.c = 3;
        this.b = new Random();
        this.d = new SoundPool(4, this.c, 0);
        a();
    }

	private synchronized b a(String str, String str2) {
        ArrayList arrayList;
        b bVar;
        if (str != null) {
            List list = (List) this.e.get(str);
            if (list == null) {
                ArrayList arrayList2 = new ArrayList();
                this.e.put(str, arrayList2);
                arrayList = arrayList2;
            } else {
                arrayList = (ArrayList) list;
            }
            Iterator it = arrayList.iterator();
            while (true) {
                if (it.hasNext()) {
                    bVar = (b) it.next();
                    if (bVar.b.equals(str)) {
                        break;
                    }
                } else {
                    b bVar2 = new b(this, this.d.load(AssetPatch.getRedirectedPath("sounds/random/" + str2 + ".ogg"), 1), str);
                    arrayList.add(bVar2);
                    bVar = bVar2;
                    break;
                }
            }
        } else {
            bVar = null;
        }
        return bVar;
    }

    private void a() {
        for (b bVar : new b[]{new b(this, "click", "random.click"), new b(this, "explode", "random.explode"), new b(this, "splash", "random.splash"), new b(this, "cloth1", "step.cloth"), new b(this, "cloth2", "step.cloth"), new b(this, "cloth3", "step.cloth"), new b(this, "cloth4", "step.cloth"), new b(this, "grass1", "step.grass"), new b(this, "grass2", "step.grass"), new b(this, "grass3", "step.grass"), new b(this, "grass4", "step.grass"), new b(this, "gravel2", "step.gravel"), new b(this, "gravel3", "step.gravel"), new b(this, "gravel4", "step.gravel"), new b(this, "sand1", "step.sand"), new b(this, "sand2", "step.sand"), new b(this, "sand3", "step.sand"), new b(this, "sand4", "step.sand"), new b(this, "stone1", "step.stone"), new b(this, "stone2", "step.stone"), new b(this, "stone3", "step.stone"), new b(this, "stone4", "step.stone"), new b(this, "wood1", "step.wood"), new b(this, "wood2", "step.wood"), new b(this, "wood3", "step.wood"), new b(this, "wood4", "step.wood")}) {
            a(bVar.b, bVar.a);
        }
    }

    public final void a(String str, float f, float f2) {
        List list = (List) this.e.get(str);
        b bVar = list == null ? null : (b) list.get(this.b.nextInt(list.size()));
        if (bVar != null) {
            float f3 = f * 2.5f;
            this.d.play(bVar.a, f3, f3, 0, 0, f2);
        }
    }
}
