package com.mojang.minecraftpe;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.text.InputFilter;
import android.text.method.SingleLineTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mojang.android.EditTextAscii;
import com.zhekasmirnov.horizon.launcher.env.AssetPatch;
import com.zhekasmirnov.horizon.runtime.logger.Logger;

final class b implements Runnable {
    private MainActivity a;
    private final boolean b;
    private final boolean c;
    private final int d;
    private final int[] e;
    private DisplayMetrics metrics;

    b(MainActivity mainActivity, boolean z, boolean z2, int i, int[] iArr) {
        this.a = mainActivity;
        this.b = z;
        this.c = z2;
        this.d = i;
        this.e = iArr;
    }

    public final void run() {
        MainActivity.a(this.a, this.b, this.c);
        View inflate = null;
        switch (this.d) {
        	case R.layout.create_world_screen:
        		inflate = createCreateWorldScreen();
        		break;
        	case R.layout.rename_mp_world:
        		inflate = createRenameMpWorld();
        		break;
        	default:
        		Logger.error("Minecraft::LayoutInflater", "Unexpected layout resource " + this.d + ", ignoring...");
        }
        if (inflate != null) {
        	this.a.j.setView(inflate);
            if (this.e != null) {
                for (int findViewById : this.e) {
                    this.a.h.add((EditText) inflate.findViewById(findViewById));
                }
            }
        }
        this.a.j.show();
    }

	private final void connectRecycleableBackground(View view) {
		byte[] dataBg32 = AssetPatch.getAssetBytes(this.a.getAssets(), "resources/bg32.png");
		final Bitmap bg32 = BitmapFactory.decodeByteArray(dataBg32, 0, dataBg32.length);
		BitmapDrawable drawableBg32 = new BitmapDrawable(bg32);
		drawableBg32.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		drawableBg32.setFilterBitmap(false);
		drawableBg32.setAntiAlias(false);
		view.setBackgroundDrawable(drawableBg32);
		this.a.j.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				bg32.recycle();
			}
		});
	}

	private final View createCreateWorldScreen() {
		LinearLayout layout = new LinearLayout(this.a);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new ViewGroup.LayoutParams(540, 400));
		connectRecycleableBackground(layout);

		TextView worldNameLabel = new TextView(this.a);
		worldNameLabel.setText("World name");
		layout.addView(worldNameLabel, new LinearLayout.LayoutParams(540, (int) toComplexUnitDip(24)));

		EditTextAscii worldName = new EditTextAscii(this.a, null);
		worldName.setId(R.id.editText_worldName);
		worldName.setText("Unnamed world");
		worldName.setTransformationMethod(SingleLineTransformationMethod.getInstance());
		worldName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(64) });
		worldName.setImeOptions(EditorInfo.IME_ACTION_DONE);
		worldName.setImeActionLabel("Ok", EditorInfo.IME_ACTION_DONE);
		layout.addView(worldName, new LinearLayout.LayoutParams(540, (int) toComplexUnitDip(48)));

		View divider = new View(this.a);
		layout.addView(divider, new LinearLayout.LayoutParams(500, 10));

		TextView worldSeedLabel = new TextView(this.a);
		worldSeedLabel.setText("Seed for the World Generator");
		layout.addView(worldSeedLabel, new LinearLayout.LayoutParams(540, (int) toComplexUnitDip(24)));

		EditTextAscii worldSeed = new EditTextAscii(this.a, null);
		worldSeed.setId(R.id.editText_worldSeed);
		worldSeed.setText("");
		worldSeed.setTransformationMethod(SingleLineTransformationMethod.getInstance());
		worldSeed.setFilters(new InputFilter[] { new InputFilter.LengthFilter(64) });
		worldSeed.setImeOptions(EditorInfo.IME_ACTION_DONE);
		worldSeed.setImeActionLabel("Ok", EditorInfo.IME_ACTION_DONE);
		layout.addView(worldSeed, new LinearLayout.LayoutParams(540, (int) toComplexUnitDip(48)));

		TextView worldSeedInfo = new TextView(this.a);
		worldSeedInfo.setText("Leave blank for a random seed");
		layout.addView(worldSeedInfo, new LinearLayout.LayoutParams(540, (int) toComplexUnitDip(24)));

		return layout;
	}

	private final View createRenameMpWorld() {
		LinearLayout layout = new LinearLayout(this.a);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new ViewGroup.LayoutParams(540, 400));
		connectRecycleableBackground(layout);

		TextView worldNameRenameLabel = new TextView(this.a);
		worldNameRenameLabel.setText("Save world as");
		layout.addView(worldNameRenameLabel, new LinearLayout.LayoutParams(540, (int) toComplexUnitDip(24)));

		EditTextAscii worldNameRename = new EditTextAscii(this.a, null);
		worldNameRename.setId(R.id.editText_worldNameRename);
		worldNameRename.setText("Saved World");
		worldNameRename.setTransformationMethod(SingleLineTransformationMethod.getInstance());
		worldNameRename.setFilters(new InputFilter[] { new InputFilter.LengthFilter(64) });
		worldNameRename.setImeOptions(EditorInfo.IME_ACTION_DONE);
		worldNameRename.setImeActionLabel("Ok", EditorInfo.IME_ACTION_DONE);
		layout.addView(worldNameRename, new LinearLayout.LayoutParams(540, (int) toComplexUnitDip(48)));

		View divider = new View(this.a);
		layout.addView(divider, new LinearLayout.LayoutParams(500, 10));

		return layout;
	}

	private final float toComplexUnitDip(float px) {
		if (metrics == null) {
			metrics = this.a.getResources().getDisplayMetrics();
		}
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, metrics);
	}
}
