package com.chozabu.android.BikeGame;

import com.badlogic.gdx.math.Vector2;

import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;

//import android.util.Log;
//import com.badlogic.gdx.physics.box2d.World;

public class MaxStepPhysicsWorld extends PhysicsWorld {

    public static final int STEPSPERSECOND_DEFAULT = 60;


    private final float mStepLength;


    public MaxStepPhysicsWorld(final int pStepsPerSecond, final Vector2 pGravity, final boolean pAllowSleep) {
        super(pGravity, pAllowSleep);
        this.mStepLength = 1.0f / pStepsPerSecond;
    }

    public MaxStepPhysicsWorld(final int pStepsPerSecond, final Vector2 pGravity, final boolean pAllowSleep, final int pVelocityIterations, final int pPositionIterations) {
        super(pGravity, pAllowSleep, pVelocityIterations, pPositionIterations);
        this.mStepLength = 1.0f / pStepsPerSecond;
    }

    public float getStepLength() {
        return this.mStepLength;
    }


    @Override
    public void onUpdate(final float pSecondsElapsed) {
        this.mRunnableHandler.onUpdate(this.mStepLength);

        //float stepLength;// = pSecondsElapsed;
        //if(pSecondsElapsed>= this.mStepLength || true){
        //	stepLength = this.mStepLength;
        //Log.i("ABike","WARNING LOW FPS - GOING SLOWMO!");
        //}
        this.mWorld.step(this.mStepLength, this.mVelocityIterations, this.mPositionIterations);

        this.mPhysicsConnectorManager.onUpdate(this.mStepLength);
        //Log.i("IAMMAREADER", ""+1.0f/stepLength);
    }

}
