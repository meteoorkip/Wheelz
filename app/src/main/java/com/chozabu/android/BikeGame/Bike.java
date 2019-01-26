package com.chozabu.android.BikeGame;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.LineJoint;
import com.badlogic.gdx.physics.box2d.joints.LineJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.particle.ParticleSystem;
import org.anddev.andengine.entity.particle.emitter.PointParticleEmitter;
import org.anddev.andengine.entity.particle.modifier.AccelerationInitializer;
import org.anddev.andengine.entity.particle.modifier.AlphaModifier;
import org.anddev.andengine.entity.particle.modifier.ExpireModifier;
import org.anddev.andengine.entity.particle.modifier.RotationInitializer;
import org.anddev.andengine.entity.particle.modifier.ScaleModifier;
import org.anddev.andengine.entity.particle.modifier.VelocityInitializer;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.opengl.util.GLHelper;
import org.anddev.andengine.util.MathUtils;

import javax.microedition.khronos.opengles.GL10;

public class Bike {
    GameWorld gameWorld;
    Sprite mBodyImg;
    public Body mBody;
    private Sprite fWheelImg;
    public Body fWheel;
    private Sprite bWheelImg;
    public Body bWheel;
    public Body roofSensor;
    LineJointDef fJointDef;
    LineJointDef bJointDef;
    LineJoint fJoint;
    LineJoint bJoint;
    private boolean isDead = false;

    private float hitTime = 0;


    private float truckLength = 111f;
    private float truckHeight = 49f;
    private float wheelDiam = 42;
    //private final float wheelRad = wheelDiam/2f;

    private final float wheelOut = 5;
    private Vector2 fOffset = new Vector2(truckLength - wheelDiam + wheelOut, 26);
    private Vector2 bOffset = new Vector2(-wheelOut, 26);
    private Vector2 offset = new Vector2(-truckLength / 2, -truckHeight - 26f / 2f);
    public boolean facingRight = true;

    private float massMult = 5f;
    private float speedMult = 25f;
    private float spinMult = 1f * massMult;
    //private float torqueMult = 2f*massMult;
    //private float torque = 40*massMult;
    private float suspension = 80 * massMult;
    private float suspensionSpeed = 1 * massMult;
    float wheelGrip = 5.0f;
    //private boolean facingRight = true;
    public Joint roofJoint;

    float eVol = 0.4f;
    private float currentAccel = 0f;
    private float leanForce = 0f;
    //public static final short MASKBITS_CIRCLE = CATEGORYBIT_WALL + CATEGORYBIT_CIRCLE; // Missing: CATEGORYBIT_BOX
    //public static final FixtureDef BIKE_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_CIRCLE, MASKBITS_CIRCLE, (short)0);


    SharedPreferences prefs;
    private boolean wheelsAttached = true;
    //private float mBodyAlphaGoal = 0;

    public boolean hitSoundsOn = false;


