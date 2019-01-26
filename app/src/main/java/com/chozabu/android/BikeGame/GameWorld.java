package com.chozabu.android.BikeGame;

import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Xml;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.anddev.andengine.engine.camera.ZoomCamera;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.primitive.Polygon;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.triangulation.EarClippingTriangulator;
import org.anddev.andengine.extension.physics.box2d.util.triangulation.ITriangulationAlgoritm;
import org.anddev.andengine.opengl.buffer.BufferObject;
import org.anddev.andengine.opengl.buffer.BufferObjectManager;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.PolygonTextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.MathUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//import android.widget.Toast;

public class GameWorld {
    BaseGameActivity root = null;
    private Scene mScene;
    final static String dbt = StatStuff.dbt;

    public PhysicsWorld mPhysicsWorld;
    Vector2 gravity = new Vector2(0, 9.081f);
    //Vector2 gravity = new Vector2(0, 0.0f);

    private String mName;
    // Sprite end;
    // Body endBody;

    Vector2 playerStart = null;

    String levelStr = "";
    boolean levelFromFile;

    public int levelId = 1;
    public String levelPrefix = null;

    Textures textures = null;
    Sounds sounds = null;
    public Bike bike;

    ZoomCamera mCamera;
    private boolean rotateCam = false;
    private boolean zoomCam = false;

    SharedPreferences prefs;
    public boolean uglyMode = false;
    public List<Body> endList = null;
    public List<Body> strawBerryList = null;
    public List<Body> wreckerList = null;
    public int berryCount = 0;
    //private String currentPack = null;
    //private ParallaxBackground2d mParallaxBackground;
    private int currentPackID;
    private boolean isPaused = true;

    void initEngine(BaseGameActivity rootIn, ZoomCamera cameraIn) {
        root = rootIn;
        mCamera = cameraIn;

        prefs = PreferenceManager.getDefaultSharedPreferences(this.root);
        this.zoomCam = prefs.getBoolean("camZoomOn", false);
        this.rotateCam = (prefs.getBoolean("camRotOn", false)
                && prefs.getBoolean("tiltOn", false));
        this.uglyMode = prefs.getBoolean("uglyMode", false);

        String cheatsString = prefs.getString("cheatsString", "");
        if (cheatsString.contains("Moon")) {
            gravity = new Vector2(0, 6.0f);
        } else if (cheatsString.contains("Jupiter")) {
            gravity = new Vector2(0, 16.0f);
        }
    }

    void initRes(Textures texIn, Sounds soundIn) {
        textures = texIn;
        sounds = soundIn;
    }

    void initScene(Scene sceneIn) {
        mScene = sceneIn;

        if (!uglyMode) {
            //String bgName = "gfx/stars.jpg";
            //String bgName = "gfx/bgm.png";
            //if (Math.random()>0.1f)bgName = "gfx/skyBG512.jpg";
            final SpriteBackground bg = new SpriteBackground(
                    new Sprite(0, 0, StatStuff.CAMERA_WIDTH, StatStuff.CAMERA_HEIGHT, textures.mBackGroundTextureRegion)
            );
			/*final RepeatingSpriteBackground rbg = new RepeatingSpriteBackground(
					StatStuff.CAMERA_WIDTH, StatStuff.CAMERA_HEIGHT,
					root.getEngine().getTextureManager(),
					new AssetTextureSource(this.root, bgName));*/

            // final ParallaxBackground autoParallaxBackground = new
            // ParallaxBackground(0, 0, 0);

            // autoParallaxBackground.addParallaxEntity(new ParallaxEntity(10.0f
            // ,
            // new Sprite(0, 0, mBackGroundTextureRegion)));
            //mParallaxBackground = new ParallaxBackground2d(0,0,0.1f);

            //mScene.setBackground(mParallaxBackground);
            mScene.setBackground(bg);
            mScene.setBackgroundEnabled(true);
        }

        String inStr = prefs.getString("fpsLowLimit", "30");
        //Log.d("ABike", "input is: " + inStr);
        int minFps = Integer.parseInt(inStr);
        minFps = 45;
        mPhysicsWorld = new MaxStepPhysicsWorld(minFps, gravity, true, 8, 6);
        //mPhysicsWorld = new FixedStepPhysicsWorld(45, gravity, false, 8, 6);
        mPhysicsWorld.setContinuousPhysics(false);
        mPhysicsWorld.setWarmStarting(true);
    }

