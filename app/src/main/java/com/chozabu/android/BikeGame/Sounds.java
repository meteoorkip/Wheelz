package com.chozabu.android.BikeGame;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import java.io.IOException;


public class Sounds {

    BaseGameActivity root = null;
    public Sound mEngineSound = null;
    public Sound mBeBoopSound = null;
    public Sound mCrashSound = null;
    public Sound mCollectedSound = null;
    //public Sound mThunkSound = null;
    //public Sound mMThunkSound = null;
    public Sound mHitBodySound = null;
    public Sound mHitWheelSound = null;
    public Sound mSkidSound = null;
    private SharedPreferences prefs;

    public void init(BaseGameActivity rootIn) {
        root = rootIn;
        SoundFactory.setAssetBasePath("mfx/");
        try {
            mEngineSound = SoundFactory.createSoundFromAsset(root.getEngine().getSoundManager(), root, "car_idle.wav");
            mBeBoopSound = SoundFactory.createSoundFromAsset(root.getEngine().getSoundManager(), root, "CLICK21A.WAV");
            mCrashSound = SoundFactory.createSoundFromAsset(root.getEngine().getSoundManager(), root, "shortcrash.wav");
            mCollectedSound = SoundFactory.createSoundFromAsset(root.getEngine().getSoundManager(), root, "collected.wav");
            mHitBodySound = SoundFactory.createSoundFromAsset(root.getEngine().getSoundManager(), root, "bodyHit.wav");
            mHitWheelSound = SoundFactory.createSoundFromAsset(root.getEngine().getSoundManager(), root, "wheelHit.wav");
            mSkidSound = SoundFactory.createSoundFromAsset(root.getEngine().getSoundManager(), root, "skidtrial1.wav");
        } catch (final IOException e) {
            Debug.e("Error", e);
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(root);


        //Log.i("ABike","LOADED SOUNDS");
    }

    public void stop() {
        if (mEngineSound == null) return;
        mEngineSound.stop();
        mSkidSound.stop();
    }

    public void start() {
        if (mEngineSound == null) return;
        if (!prefs.getBoolean("soundOn", true)) return;
        mEngineSound.setLooping(true);
        mEngineSound.setRate(0.5f);
        mEngineSound.setVolume(0.3f);
        mSkidSound.setLooping(true);
        //mSkidSound.setRate
        mSkidSound.setVolume(0f);
        mSkidSound.stop();
        mSkidSound.play();
        //if(prefs.getBoolean("soundOn", true))
        //Log.i("ABike","PLAYING SOUNDS");
        mEngineSound.stop();
        mEngineSound.play();
    }
}
