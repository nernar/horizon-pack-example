package com.mojang.minecraftpe.input;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.input.InputManager;

@TargetApi(16)
public class JellyBeanDeviceManager extends InputDeviceManager implements InputManager.InputDeviceListener {
    private final InputManager inputManager;

    native void onInputDeviceAddedNative(int i);

    native void onInputDeviceChangedNative(int i);

    native void onInputDeviceRemovedNative(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public JellyBeanDeviceManager(Context ctx) {
        this.inputManager = (InputManager) ctx.getSystemService("input");
    }

    @Override // com.mojang.minecraftpe.input.InputDeviceManager
    public void register() {
        this.inputManager.getInputDeviceIds();
        this.inputManager.registerInputDeviceListener(this, null);
    }

    @Override // com.mojang.minecraftpe.input.InputDeviceManager
    public void unregister() {
        this.inputManager.unregisterInputDeviceListener(this);
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceAdded(int deviceId) {
        onInputDeviceAddedNative(deviceId);
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceChanged(int deviceId) {
        onInputDeviceChangedNative(deviceId);
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceRemoved(int deviceId) {
        onInputDeviceRemovedNative(deviceId);
    }
}
