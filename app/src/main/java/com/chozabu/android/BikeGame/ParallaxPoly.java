package com.chozabu.android.BikeGame;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.primitive.Polygon;
import org.anddev.andengine.opengl.texture.region.PolygonTextureRegion;

import javax.microedition.khronos.opengles.GL10;

public class ParallaxPoly extends Polygon {

    float parallaxX = 1f;
    float parallaxY = 1f;

    float translateX = 0f;
    float translateY = 0f;

    Camera camera;

    public ParallaxPoly(float pX, float pY, float[] pVertices,
                        PolygonTextureRegion pTextureRegion, Camera cam) {
        super(pX, pY, pVertices, pTextureRegion);
        camera = cam;
    }

    public ParallaxPoly(float pX, float pY, float parX, float parY, float[] pVertices,
                        PolygonTextureRegion pTextureRegion, Camera cam) {
        super(pX, pY, pVertices, pTextureRegion);
        setParallaxFactor(parX, parY);
        camera = cam;
    }

    public void setParallaxFactor(float parX, float parY) {
        parallaxX = parX;
        parallaxY = parY;
    }

    float offsetX = 0;
    float offsetY = 0;

    public void setOffsetCentre(float pX, float pY) {
        offsetX = mX - pX;
        offsetY = mY - pY;
    }

    private void calcPos() {
        float cx = camera.getCenterX();
        float cy = camera.getCenterY();
        float xd = cx - mX;
        float yd = cy - mY;
        //+offsetX*(xd*parallaxX)
        translateX = mX + xd * (1f - parallaxX);
        translateY = mY + yd * (1f - parallaxY);
    }

    @Override
    protected void applyTranslation(final GL10 pGL) {
        calcPos();
        pGL.glTranslatef(translateX, translateY, 0);
        //pGL.glTranslatef(this.mX, this.mY, 0);
    }

}