    //boolean hasInit = false;
    void initLoaded() {
        //if (hasInit){
        //	return;
        //}
        //hasInit = true;

    }


    public void frameUpdate(float pSecondsElapsed) {
        if (bike != null) {
            Vector2 vel = bike.mBody.getLinearVelocity();
            vel.mul(9.6f * 1.5f);
            float spr = 0.1f;
            if (isPaused) spr *= 0.2f;
            float sprInv = 1f - spr;
            float xp = (bike.mBodyImg.getX() + bike.mBodyImg.getWidth() * .5f + vel.x) * spr + mCamera.getCenterX() * sprInv;
            float yp = (bike.mBodyImg.getY() + bike.mBodyImg.getHeight() * .5f + vel.y) * spr + mCamera.getCenterY() * sprInv;
            mCamera.setCenter(xp, yp);

            if (zoomCam) {
                float zoom = vel.len() * 0.0002f;
                zoom = mCamera.getZoomFactor() * 0.99f + (0.03f - zoom);
                zoom = MathUtils.bringToBounds(.75f, 1.25f, zoom);
                mCamera.setZoomFactor(zoom);
            }

            bike.frameUpdate(pSecondsElapsed);
            if (rotateCam)
                mCamera.setRotation(-bike.mBodyImg.getRotation());
        }
        //mParallaxBackground.setParallaxValue(mCamera.getCenterX(), mCamera.getCenterY());
    }

    void pause() {
        isPaused = true;
        mScene.unregisterUpdateHandler(mPhysicsWorld);
    }

    void unPause() {
        isPaused = false;
        mScene.registerUpdateHandler(mPhysicsWorld);
    }

    void clearLvl() {
        int currentlayer = 0;
        while (currentlayer < mScene.getLayerCount()) {
            while (mScene.getLayer(currentlayer).getEntityCount() > 0) {
                mScene.getLayer(currentlayer).removeEntity(0);
            }
            currentlayer++;
        }
/*
		while (mScene.getTopLayer().getEntityCount() > 0) {
			mScene.getTopLayer().removeEntity(0);
		}
		while (mScene.getBottomLayer().getEntityCount() > 0) {
			mScene.getBottomLayer().removeEntity(0);
		}*/
        Iterable<Body> bs = this.mPhysicsWorld.getBodies();
        for (Body b : bs) {
            this.mPhysicsWorld.destroyBody(b);
            b = null;
        }

        this.mPhysicsWorld.clearPhysicsConnectors();
        //BufferObjectManager.getActiveInstance().clear();


    }

    void nextLevel() {

        if (levelFromFile) {

			/*Intent mainMenuIntent = new Intent(root, AEMainMenu.class);
			root.startActivity(mainMenuIntent);
			quitFunc();*/
            loadCurrentFromName();
            return;
        }
        levelId++;
        //int lvlMax = StatStuff.packLevelCount[currentPackID];

        //if (levelId >= lvlMax) {
        //Toast.makeText(root,
        //		"Presets complete! Download or make more levels!",
        //		Toast.LENGTH_LONG).show();

			/*Intent mainMenuIntent = new Intent(root, AEMainMenu.class);
			mainMenuIntent.putExtra(
					"com.chozabu.android.BikeGame.gameComplete", true);
			root.startActivity(mainMenuIntent);*/
        //System.exit(0);
        //root.finish();
        //quitFunc();
        //	return;
        //}

        loadCurrentFromAsset();

    }

    void loadCurrentLevel() {
        if (levelFromFile) {
            loadCurrentFromName();
        } else {
            loadCurrentFromAsset();
        }
    }

