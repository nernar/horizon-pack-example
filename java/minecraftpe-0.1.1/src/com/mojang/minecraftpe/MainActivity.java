package com.mojang.minecraftpe;

// TODO(0.1.1j): import android.app.Activity;
import android.app.AlertDialog;
import android.app.NativeActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import com.mojang.minecraftpe.sound.a;
import com.zhekasmirnov.horizon.launcher.env.AssetPatch;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends /* TODO(0.1.1j): Activity */ NativeActivity {
    private static boolean e = false;
    private g a;
    private float b = 1.0f;
    private Vector c = new Vector();
    private Vector d = new Vector();
    private int f = -1;
    private String[] g = null;
    private ArrayList h = new ArrayList();
    private a i;
    private AlertDialog j;
    private final DateFormat k = new SimpleDateFormat();

    static {
        System.loadLibrary("minecraftpe");
    }

    private void a(int i2, int[] iArr, boolean z, boolean z2) {
    	Logger.error("Minecraft", "Overriding a(int, int[], bool, bool)...");
        this.h.clear();
        runOnUiThread(new b(this, z, z2, i2, iArr));
    }

    static void a(MainActivity mainActivity, boolean z, boolean z2) {
    	Logger.error("Minecraft", "Overriding a(ctx, bool, bool)...");
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("");
        if (z2) {
            builder.setCancelable(false);
        }
        builder.setOnCancelListener(new f(mainActivity));
        builder.setPositiveButton("Ok", new e(mainActivity));
        if (z) {
            builder.setNegativeButton("Cancel", new d(mainActivity));
        }
        mainActivity.j = builder.create();
        mainActivity.j.setOwnerActivity(mainActivity);
    }

    public static boolean a() {
    	Logger.error("Minecraft", "Overriding a()...");
        return e;
    }

    static native boolean nativeHandleBack(boolean z);

    static native void nativeMouseDown(int i2, int i3, float f2, float f3);

    static native void nativeMouseMove(int i2, float f2, float f3);

    static native void nativeMouseUp(int i2, int i3, float f2, float f3);

    // TODO(0.1.1j): static native void nativeOnCreate();

    // TODO(0.1.1j): static native void nativeOnDestroy();

    static native void nativeOnKeyDown(int i2);

    static native void nativeOnKeyUp(int i2);

    public static void saveScreenshot(String str, int i2, int i3, int[] iArr) {
    	Logger.error("Minecraft", "Overriding saveScreenshot()...");
        Bitmap createBitmap = Bitmap.createBitmap(iArr, i2, i3, Bitmap.Config.ARGB_8888);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str);
            createBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);
            try {
                fileOutputStream.flush();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        } catch (FileNotFoundException e4) {
            System.err.println("Couldn't create file: " + str);
            e4.printStackTrace();
        }
    }

    public final void b() {
        this.c.clear();
        this.d.clear();
    }

    public void buyGame() {
    	Logger.error("Minecraft", "Overriding buyGame()...");
    }

    public final void c() {
        int i2 = 0;
        while (i2 < this.c.size()) {
            int i3 = i2 + 1;
            MotionEvent motionEvent = (MotionEvent) this.c.get(i2);
            int action = motionEvent.getAction();
            int i4 = action & 255;
            int i5 = (action & 65280) >> 8;
            int pointerId = motionEvent.getPointerId(i5);
            float x = motionEvent.getX(i5) * this.b;
            float y = motionEvent.getY(i5) * this.b;
            switch (i4) {
                case 0:
                    nativeMouseDown(pointerId, 1, x, y);
                    i2 = i3;
                    continue;
                case 1:
                    nativeMouseUp(pointerId, 1, x, y);
                    i2 = i3;
                    continue;
                case 2:
                    int pointerCount = motionEvent.getPointerCount();
                    for (int i6 = 0; i6 < pointerCount; i6++) {
                        nativeMouseMove(motionEvent.getPointerId(i6), motionEvent.getX(i6) * this.b, motionEvent.getY(i6) * this.b);
                    }
                    break;
                case 5:
                    nativeMouseDown(pointerId, 1, x, y);
                    i2 = i3;
                    continue;
                case 6:
                    nativeMouseUp(pointerId, 1, x, y);
                    i2 = i3;
                    continue;
            }
            i2 = i3;
        }
        int i7 = 0;
        while (i7 < this.d.size()) {
            int i8 = i7 + 1;
            KeyEvent keyEvent = (KeyEvent) this.d.get(i7);
            int keyCode = keyEvent.getKeyCode();
            if (keyCode == 4) {
                if (!nativeHandleBack(keyEvent.getAction() == 0) && keyEvent.getAction() == 0) {
                    finish();
                }
                i7 = i8;
            } else if (keyEvent.getAction() == 0) {
                nativeOnKeyDown(keyCode);
                i7 = i8;
            } else {
                if (keyEvent.getAction() == 1) {
                    nativeOnKeyUp(keyCode);
                }
                i7 = i8;
            }
        }
        b();
    }

    public int checkLicense() {
    	Logger.error("Minecraft", "Overriding checkLicense()...");
        return 0;
    }

    public void displayDialog(int i2) {
    	Logger.error("Minecraft", "Overriding displayDialog()...");
        if (i2 == 1) {
            a(R.layout.create_world_screen, new int[]{R.id.editText_worldName, R.id.editText_worldSeed}, true, false);
        } else if (i2 == 3) {
            Intent intent = new Intent(this, MainMenuOptionsActivity.class);
            intent.putExtra("preferenceId", R.xml.preferences);
            startActivityForResult(intent, i2);
        } else if (i2 == 4) {
            a(R.layout.rename_mp_world, new int[]{R.id.editText_worldNameRename}, false, true);
        }
    }

    public String getDateString(int i2) {
    	Logger.error("Minecraft", "Overriding getDateString()...");
        return this.k.format(new Date(((long) i2) * 1000));
    }

    public int[] getImageData(String str) {
    	Logger.error("Minecraft", "Overriding getImageData(" + str + ")...");
    	InputStream asset = AssetPatch.getAssetInputStream(getAssets(), str);
    	if (asset == null) {
    		Logger.error("Minecraft::ResourceStorage", "Missing texture " + str + ", ignoring...");
    		return new int[0];
    	}
        Bitmap decodeStream = BitmapFactory.decodeStream(asset);
        int width = decodeStream.getWidth();
        int height = decodeStream.getHeight();
        int[] iArr = new int[((width * height) + 2)];
        iArr[0] = width;
        iArr[1] = height;
        decodeStream.getPixels(iArr, 2, width, 0, 0, width, height);
        return iArr;
    }

    public String[] getOptionStrings() {
        // Map<String, ?> all = PreferenceManager.getDefaultSharedPreferences(this).getAll();
        // String[] strArr = new String[(all.size() * 2)];
        // int i2 = 0;
        // for (Map.Entry next : all.entrySet()) {
            // int i3 = i2 + 1;
            // strArr[i2] = (String) next.getKey();
            // i2 = i3 + 1;
            // strArr[i3] = next.getValue().toString();
        // }
        // return strArr;
        Logger.error("Minecraft", "Overriding getOptionStrings()...");
        return new String[] {
        	"mp_username", "Steve",
        	"mp_server_visible_default", "false",
        	"gfx_fancygraphics", "false",
        	"gfx_lowquality", "false",
        	"ctrl_invertmouse", "false",
        	"ctrl_usetouchscreen", "true",
        	"feedback_vibration", "false"
        };
    }

    public int getScreenHeight() {
    	Logger.error("Minecraft", "Overriding getScreenHeight()...");
        Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        int min = Math.min(defaultDisplay.getWidth(), defaultDisplay.getHeight());
        System.out.println("getheight: " + min);
        return min;
    }

    public int getScreenWidth() {
    	Logger.error("Minecraft", "Overriding getScreenWidth()...");
        Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        int max = Math.max(defaultDisplay.getWidth(), defaultDisplay.getHeight());
        System.out.println("getwidth: " + max);
        return max;
    }

    public int getUserInputStatus() {
        return this.f;
    }

    public String[] getUserInputString() {
        return this.g;
    }

    public boolean hasBuyButtonWhenInvalidLicense() {
    	Logger.error("Minecraft", "Overriding hasBuyButtonWhenInvalidLicense()...");
        return true;
    }

    public void initiateUserInput(int i2) {
    	Logger.error("Minecraft", "Overriding initiateUserInput()...");
        this.g = null;
        this.f = -1;
    }

    public boolean isTouchscreen() {
    	Logger.error("Minecraft", "Overriding isTouchscreen()...");
        return true;
    }

    native void nativeRegisterThis();

    native void nativeUnregisterThis();

    protected void onActivityResult(int i2, int i3, Intent intent) {
    	Logger.error("Minecraft", "Overriding onActivityResult()...");
        if (i2 == 3) {
            this.f = 1;
        }
    }

    public void onCreate(Bundle bundle) {
    	Logger.error("Minecraft", "Overriding onCreate()...");
        setVolumeControlStream(3);
        super.onCreate(bundle);
        // TODO(0.1.1j/custom): setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        nativeRegisterThis();
        /*
        TODO(0.1.1j):
	        nativeOnCreate();
	        this.a = new g(getApplication(), this);
	        this.a.setEGLConfigChooser(true);
	        this.a.a();
	        setContentView(this.a);
        */
        this.i = new a(this);
    }

    protected void onDestroy() {
    	Logger.error("Minecraft", "Overriding onDestroy()...");
        nativeUnregisterThis();
        super.onDestroy();
        // TODO(0.1.1j): nativeOnDestroy();
    }

    public boolean onKeyDown(int i2, KeyEvent keyEvent) {
    	Logger.error("Minecraft", "Overriding onKeyDown()...");
        if (keyEvent.getRepeatCount() <= 0) {
            this.d.add(new KeyEvent(keyEvent));
        }
        return (i2 == 25 || i2 == 24) ? false : true;
    }

    public boolean onKeyUp(int i2, KeyEvent keyEvent) {
    	Logger.error("Minecraft", "Overriding onKeyUp()...");
        if (keyEvent.getRepeatCount() <= 0) {
            this.d.add(new KeyEvent(keyEvent));
        }
        return (i2 == 25 || i2 == 24) ? false : true;
    }

    protected void onPause() {
    	Logger.error("Minecraft", "Overriding onPause()...");
        super.onPause();
        // TODO(0.1.1j): this.a.onPause();
    }

    protected void onResume() {
    	Logger.error("Minecraft", "Overriding onResume()...");
        super.onResume();
        // TODO(0.1.1j): this.a.onResume();
    }

    protected void onStart() {
    	Logger.error("Minecraft", "Overriding onStart()...");
        super.onStart();
    }

    protected void onStop() {
    	Logger.error("Minecraft", "Overriding onStop()...");
        super.onStop();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.c.add(MotionEvent.obtainNoHistory(motionEvent));
        return super.onTouchEvent(motionEvent);
    }

    public void onWindowFocusChanged(boolean z) {
    	Logger.error("Minecraft", "Overriding onWindowFocusChanged()...");
        super.onWindowFocusChanged(z);
    }

    public void playSound(String str, float f2, float f3) {
    	Logger.error("Minecraft", "Overriding playSound()...");
    	this.i.a(str, f2, f3);
    }

    public void postScreenshotToFacebook(String str, int i2, int i3, int[] iArr) {
    	Logger.error("Minecraft", "Overriding postScreenshotToFacebook()...");
    }

    public void quit() {
    	Logger.error("Minecraft", "Overriding quit()...");
        runOnUiThread(new c(this));
    }

    public void setIsPowerVR(boolean z) {
    	Logger.error("Minecraft", "Overriding setIsPowerVR()...");
        e = z;
    }

    public void tick() {
    	Logger.error("Minecraft", "Overriding tick()...");
    }

    public void vibrate(int i2) {
    	Logger.error("Minecraft", "Overriding vibrate()...");
        ((Vibrator) getSystemService("vibrator")).vibrate((long) i2);
    }
}
