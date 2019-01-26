package com.chozabu.android.BikeGame;

import android.view.KeyEvent;

import org.anddev.andengine.entity.scene.Scene;

public interface GameScene {

    Scene onLoadScene();

    void onLoadComplete();

    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent);

    public boolean onKeyUp(final int pKeyCode, final KeyEvent pEvent);

    void frameUpdate(float pSecondsElapsed);
}