    void loadCurrentFromAsset() {
        root.getEngine().runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                clearLvl();
                loadFromAsset(levelPrefix + levelId + ".lvl");

            }
        });
    }

    void loadCurrentFromName() {
        root.getEngine().runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                clearLvl();
                loadFromFile(levelStr);
                //Log.d("ABike", "SHOULD HAVE WORKED");

            }
        });
        //Log.d("ABike", "didc");
    }

    void restartLevel() {

        this.mScene.reset();

        Iterable<Body> bs = this.mPhysicsWorld.getBodies();
        for (Body b : bs) {
            b.setAwake(true);
            if (b.getUserData() == null)
                continue;

            final Shape shape = ((UserData) b.getUserData()).sprite;
            final Body body = b;

            final Vector2 position = new Vector2(shape.getX(), shape.getY());// body.getPosition();
            final float pixelToMeterRatio = 32f;
            position.x = (position.x + shape.getBaseWidth() * 0.5f)
                    / pixelToMeterRatio;
            position.y = (position.y + shape.getBaseHeight() * 0.5f)
                    / pixelToMeterRatio;
            // shape.setPosition(position.x * pixelToMeterRatio -
            // this.mShapeHalfBaseWidth, position.y * pixelToMeterRatio -
            // this.mShapeHalfBaseHeight);

            final float angle = MathUtils.degToRad(shape.getRotation());// body.getAngle();
            // shape.setRotation(MathUtils.radToDeg(angle));

            body.setTransform(position, angle);
            body.setAngularVelocity(0f);
            body.setLinearVelocity(new Vector2(0, 0));

            // this.mPhysicsWorld.destroyBody(b);
        }
        resetBerries();
        berryCount = strawBerryList.size();
        this.checkCanFinish();

        bike.attachWheels();
        // mScene.getTopLayer().getEntity(0).get
        /*
         * root.getEngine().runOnUpdateThread(new Runnable() {
         *
         * @Override public void run() {
         *
         * clearLvl(); if (levelFromFile) { loadFromFile(levelStr); } else {
         * loadFromAsset(levelStr); } } });
         */
    }

    public void checkCanFinish() {

        if (berryCount > 0) {
            allowFinish(false);
        } else {
            allowFinish(true);
        }

    }

    public void loadFromAsset(String assetName) {
        Reader inStream;
        try {
            inStream = new InputStreamReader(root.getAssets().open(assetName));
            loadFromReader(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        levelFromFile = false;
        levelStr = assetName;
    }

    public void loadFromFile(String assetName) {
        String newState = FileSystem.readFile(assetName);
        loadFromReader(new StringReader(newState));
        levelFromFile = true;
        levelStr = assetName;
    }

    LinkedList<BufferObject> bufferwaste = new LinkedList<BufferObject>();

    private void loadFromReader(final Reader inStream) {

        for (BufferObject pBufferObject : bufferwaste) {
            BufferObjectManager.getActiveInstance().unloadBufferObject(pBufferObject);
        }
        bufferwaste.clear();

        System.gc();
        // Debug.startMethodTracing("ABikeTrace");
        try {
            setXState(inStream);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Debug.stopMethodTracing();
        //mCamera.setChaseShape(bike.mBodyImg);
        Vector2 ep1 = new Vector2(0, 0);
        if (endList.size() > 0)
            ep1 = endList.get(0).getPosition();
        ep1.mul(32f);
        mCamera.setCenter(ep1.x, ep1.y);
        System.gc();
    }

    private void setXState(Reader inStream) throws XmlPullParserException,
            IOException {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xrp = factory.newPullParser();
        AttributeSet attributes = Xml.asAttributeSet(xrp);

        xrp.setInput(inStream);
        Texture currentTex = this.textures.defaultTexture;

        final float scale = 32f;
        final float myScale = 1.5f;
        final float flip = -1;

        final ITriangulationAlgoritm triangulationAlgoritm = new EarClippingTriangulator();

        endList = new LinkedList<Body>();
        strawBerryList = new LinkedList<Body>();
        wreckerList = new LinkedList<Body>();

        // reading buffer info
        boolean inName = false;
        Vector2 pos = new Vector2();
        String typeid = null;
        boolean hasPhysics = false;
        boolean inBackground = false;
        boolean isLayer = false;
        int layerid = -1;
        String paramName = null;
        float rSize = 1f;
        String blockName = null;
        String jointStart = null;
        String jointEnd = null;
        String jointType = null;
        List<Vector2> verticesList = null;// new LinkedList<Vector2>();
        verticesList = new LinkedList<Vector2>();
        List<Vector2> layersList = null;// new LinkedList<Vector2>();
        List<Boolean> layersFrontList = null;// new LinkedList<Vector2>();
        layersList = new LinkedList<Vector2>();
        layersFrontList = new LinkedList<Boolean>();
        Body endBody = null;
        Sprite end = null;
        Body strawBerryBody = null;
        Sprite strawBerry = null;
        Body wreckerBody = null;
        Sprite wrecker = null;
        float density = 0, elas = 0, friction = 0;
        float redTintBuff = 1f, greenTintBuff = 1f, blueTintBuff = 1f;

        JointList.reset();
        // read loop
        int event = xrp.getEventType();
        while (event != XmlResourceParser.END_DOCUMENT) {
            // ///////////////////////////////////
            // ///////////START TAG///////////////
            // ///////////////////////////////////
            if (event == XmlResourceParser.START_TAG) {
                String s = xrp.getName();
                if (s.equals("limits")) {
                    Vector2 min = new Vector2();
                    Vector2 max = new Vector2();
                    float left = attributes.getAttributeFloatValue(null,
                            "left", 0)
                            * myScale;
                    float right = attributes.getAttributeFloatValue(null,
                            "right", 0)
                            * myScale;
                    float top = attributes.getAttributeFloatValue(null, "top",
                            0)
                            * flip * myScale;
                    float bottom = attributes.getAttributeFloatValue(null,
                            "bottom", 0)
                            * flip * myScale;
                    min.x = Math.min(left, right);
                    min.y = Math.min(top, bottom);
                    max.x = Math.max(left, right);
                    max.y = Math.max(top, bottom);

                    this.initBorders(min, max);
                } else if (s.equals("layeroffset")) {
                    float x = attributes.getAttributeFloatValue(null,
                            "x", 0);
                    float y = attributes.getAttributeFloatValue(null,
                            "y", 0);
                    layersList.add(new Vector2(x, y));
                    //frontlayer
                    Boolean front = attributes.getAttributeBooleanValue(null,
                            "frontlayer", false);
                    layersFrontList.add(front);
                } else if (s.equals("layeroffsets")) {
                    //layersList = new LinkedList<Vector2>();
                    //layersFrontList = new LinkedList<Boolean>();
                } else if (s.equals("usetexture")) {
                    redTintBuff = attributes.getAttributeIntValue(null, "color_r", 256) / 256f;
                    greenTintBuff = attributes.getAttributeIntValue(null, "color_g", 256) / 256f;
                    blueTintBuff = attributes.getAttributeIntValue(null, "color_b", 256) / 256f;
                    String texName = attributes.getAttributeValue(null, "id");
                    if (texName.compareTo("Dirt") == 0) {
                        currentTex = this.textures.mEarthTex;
                    } else if (texName.compareTo("DarkDirt") == 0) {
                        currentTex = this.textures.mDarkEarthTex;
                    } else if (texName.compareTo("ice2") == 0) {
                        currentTex = this.textures.mIce2Tex;
                    } else if (texName.compareTo("Asphalt1") == 0) {
                        currentTex = this.textures.mSlabsTex;
                    } else if (texName.compareTo("windows1") == 0) {
                        currentTex = this.textures.mWindowsTex;
                    } else if (texName.compareTo("snow") == 0) {
                        currentTex = this.textures.mSnowTex;
                    } else if (texName.compareTo("Grass2") == 0) {
                        currentTex = this.textures.mGrass2Tex;
                    } else if (texName.compareTo("Grass1") == 0
                            || texName.compareTo("default") == 0) {
                        currentTex = this.textures.mGrassTex;
                    } else if (texName.compareTo("wood") == 0) {
                        currentTex = this.textures.mWoodTex;
                    } else if (texName.compareTo("Wood2") == 0) {
                        currentTex = this.textures.mWood2Tex;
                    } else if (texName.compareTo("Wood3") == 0) {
                        currentTex = this.textures.mWood3Tex;
                    } else if (texName.compareTo("Bricks") == 0) {
                        currentTex = this.textures.mBricksTex;
                    } else if (texName.compareTo("FrostGround") == 0) {
                        currentTex = this.textures.mFrostGroundTex;
                    } else if (texName.compareTo("DarkFrostGround") == 0) {
                        currentTex = this.textures.mDarkFrostGroundTex;
                    } else if (texName.compareTo("MoltenRock") == 0) {
                        currentTex = this.textures.mMoltenRockTex;
                    } else {
                        //Log.d(dbt,"missing texture: "+texName);
                        currentTex = this.textures.defaultTexture;
                    }

                } else if (s.equals("physics")) {
                    density = attributes.getAttributeFloatValue(null, "mass",
                            14f);
                    elas = attributes.getAttributeFloatValue(null,
                            "elasticity", 0f);
                    friction = attributes.getAttributeFloatValue(null,
                            "friction", 0.5f);
                    float fGrip = attributes.getAttributeFloatValue(null,
                            "grip", -1f);
                    if (fGrip != -1f)
                        friction = fGrip / 20f;
                } else if (s.equals("block")) {
                    verticesList = new LinkedList<Vector2>();
                    density = 14f;
                    elas = 0f;
                    friction = 0.5f;
                    blockName = attributes.getAttributeValue(null, "id");
                } else if (s.equals("size")) {
                    rSize = attributes.getAttributeFloatValue(null,
                            "r", 1f);
                } else if (s.equals("param")) {
                    String paramNameRead = attributes.getAttributeValue(null, "name");
                    if (paramNameRead.equals("name")) {
                        paramName = attributes.getAttributeValue(null, "value");
                    }
                } else if (s.equals("entity")) {
                    typeid = attributes.getAttributeValue(null, "typeid");
                } else if (s.equals("joint")) {
                    jointStart = attributes.getAttributeValue(null, "connection-start");
                    jointEnd = attributes.getAttributeValue(null, "connection-end");
                    jointType = attributes.getAttributeValue(null, "type");
                } else if (s.equals("position")) {
                    float x = attributes.getAttributeFloatValue(null, "x", 0)
                            * scale * myScale;
                    float y = attributes.getAttributeFloatValue(null, "y", 0)
                            * scale * flip * myScale;

                    hasPhysics = attributes.getAttributeBooleanValue(null,
                            "physics", false);
                    inBackground = attributes.getAttributeBooleanValue(null,
                            "background", false);
                    isLayer = attributes.getAttributeBooleanValue(null,
                            "islayer", false);
                    layerid = attributes.getAttributeIntValue(null, "layerid",
                            -1);
                    pos = new Vector2(x, y);
                } else if (s.equals("vertex")) {
                    float x = attributes.getAttributeFloatValue(null, "x", 0)
                            * myScale;
                    float y = attributes.getAttributeFloatValue(null, "y", 0)
                            * flip * myScale;
                    verticesList.add(new Vector2(x, y));
                } else if (s.equals("name")) {
                    inName = true;
                }
                // ///////////////////////////////////
                // ////////END TAG////////////////////
                // ///////////////////////////////////
            } else if (event == XmlResourceParser.END_TAG) {
                String s = xrp.getName();
                boolean skip = false;//(this.uglyMode) && (inBackground || isLayer);
                if (this.uglyMode) {
                    if (inBackground && !hasPhysics) skip = true;
                    if (isLayer) skip = true;
                }
                if (s.equals("block") && !skip) {

                    // Triangulate the points, making several smaller triangles
                    // of the complex shape
                    List<Vector2> triangles = null;//triangulationAlgoritm
                    //.computeTriangles(verticesList);

                    Iterator<Vector2> preVi = verticesList.iterator();

                    Vector2 avgPos = new Vector2(0, 0);
                    while (preVi.hasNext()) {
                        Vector2 current = preVi.next();
                        avgPos.add(current);
                    }
                    avgPos.mul(1.f / (float) verticesList.size());
                    avgPos.mul(32f);
                    Vector2 posDiff = avgPos.cpy().mul(1.f / 32f);
                    avgPos.add(pos);


                    if (verticesList.size() > 3) {
                        triangles = triangulationAlgoritm
                                .computeTriangles(verticesList);
                    } else if (verticesList.size() > 0) {
                        triangles = new LinkedList<Vector2>();
                        triangles.add(verticesList.get(1));
                        triangles.add(verticesList.get(0));
                        triangles.add(verticesList.get(2));
                    } else {
                        continue;
                    }
					/*Vector2 temp = triangles.get(0);
					triangles.set(0, triangles.get(1));
					triangles.set(1, temp);*/
                    // Log.d(dbt,"out block mid");
                    final float box2dspacefactor = 32;

                    float[] vray = new float[triangles.size() * 2];
                    Iterator<Vector2> vi = triangles.iterator();
                    int index = 0;
                    //Log.d("ABike","polystart at:"+pos.x+"\t :"+pos.y);
                    while (vi.hasNext()) {
                        Vector2 current = vi.next();
                        current.sub(posDiff);
                        vray[index * 2] = current.x * box2dspacefactor;
                        vray[index * 2 + 1] = current.y * box2dspacefactor;
                        //current.add(pos);
                        //Log.d("ABike","X="+current.x+"\t Y="+current.y);
                        index++;
                    }
                    //Log.d("ABike","polyend");

                    PolygonTextureRegion textureRegion = null;
                    if (!this.uglyMode)
                        textureRegion = TextureRegionFactory
                                .extractPolygonFromTexture(currentTex, 0, 0,
                                        128, 128, vray);

                    // Create Polygon using the triangulated vertex list
                    //final Polygon polygon = new Polygon(avgPos.x, avgPos.y, vray,
                    //		textureRegion);
                    Polygon polygon;
                    if (isLayer) {
                        if (layersList.size() <= layerid)
                            continue;
                        ParallaxPoly p = new ParallaxPoly(avgPos.x, avgPos.y,
                                vray,
                                textureRegion, mCamera);
                        float lx = layersList.get(layerid).x;
                        float ly = layersList.get(layerid).y;
                        p.setParallaxFactor(lx, ly);
                        polygon = p;
                    } else {
                        polygon = new Polygon(avgPos.x, avgPos.y, vray,
                                textureRegion);
                    }
                    polygon.setRGB(redTintBuff, greenTintBuff, blueTintBuff);
                    if (this.uglyMode)
                        polygon.setRGB((float) Math.random(), (float) Math.random(), (float) Math.random());
                    else
                        bufferwaste.add(textureRegion.mTextureRegionBuffer);
                    bufferwaste.add(polygon.getVertexBuffer());
                    polygon.setUpdatePhysics(false);

                    if (inBackground)
                        this.mScene.getLayer(1).addEntity(polygon);
                        //this.mScene.getBottomLayer().addEntity(polygon);
                    else if (isLayer)
                        if (layerid < layersFrontList.size() && layersFrontList.get(layerid))
                            this.mScene.getTopLayer().addEntity(polygon);
                        else
                            this.mScene.getBottomLayer().addEntity(polygon);
                        //	mParallaxBackground.addParallaxEntity(new ParallaxBackground2d.ParallaxBackground2dEntity(layersList.get(layerid).x, layersList.get(layerid).y, polygon,false,false,true));
                    else
                        this.mScene.getLayer(2).addEntity(polygon);
                    //Collide with player!
                    Body body = null;
                    if ((!inBackground || hasPhysics) && !isLayer) {
                        Rectangle polygonBoundingBox = new Rectangle(polygon
                                .getX() - 60, polygon.getY() - 60, 120, 120);
                        BodyType btype;
                        short cat;
                        short mask;
                        if (hasPhysics) {
                            btype = BodyType.DynamicBody;
                            cat = (inBackground) ? StatStuff.CATEGORYBIT_BG_DYNAMIC : StatStuff.CATEGORYBIT_FG_DYNAMIC;
                            mask = (inBackground) ? StatStuff.MASKBITS_BG_DYNAMIC : StatStuff.MASKBITS_FG_DYNAMIC;
                        } else {
                            btype = BodyType.StaticBody;
                            density = 1000000f;
                            cat = (inBackground) ? StatStuff.CATEGORYBIT_BG_STATIC : StatStuff.CATEGORYBIT_FG_STATIC;
                            mask = (inBackground) ? StatStuff.MASKBITS_BG_STATIC : StatStuff.MASKBITS_FG_STATIC;
                        }


                        FixtureDef FD = PhysicsFactory.createFixtureDef(
                                density, elas, friction, false, cat, mask, (short) 0);
                        body = PhysicsFactory
                                .createTrianglulatedBody(this.mPhysicsWorld,
                                        polygonBoundingBox, triangles, btype,
                                        FD);
                        //body.setTransform(pos, 0);
                        // MassData data = body.getMassData();
                        // data.mass = density;
                        // body.setMassData(data);
                        //polygon.
                        body.setUserData(new UserData(polygon, blockName));
                        //bodyList.add(body);
                        this.mPhysicsWorld
                                .registerPhysicsConnector(new PhysicsConnector(
                                        polygon, body, true, true, false, false));
                    }

                    //JointList.passBody(blockName, body);
                    // Log.d(dbt,"out block end");

                } else if (s.equals("entity")) {// Strawberry
                    if (typeid.compareTo("PlayerStart") == 0) {
                        playerStart = pos.cpy();
                        this.bike = new Bike(this, playerStart);
                    } else if (typeid.compareTo("Sprite") == 0) {
                        if (paramName.equals("Tree1")) {
                            float width = 512 * rSize;
                            float height = 512 * rSize;
                            Sprite newTree = new Sprite(pos.x - width / 2, pos.y - height * 0.98f,
                                    width, height, this.textures.mTreeTextureRegion);

                            newTree.setUpdatePhysics(false);
                            bufferwaste.add(newTree.getVertexBuffer());

                            this.mScene.getTopLayer().addEntity(newTree);
                        }
                    } else if (typeid.compareTo("Wrecker") == 0) {
                        final FixtureDef endSensorDef = PhysicsFactory
                                .createFixtureDef(1, 0.1f, 0.98f, true);
                        wrecker = new Sprite(pos.x - 16, pos.y - 16, 32, 32,
                                this.textures.mWreckerTextureRegion);
                        wreckerBody = PhysicsFactory.createCircleBody(
                                this.mPhysicsWorld, wrecker,
                                BodyType.StaticBody, endSensorDef);
                        //wreckerBody.setUserData(wrecker);
                        wreckerList.add(wreckerBody);
                        wrecker.setUpdatePhysics(false);
                        bufferwaste.add(wrecker.getVertexBuffer());

                        this.mScene.getTopLayer().addEntity(wrecker);

                    } else if (typeid.compareTo("Strawberry") == 0) {
                        final FixtureDef endSensorDef = PhysicsFactory
                                .createFixtureDef(1, 0.1f, 0.98f, true);
                        strawBerry = new Sprite(pos.x - 32, pos.y - 32,
                                this.textures.mStrawBerryTextureRegion);
                        strawBerryBody = PhysicsFactory.createBoxBody(
                                this.mPhysicsWorld, strawBerry,
                                BodyType.StaticBody, endSensorDef);
                        strawBerryBody.setUserData(new UserData(strawBerry, null));
                        strawBerryList.add(strawBerryBody);
                        strawBerry.setUpdatePhysics(false);
                        bufferwaste.add(strawBerry.getVertexBuffer());

                        this.mScene.getTopLayer().addEntity(strawBerry);

                    } else if (typeid.compareTo("EndOfLevel") == 0) {
                        final FixtureDef endSensorDef = PhysicsFactory
                                .createFixtureDef(1, 0.1f, 0.98f, true);
                        end = new Sprite(pos.x - 32, pos.y - 32,
                                this.textures.mFinishTextureRegion);
                        endBody = PhysicsFactory.createBoxBody(
                                this.mPhysicsWorld, end, BodyType.StaticBody,
                                endSensorDef);
                        endList.add(endBody);
                        endBody.setUserData(new UserData(end, null));
                        end.setUpdatePhysics(false);

                        this.mScene.getTopLayer().addEntity(end);
                        bufferwaste.add(end.getVertexBuffer());

                    } else if (typeid.compareTo("Joint") == 0) {
                        JointList.add(pos.mul(1f / 32f), jointStart, jointEnd, jointType);
                    }
                }

            } else if (event == XmlResourceParser.TEXT) {
                if (inName) {
                    inName = false;
                    this.mName = xrp.getText();
                    // Log.d(dbt,"\n\n\n\n\n\n name is: "+this.mName);
                } else {
                    // Log.d(dbt,"\n text is is: "+xrp.getText());
                }
            }
            event = xrp.next();
        }
        JointList.makeJoints(this);
        berryCount = strawBerryList.size();
        this.checkCanFinish();


        Iterable<Body> bs = this.mPhysicsWorld.getBodies();
        for (Body b : bs) {
            b.setAwake(true);
        }
        //Log.d(dbt, "\n\n Level Load Complete of: " + this.mName);
        //Log.d(dbt, "ID: " + this.levelId);
    }

    private void initBorders(Vector2 min, Vector2 max) {
        min.mul(32f);
        max.mul(32f);

        float thick = 320f;
        //addQuad(min,new Vector2(min.x,max.y),new Vector2(min.x,max.y+thick),new Vector2(min.x-thick,min.y-thick));

        if (uglyMode) {
            addLine(min.x, min.y, max.x, min.y);// t
            addLine(min.x, max.y, max.x, max.y);// b
            addLine(min.x, min.y, min.x, max.y);// l
            addLine(max.x, min.y, max.x, max.y);// r


        } else {

            // float ht = thick / 2f;
            float width = max.x - min.x;
            float height = max.y - min.y;
            addBox(min.x - thick, min.y - thick, width + thick * 2, thick);// top
            addBox(min.x - thick, max.y, width + thick * 2, thick);// bottom

            addBox(min.x - thick, min.y, thick, height);// left
            addBox(max.x, min.y, thick, height);// right

        }

    }

    private void addBox(final float pX, final float pY, float width,
                        float height) {
        final Scene scene = this.mScene;

        final Sprite face;
        face = new Sprite(pX, pY, width, height,
                this.textures.mEarthTextureRegion);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, face,
                BodyType.StaticBody, PhysicsFactory.createFixtureDef(1, 0.1f,
                        0.98f));
        face.setUpdatePhysics(false);

        bufferwaste.add(face.getVertexBuffer());
        scene.getTopLayer().addEntity(face);
    }

    private void addLine(float x1, float y1, float x2, float y2) {
        final Scene scene = this.mScene;
        final Line line = new Line(x1, y1, x2, y2, 5);

        line.setColor(1.0f, 1.0f, 1.0f);
        PhysicsFactory.createLineBody(this.mPhysicsWorld, line, PhysicsFactory
                .createFixtureDef(1, 0.1f, 0.98f));
        bufferwaste.add(line.getVertexBuffer());
        scene.getTopLayer().addEntity(line);
    }
	/*
	private void addQuad(Vector2 a,Vector2 b,Vector2 c,Vector2 d){
		float[] vray = new float[8];
		vray[0]=a.x;
		vray[1]=a.y;
		vray[2]=b.x;
		vray[3]=b.y;
		vray[4]=c.x;
		vray[5]=c.y;
		vray[6]=d.x;
		vray[7]=d.y;

		PolygonTextureRegion textureRegion = null;
		if (!this.uglyMode)
			textureRegion = TextureRegionFactory
					.extractPolygonFromTexture(this.textures.mEarthTex, 0, 0,
							128, 128, vray);
		Polygon polygon = new Polygon(0, 0, vray,
				textureRegion);
		mScene.getTopLayer().addEntity(polygon);
		
	}*/

    public Scene getScene() {
        return mScene;
    }

    private void allowFinish(boolean in) {
        float alpha = (in) ? 1f : 0.35f;
        Iterator<Body> vi = endList.iterator();
        while (vi.hasNext()) {
            Body current = vi.next();
            (((UserData) current.getUserData()).sprite).setAlpha(alpha);//setVisible(in);
        }

    }

    public void resetBerries() {
        Iterator<Body> xi = strawBerryList.iterator();
        while (xi.hasNext()) {
            Body current = xi.next();
            ((UserData) current.getUserData()).sprite.setVisible(true);
        }
    }

    public void setLevelPack(int currentPackIn) {
        currentPackID = currentPackIn;
        levelPrefix = StatStuff.packPrefix[currentPackID];
        if (currentPackID == StatStuff.originalPackID || currentPackID == StatStuff.xmClassicPackID) {
            Vector2 grav = new Vector2(0, 6.0f);
            String cheatsString = prefs.getString("cheatsString", "");
            if (cheatsString.contains("Moon")) {
                grav = new Vector2(0, 4.0f);
            } else if (cheatsString.contains("Jupiter")) {
                grav = new Vector2(0, 9.0f);
            }
            mPhysicsWorld.setGravity(grav);
        }

    }


}
