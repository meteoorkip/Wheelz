package com.chozabu.android.BikeGame;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.ZoomCamera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchException;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.LayoutGameActivity;


public class MainActivity extends LayoutGameActivity {


    ZoomCamera camera;
    Textures textures = new Textures();
    Sounds sounds = new Sounds();
    GameWorld gameWorld = new GameWorld();
    SharedPreferences prefs;
    GameScene currentGameScene;

    @Override
    public void onPause() {
        super.onPause();
        if (sounds != null)
            sounds.stop();
    }


    @Override
    protected void onCreate(final Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //this.currentGameScene = new MainMenuGameScene();
        m_Handler = new Handler();


    }


    private Handler m_Handler;


    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    public void onStop() {
        super.onStop();
        if (sounds != null)
            sounds.stop();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (sounds != null)
            sounds.stop();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (sounds != null)
            sounds.start();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.simplelayout;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.flip_render;
    }


    @Override
    public Engine onLoadEngine() {
        camera = new ZoomCamera(0, 0, StatStuff.CAMERA_WIDTH,
                StatStuff.CAMERA_HEIGHT);
        gameWorld.initEngine(this, camera);
        // camera.setRotation(180.f);
        // camera.setCameraSceneRotation(50.f);
        // camera.setZoomFactor(.05f);
        Engine engine = new Engine(new EngineOptions(true,
                ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(
                StatStuff.CAMERA_WIDTH, StatStuff.CAMERA_HEIGHT),
                camera).setNeedsSound(true));

        try {
            if (MultiTouch.isSupported(this)) {
                engine.setTouchController(new MultiTouchController());
            }
        } catch (final MultiTouchException e) {
        }

        return engine;
    }

    public void enableAccel(IAccelerometerListener pAccelerometerListener) {
        this.enableAccelerometerSensor(pAccelerometerListener);
    }

    @Override
    public void onLoadResources() {
        System.gc();
        textures.init(this);
        sounds.init(this);
        sounds.start();

        gameWorld.initRes(textures, sounds);

        System.gc();

    }

    @Override
    public Scene onLoadScene() {
        //return null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String toLoad = extras
                    .getString("com.chozabu.android.BikeGame.toLoad");

            extras.clear();


            currentGameScene = new GameRoot(MainActivity.this, -1, -1, toLoad);
            Scene nScene = currentGameScene.onLoadScene();
            nScene.registerUpdateHandler(new IUpdateHandler() {

                @Override
                public void onUpdate(float pSecondsElapsed) {
                    MainActivity.this.frameUpdate(1.0f / 45.f);
                }

                @Override
                public void reset() {

                }
            });
            return nScene;
        }

        AEMainMenu temp = new AEMainMenu(this);
        temp.doIntroDialog();
        currentGameScene = temp;
        Scene nScene = currentGameScene.onLoadScene();
        nScene.registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void onUpdate(float pSecondsElapsed) {
                MainActivity.this.frameUpdate(1.0f / 45.f);
            }

            @Override
            public void reset() {

            }
        });
        //temp.doIntroDialog();
        temp = null;

        return nScene;
    }

    public void frameUpdate(float pSecondsElapsed) {
        this.currentGameScene.frameUpdate(1.0f / 45.f);
    }

    public void setMainMenu() {
        this.disableAccelerometerSensor();
        this.getEngine().runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                //BufferObjectManager.getActiveInstance().clear();
                currentGameScene = new AEMainMenu(MainActivity.this);

                Scene nScene = currentGameScene.onLoadScene();
                nScene.registerUpdateHandler(new IUpdateHandler() {

                    @Override
                    public void onUpdate(float pSecondsElapsed) {
                        MainActivity.this.frameUpdate(1.0f / 45.f);
                    }

                    @Override
                    public void reset() {

                    }
                });
                MainActivity.this.getEngine().setScene(nScene);
                currentGameScene.onLoadComplete();

            }
        });

    }

    public void setInGame(final int packID, final int levelID) {
        this.getEngine().runOnUpdateThread(new Runnable() {
            @Override
            public void run() {

                //BufferObjectManager.getActiveInstance().clear();
                currentGameScene = new GameRoot(MainActivity.this, packID, levelID, null);
                Scene nScene = currentGameScene.onLoadScene();
                nScene.registerUpdateHandler(new IUpdateHandler() {

                    @Override
                    public void onUpdate(float pSecondsElapsed) {
                        MainActivity.this.frameUpdate(1.0f / 45.f);
                    }

                    @Override
                    public void reset() {

                    }
                });

                MainActivity.this.getEngine().setScene(nScene);
                currentGameScene.onLoadComplete();

            }
        });
    }

    @Override
    public void onLoadComplete() {
        if (StatStuff.isDemo) loadAds();
        currentGameScene.onLoadComplete();
        //doIntroDialog();

    }


    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (currentGameScene == null)
            return super.onKeyDown(pKeyCode, pEvent);

        //Debug.stopMethodTracing();
        if (currentGameScene.onKeyDown(pKeyCode, pEvent))
            return true;
        return super.onKeyDown(pKeyCode, pEvent);
    }

    @Override
    public boolean onKeyUp(final int pKeyCode, final KeyEvent pEvent) {
        if (currentGameScene == null)
            return super.onKeyUp(pKeyCode, pEvent);
        if (currentGameScene.onKeyUp(pKeyCode, pEvent))
            return true;
        return super.onKeyUp(pKeyCode, pEvent);
    }


    public Scene getScene() {
        return this.mEngine.getScene();
    }


    private void loadAds() {

    }


    void quitFunc() {
        if (sounds != null)
            sounds.stop();
        this.finish();
    }

    void superQuitFunc() {
        quitFunc();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

}
