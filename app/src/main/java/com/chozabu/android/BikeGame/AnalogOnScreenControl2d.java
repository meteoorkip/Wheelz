package com.chozabu.android.BikeGame;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

//import AnalogOnScreenControl;

public class AnalogOnScreenControl2d extends AnalogOnScreenControl {

    public AnalogOnScreenControl2d(int pX, int pY, Camera pCamera,
                                   TextureRegion pControlBaseTextureRegion,
                                   TextureRegion pControlKnobTextureRegion, float pTimeBetweenUpdates,
                                   long pOnControlClickMaximumMilliseconds,
                                   IAnalogOnScreenControlListener pAnalogOnScreenControlListener) {
        super(pX, pY, pCamera, pControlBaseTextureRegion, pControlKnobTextureRegion,
                pTimeBetweenUpdates, pOnControlClickMaximumMilliseconds,
                pAnalogOnScreenControlListener);
    }


    @Override
    protected boolean onHandleControlBaseTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        //super.mClickDetector.onSceneTouchEvent(null, pSceneTouchEvent);
        return super.onHandleControlBaseTouched(pSceneTouchEvent, pTouchAreaLocalX, this.getControlBase().getBaseHeight() / 2f);
    }

}