    public Bike(GameWorld rootIn, Vector2 startPos) {
        startPos.add(offset);
        gameWorld = rootIn;

        prefs = PreferenceManager.getDefaultSharedPreferences(this.gameWorld.root);
        hitSoundsOn = prefs.getBoolean("hitSoundOn", true);


        String inStr;
        inStr = prefs.getString("cheatsString", "");
        turboOn = inStr.contains("RocketMan");
        inStr = prefs.getString("wheelSize", "42");
        //Log.d("ABike","input is: "+inStr);
        wheelDiam = Float.parseFloat(inStr);

        PhysicsWorld physicsWorld = gameWorld.mPhysicsWorld;

        //Main Body
        this.mBodyImg = new Sprite(startPos.x, startPos.y, truckLength, truckHeight, gameWorld.textures.mTruckBodyTextureRegion) {
            public float altRotation = 0;

            @Override
            protected void applyRotation(final GL10 pGL) {
                /* Disable culling so we can see the backside of this sprite. */
                GLHelper.disableCulling(pGL);
                final float rotation = this.mRotation;
                if (rotation != 0) {
                    final float rotationCenterX = this.mRotationCenterX;
                    final float rotationCenterY = this.mRotationCenterY;

                    pGL.glTranslatef(rotationCenterX, rotationCenterY, 0);
                    /* Note we are applying rotation around the y-axis and not the z-axis anymore! */

                    pGL.glRotatef(rotation, 0, 0, 1);
                    pGL.glRotatef(this.altRotation, 0, 1, 0);
                    pGL.glTranslatef(-rotationCenterX, -rotationCenterY, 0);
                }
            }

            @Override
            public void setAlpha(float rots) {
                this.altRotation = rots;
            }

            @Override
            public float getAlpha() {
                return this.altRotation;
            }

            @Override
            protected void drawVertices(final GL10 pGL, final Camera pCamera) {
                //GLHelper.blendFunction(pGL, GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
                //GLHelper.blendFunction(pGL, GL10.GL_ONE, GL10.GL);
                //pGL.glAlphaFunc(GL10.GL_GEQUAL, -1.1f);
                //if (gameWorld.uglyMode) GLHelper.disableBlend(pGL);
                super.drawVertices(pGL, pCamera);

                /* Enable culling as 'normal' entities profit from culling. */
                GLHelper.enableCulling(pGL);
            }
			/*@Override
			protected void applyTranslation(final GL10 pGL) {
				pGL.glTranslatef(this.mX, this.mY, 0);
			}*/
        };

        this.mBodyImg.setUpdatePhysics(false);
        final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(1.54f * massMult, 0.3f, 0.4f);
        Rectangle bodyRect = new Rectangle(mBodyImg.getX(), mBodyImg.getY() + truckHeight * 0.2f, truckLength, truckHeight * 0.6f);
        this.mBody = PhysicsFactory.createBoxBody(physicsWorld, bodyRect, BodyType.DynamicBody, carFixtureDef);
        this.mBody.setUserData(new UserData(this.mBodyImg, null));
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mBodyImg, this.mBody, true, true, false, false));
        gameWorld.getScene().getTopLayer().addEntity(this.mBodyImg);

        //Roof sensor
        //Sprite roofSensorRect = new Sprite(mBodyImg.getX()-1f+truckLength*0.3f, mBodyImg.getY()-truckHeight*0.1f,truckLength*0.4f,truckHeight*0.2f, gameWorld.textures.mTruckFWheelTextureRegion);
        final FixtureDef roofSensorDef = PhysicsFactory.createFixtureDef(0.1f, 0.3f, 0.7f, true);
        Rectangle roofSensorRect = new Rectangle(mBodyImg.getX() - 1f + truckLength * 0.3f, mBodyImg.getY() - truckHeight * 0.1f, truckLength * 0.4f, truckHeight * 0.2f);
        this.roofSensor = PhysicsFactory.createBoxBody(physicsWorld, roofSensorRect, BodyType.DynamicBody, roofSensorDef);

        //physicsWorld.registerPhysicsConnector(new PhysicsConnector(roofSensorRect, this.roofSensor, true, true, false, false));
        //gameWorld.getScene().getTopLayer().addEntity(roofSensorRect);


        //wheel fixture def
        final FixtureDef wheelFixtureDef = PhysicsFactory.createFixtureDef(1.5f * massMult, 0.15f, wheelGrip, false,
                StatStuff.CATEGORYBIT_FG_DYNAMIC, StatStuff.MASKBITS_FG_DYNAMIC, (short) 0);
        //front wheel
        this.fWheelImg = new Sprite(startPos.x + fOffset.x, startPos.y + fOffset.y, wheelDiam, wheelDiam, gameWorld.textures.mTruckFWheelTextureRegion);

        this.fWheelImg.setUpdatePhysics(false);
        //final FixtureDef fWheelFixtureDef = PhysicsFactory.createFixtureDef(1.5f*massMult, 0.15f, wheelGrip);
        this.fWheel = PhysicsFactory.createCircleBody(physicsWorld, this.fWheelImg, BodyType.DynamicBody, wheelFixtureDef);
        this.fWheel.setUserData(new UserData(this.fWheelImg, null));
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this.fWheelImg, this.fWheel, true, true, false, false));
        gameWorld.getScene().getTopLayer().addEntity(this.fWheelImg);


        //back wheel
        this.bWheelImg = new Sprite(startPos.x + bOffset.x, startPos.y + bOffset.y, wheelDiam, wheelDiam, gameWorld.textures.mTruckBWheelTextureRegion);

        this.bWheelImg.setUpdatePhysics(false);
        //final FixtureDef bWheelFixtureDef = PhysicsFactory.createFixtureDef(1.5f*massMult, 0.15f, wheelGrip);
        this.bWheel = PhysicsFactory.createCircleBody(physicsWorld, this.bWheelImg, BodyType.DynamicBody, wheelFixtureDef);
        this.bWheel.setUserData(new UserData(this.bWheelImg, null));
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this.bWheelImg, this.bWheel, true, true, false, false));
        gameWorld.getScene().getTopLayer().addEntity(this.bWheelImg);

        this.fWheel.setAngularDamping(0.5f);
        this.bWheel.setAngularDamping(0.5f);

        //roof joint
        WeldJointDef wjd = new WeldJointDef();
        wjd.initialize(mBody, roofSensor, roofSensor.getPosition());
        roofSensor.setUserData(new UserData(this.mBodyImg, null));
        roofJoint = physicsWorld.createJoint(wjd);


        //int inInt = prefs.getInt("suspensionHeight", 2);
		/*inStr = prefs.getString("suspensionHeight", "2");
		//Log.d("ABike","input is: "+inStr);
		int suspensionHeight = Integer.parseInt(inStr);
		float upperTranslation = 0.01f;
		float lowerTranslation = -0.3f;
		if(suspensionHeight==1){
			upperTranslation = 0.15f;
			lowerTranslation = -0.15f;
		}else if(suspensionHeight==3){
			upperTranslation = -0.15f;
			lowerTranslation = -0.45f;
		}*/


        inStr = prefs.getString("suspensionHeight", "0.01");
        //Log.d("ABike","input is: "+inStr);
        float upperTranslation = Float.parseFloat(inStr);
        int check = (int) upperTranslation;
        if (check == upperTranslation) {
            Editor editor = prefs.edit();
            editor.remove("suspensionHeight");
            editor.commit();
            upperTranslation = 0.01f;
        }
        inStr = prefs.getString("suspensionLength", "1.0");
        float suspTravel = 0.3f * Float.parseFloat(inStr);
        float lowerTranslation = upperTranslation - suspTravel;

        inStr = prefs.getString("suspensionStiffness", "1.0");
        float suspStiff = Float.parseFloat(inStr);


        //front/back wheel joints
        //LineJointDef RJD;
        this.fJointDef = new LineJointDef();
        this.fJointDef.initialize(mBody, fWheel, fWheel.getPosition(), new Vector2(0, 1));
        this.fJointDef.enableMotor = true;
        this.fJointDef.upperTranslation = upperTranslation;
        this.fJointDef.lowerTranslation = lowerTranslation;
        this.fJointDef.enableLimit = true;
        this.fJointDef.motorSpeed = suspensionSpeed * suspStiff;
        this.fJointDef.maxMotorForce = suspension * suspStiff;
        this.fJoint = (LineJoint) physicsWorld.createJoint(this.fJointDef);

        this.bJointDef = new LineJointDef();
        this.bJointDef.initialize(mBody, bWheel, bWheel.getPosition(), new Vector2(0, 1));
        this.bJointDef.enableMotor = true;
        this.bJointDef.upperTranslation = upperTranslation;
        this.bJointDef.lowerTranslation = lowerTranslation;
        this.bJointDef.enableLimit = true;
        this.bJointDef.motorSpeed = suspensionSpeed * suspStiff;
        this.bJointDef.maxMotorForce = suspension * suspStiff;
        this.bJoint = (LineJoint) physicsWorld.createJoint(this.bJointDef);
        //detachWheels();

        initPSystem();

    }

    ParticleSystem particleSystem;
    PointParticleEmitter ppe;
    VelocityInitializer initialv;

    void initPSystem() {
        ppe = new PointParticleEmitter(0f, 0f);
        particleSystem = new ParticleSystem(ppe, 20, 20, 50, gameWorld.textures.mDirtClodTextureRegion);
        initialv = new VelocityInitializer(0, 0, 0, 0);


        {
            //final ParticleSystem particleSystem = new ParticleSystem(new PointParticleEmitter(0, CAMERA_HEIGHT), 6, 10, 200, this.mParticleTextureRegion);
            //particleSystem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
            particleSystem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

            particleSystem.addParticleInitializer(initialv);
            particleSystem.addParticleInitializer(new AccelerationInitializer(0, 400));
            particleSystem.addParticleInitializer(new RotationInitializer(0.0f, 360.0f));
            //particleSystem.addParticleInitializer(new ColorInitializer(1.0f, 1.0f, 1.0f));

            particleSystem.addParticleModifier(new ScaleModifier(0.4f, 0.2f, 0, 1.0f));
            particleSystem.addParticleModifier(new ExpireModifier(1.0f));
            particleSystem.addParticleModifier(new AlphaModifier(1.0f, 0.0f, 0.7f, 1.0f));
            //particleSystem.addParticleModifier(new AlphaModifier(0.0f, 1.0f, 3.5f, 4.5f));
            //particleSystem.addParticleModifier(new ColorModifier(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 11.5f));
            //particleSystem.addParticleModifier(new AlphaModifier(1.0f, 0.0f, 4.5f, 11.5f));

            //scene.getTopLayer().addEntity(particleSystem);
        }
        gameWorld.getScene().getTopLayer().addEntity(particleSystem);
    }


    boolean canSoundFJoint = true;
    boolean canSoundBJoint = true;
    float whTime = 0f;
    private boolean turboOn;
    float mudTime = 0.01f;

    //float skidRate = 0.95f;
    public void frameUpdate(float pSecondsElapsed) {
        gameWorld.sounds.mSkidSound.setVolume(gameWorld.sounds.mSkidSound.getVolume() * 0.9f);
        //gameWorld.sounds.mEngineSound.setVolume(gameWorld.sounds.mEngineSound.getVolume()*0.99f);
        //skidRate=skidRate*0.95f+0.05f;
        //gameWorld.sounds.mSkidSound.setRate(skidRate);

        if (hitTime > 0)
            hitTime -= pSecondsElapsed;

        if (mudTime > 0)
            mudTime -= pSecondsElapsed;
        else
            particleSystem.setParticlesSpawnEnabled(false);
        calcERate();
        powerWheels();
        //this.fJoint.getJointTranslation()
        //this.mBodyImg.setAlpha(this.mBodyImg.getAlpha()*0.7f+mBodyAlphaGoal*0.3f);

        //mBody.applyAngularImpulse(this.leanForce);
        mBody.applyTorque(this.leanForce * 60);
    }

    private void powerWheels() {
        float fwspeed = this.fWheel.getAngularVelocity();
        float bwspeed = this.bWheel.getAngularVelocity();
        if (fwspeed < 0) fwspeed = -fwspeed;
        if (bwspeed < 0) bwspeed = -bwspeed;
        if ((currentAccel < 0 && facingRight) || (currentAccel > 0 && !facingRight)) {
            //if(bwspeed>2 || fwspeed>2){
            this.fWheel.setAngularVelocity(0f);
            this.bWheel.setAngularVelocity(0f);
            return;
            //}
        } else {
            float fwDiv = 1;
            float bwDiv = 1;
            if (fwspeed > 11) fwDiv = fwspeed - 10;
            if (bwspeed > 11) bwDiv = bwspeed - 10;
            if (fwspeed < 50) this.fWheel.applyTorque(currentAccel * 0.6f / fwDiv * 60);
            if (bwspeed < 50) this.bWheel.applyTorque(currentAccel * 0.6f / bwDiv * 60);
            if (turboOn) {
                float angle = mBody.getAngle();
                mBody.applyForce(new Vector2((float) Math.cos(angle), (float) Math.sin(angle)).mul(currentAccel * 15f), mBody.getPosition());
                //mBody.applyForce(mBody.getLinearVelocity().nor().mul(currentAccel*10f),mBody.getPosition());
            }
        }
    }

    private void calcERate() {
        float espeed = this.bWheel.getAngularVelocity() * 0.07f;
        if (espeed < 0) espeed = -espeed;
        espeed = (float) Math.sqrt(espeed);
        espeed += 0.4f;
        gameWorld.sounds.mEngineSound.setRate(espeed);
    }

    public void stopWheels() {
        this.bWheel.setAngularVelocity(0f);
        this.fWheel.setAngularVelocity(0f);
        calcERate();
    }

    public void flipDirecion() {
        LineJoint temp = this.bJoint;
        this.bJoint = this.fJoint;
        this.fJoint = temp;

        Body tempBody = this.bWheel;
        this.bWheel = this.fWheel;
        this.fWheel = tempBody;

        facingRight = !facingRight;
        if (facingRight)
            //this.mBodyAlphaGoal  = 0f;
            this.mBodyImg.setAlpha(0);//overridden to provide 3d rotation
        else
            //this.mBodyAlphaGoal = 180f;
            this.mBodyImg.setAlpha(180);

        currentAccel = -currentAccel;
        //setSpeed(this.currentAccel/this.speedMult);
        //this.mBodyImg.setScaleX(-this.mBodyImg.getScaleX());
        //this.mBodyImg.setAltRot
    }

    void calcVol(float inSpeed) {
        if (inSpeed < 0) inSpeed = -inSpeed;
        eVol = (eVol * 0.6f) + (inSpeed * 0.5f + .5f) * .4f;
        gameWorld.sounds.mEngineSound.setVolume(eVol);
    }

    public void setSpeed(float inSpeed) {
        if (isDead) return;

        if (inSpeed >= 0) {
            if (inSpeed > 0.1) inSpeed -= 0.1;
            else {
                //enableMotor = false;
                //return;
            }
        } else {
            if (inSpeed < -0.1) inSpeed += 0.1;
            else {
                //enableMotor = false;
                //return;
            }
            //inSpeed*=0.4f;
        }
        calcVol(inSpeed);
        //if (inSpeed < 3 && inSpeed > -3) enableMotor = false;
        inSpeed *= speedMult;
        if (!facingRight) inSpeed = -inSpeed;
        this.currentAccel = inSpeed;
        //if(this.bJoint.getMotorSpeed()!=inSpeed)
        //	Log.i("ABike", "speed set to: "+inSpeed);
        //this.bJoint.setMotorSpeed(inSpeed);
        //this.bJoint.enableMotor(enableMotor);
        //this.fJoint.setMotorSpeed(inSpeed);
        //this.fJoint.enableMotor(enableMotor);

    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean pDead) {
        this.setSpeed(0);
        this.modRot(0);
        isDead = pDead;
    }

    public boolean containsBody(Body body) {
        if (body == this.mBody
                || body == this.fWheel
                || body == this.bWheel
                || body == this.roofSensor) return true;
        return false;
    }

    public void modRot(float force) {
        if (isDead) return;
        this.leanForce = force * spinMult;

        //force*=spinMult;
        //mBody.applyAngularImpulse(force);
    }


    void setPos(Vector2 inVeca) {
        Vector2 inVec = inVeca.cpy();
        inVec.mul(1.f / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
        inVec.sub(this.mBody.getPosition());
        Vector2 pos;
        float angle;
        pos = this.mBody.getPosition().add(inVec);
        angle = this.mBody.getAngle();
        this.mBody.setTransform(pos, angle);


        pos = this.fWheel.getPosition().add(inVec);
        angle = this.fWheel.getAngle();
        this.fWheel.setTransform(pos, angle);

        pos = this.bWheel.getPosition().add(inVec);
        angle = this.bWheel.getAngle();
        this.bWheel.setTransform(pos, angle);
        //this.bWheel.setLinearVelocity(new Vector2(0,0));
    }

    public void attachWheels() {
		/*gameWorld.root.getEngine().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {*/
        if (wheelsAttached) return;
        wheelsAttached = true;
        fJoint = (LineJoint) gameWorld.mPhysicsWorld.createJoint(fJointDef);
        bJoint = (LineJoint) gameWorld.mPhysicsWorld.createJoint(bJointDef);
        //	}});

    }

    public void detachWheels() {
        gameWorld.root.getEngine().runOnUpdateThread(new Runnable() {
            @Override
            public void run() {

                if (!wheelsAttached) return;
                wheelsAttached = false;
                gameWorld.mPhysicsWorld.destroyJoint(fJoint);
                fJoint = null;
                gameWorld.mPhysicsWorld.destroyJoint(bJoint);
                bJoint = null;

            }
        });

    }

    public void beginContact(Contact contact) {
        //sounds - body impact
        if (!this.hitSoundsOn) return;
        //if(this.hitSoundsOn)
        if (this.mBody == (contact.getFixtureA().getBody())
                || this.mBody == (contact.getFixtureB()
                .getBody())) {
            Vector2 va = contact.getFixtureA()
                    .getBody().getLinearVelocity();
            va.sub(contact.getFixtureB()
                    .getBody().getLinearVelocity());
            float iForce = va.len2();

            if (iForce > 1.75f) {

                if (hitTime <= 0) {
                    hitTime = 0.1f;
                    float iVol = iForce - 0.8f;
                    iVol *= 0.35f;
                    iVol = MathUtils.bringToBounds(0f, 1f, iVol);
                    gameWorld.sounds.mHitBodySound.stop();
                    gameWorld.sounds.mHitBodySound.setVolume(iVol);
                    gameWorld.sounds.mHitBodySound.play();
                }

            }
        }
        //wheel impact
        if (this.fWheel == (contact.getFixtureA().getBody())
                || this.fWheel == (contact.getFixtureB()
                .getBody()) ||
                this.bWheel == (contact.getFixtureA().getBody())
                || this.bWheel == (contact.getFixtureB()
                .getBody())) {
            float iForce;
            Vector2 contactPos = contact.GetWorldManifold().getPoints()[0];
            Vector2 contactNormal = contact.GetWorldManifold().getNormal();

            Vector2 va = contact.getFixtureA()
                    .getBody().getLinearVelocityFromWorldPoint(contactPos);
            va.sub(contact.getFixtureB()
                    .getBody().getLinearVelocityFromWorldPoint(contactPos));
            iForce = va.len();

            float cVol = gameWorld.sounds.mSkidSound.getVolume();
            float miForce = iForce * 0.2f;
            if (miForce > cVol) {
                gameWorld.sounds.mSkidSound.setVolume(iForce / 10f);
            }
            //miForce=miForce*0.1f+0.6f;
            //if(miForce>skidRate)skidRate=miForce;

            if (iForce > 0.4f) {

                mudTime = 0.1f;

                particleSystem.setParticlesSpawnEnabled(true);
                ppe.setCenter(contactPos.x * 32f - 16f, contactPos.y * 32f - 16f);

                contactNormal.mul(iForce);
                va.sub(contactNormal);
                va.mul(16f);
                initialv.setVelocityX(-va.x - 32f, -va.x + 32f);
                initialv.setVelocityY(-va.y, -va.y - 32f);

                Vector2 vb = contact.getFixtureA()
                        .getBody().getLinearVelocity();
                vb.sub(contact.getFixtureB()
                        .getBody().getLinearVelocity());
                iForce = Math.min(iForce, vb.len());
                if (hitTime <= 0 && iForce > 8.2f) {
                    hitTime = 0.1f;
                    float iVol = iForce - 8.2f;
                    iVol *= 0.4f;
                    iVol = MathUtils.bringToBounds(0f, 1f, iVol);

                    gameWorld.sounds.mHitWheelSound.stop();
                    gameWorld.sounds.mHitWheelSound.setVolume(iVol);
                    gameWorld.sounds.mHitWheelSound.play();
                }

            }
        }

    }


}
