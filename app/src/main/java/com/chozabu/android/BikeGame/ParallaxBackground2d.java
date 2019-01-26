package com.chozabu.android.BikeGame;

//package com.dustypixels.andengine.entity.scene.background;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author Oldskool73
 *
 * <span class="posthilit">Parallax</span> background that scrolls in both X and/or Y directions.
 * <p>
 * Usage:
 * <p>
 * ...x & y free scrolling tiled background...
 * mParallaxBackground.addParallaxEntity(new ParallaxBackground2d.ParallaxBackground2dEntity(-0.2f,-0.2f, new Sprite(0, 0, this.mParallaxLayerStars)));
 * <p>
 * ...side scroller repeating strip...
 * mParallaxBackground.addParallaxEntity(new ParallaxBackground2d.ParallaxBackground2dEntity(-0.4f, 0.0f, new Sprite(0, 100, this.mParallaxLayerHills),true,false));
 * <p>
 * ...vertical scroller repeating strip...
 * mParallaxBackground.addParallaxEntity(new ParallaxBackground2d.ParallaxBackground2dEntity(-0.0f,-0.4f, new Sprite(100, 0, this.mParallaxLayerHills),false,true));
 * <p>
 * ...non repeating positioned item...
 * mParallaxBackground.addParallaxEntity(new ParallaxBackground2d.ParallaxBackground2dEntity(-0.4f,-0.4f, new Sprite(100, 100, this.mParallaxLayerSun),false,false,true));
 */
public class ParallaxBackground2d extends ColorBackground {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private final ArrayList<ParallaxBackground2dEntity> mParallaxEntities = new ArrayList<ParallaxBackground2dEntity>();
    private int mParallaxEntityCount;

    protected float mParallaxValueX;
    protected float mParallaxValueY;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ParallaxBackground2d(float pRed, float pGreen, float pBlue) {
        super(pRed, pGreen, pBlue);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public void setParallaxValue(final float pParallaxValueX, final float pParallaxValueY) {
        this.mParallaxValueX = pParallaxValueX;
        this.mParallaxValueY = pParallaxValueY;
    }

    public void offsetParallaxValue(final float pParallaxValueX, final float pParallaxValueY) {
        this.mParallaxValueX += pParallaxValueX;
        this.mParallaxValueY += pParallaxValueY;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void onDraw(final GL10 pGL, final Camera pCamera) {
        super.onDraw(pGL, pCamera);

        final float parallaxValueX = this.mParallaxValueX;
        final float parallaxValueY = this.mParallaxValueY;
        final ArrayList<ParallaxBackground2dEntity> parallaxEntities = this.mParallaxEntities;

        for (int i = 0; i < this.mParallaxEntityCount; i++) {
            parallaxEntities.get(i).onDraw(pGL, parallaxValueX, parallaxValueY, pCamera);
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public void addParallaxEntity(final ParallaxBackground2dEntity pParallaxEntity) {
        this.mParallaxEntities.add(pParallaxEntity);
        this.mParallaxEntityCount++;
    }

    public boolean removeParallaxEntity(final ParallaxBackground2dEntity pParallaxEntity) {
        final boolean success = this.mParallaxEntities.remove(pParallaxEntity);
        if (success == true) {
            this.mParallaxEntityCount--;
        }
        return success;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public static class ParallaxBackground2dEntity {
        // ===========================================================
        // Constants
        // ===========================================================

        // ===========================================================
        // Fields
        // ===========================================================

        final float mParallaxFactorX;
        final float mParallaxFactorY;
        final Boolean mRepeatX;
        final Boolean mRepeatY;
        final Shape mShape;
        final Boolean mShouldCull;

        // ===========================================================
        // Constructors
        // ===========================================================

        // add a repeating x & y texture fill
        public ParallaxBackground2dEntity(final float pParallaxFactorX, final float pParallaxFactorY, final Shape pShape) {
            this.mParallaxFactorX = pParallaxFactorX;
            this.mParallaxFactorY = pParallaxFactorY;
            this.mRepeatX = true;
            this.mRepeatY = true;
            this.mShouldCull = false;
            this.mShape = pShape;
        }

        // add an x or y only repeating strip
        public ParallaxBackground2dEntity(final float pParallaxFactorX, final float pParallaxFactorY, final Shape pShape, final Boolean pRepeatX, final Boolean pRepeatY) {
            this.mParallaxFactorX = pParallaxFactorX;
            this.mParallaxFactorY = pParallaxFactorY;
            this.mRepeatX = pRepeatX;
            this.mRepeatY = pRepeatY;
            this.mShouldCull = false;
            this.mShape = pShape;
        }

        // add an x or y only repeating strip or non repeating feature that may be culled when off screen
        public ParallaxBackground2dEntity(final float pParallaxFactorX, final float pParallaxFactorY, final Shape pShape, final Boolean pRepeatX, final Boolean pRepeatY, final Boolean pShouldCull) {
            this.mParallaxFactorX = pParallaxFactorX;
            this.mParallaxFactorY = pParallaxFactorY;
            this.mRepeatX = pRepeatX;
            this.mRepeatY = pRepeatY;
            this.mShouldCull = (pRepeatX && pRepeatY) ? false : pShouldCull;
            this.mShape = pShape;
        }


        // ===========================================================
        // Getter & Setter
        // ===========================================================

        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================

        // ===========================================================
        // Methods
        // ===========================================================

        public void onDraw(final GL10 pGL, final float pParallaxValueX, final float pParallaxValueY, final Camera pCamera) {
            pGL.glPushMatrix();
            {
                final float cameraWidth = pCamera.getWidth();
                final float cameraHeight = pCamera.getHeight();
                final float shapeWidthScaled = this.mShape.getWidthScaled();
                final float shapeHeightScaled = this.mShape.getHeightScaled();

                //reposition
                float baseOffsetX = (pParallaxValueX * this.mParallaxFactorX);
                if (this.mRepeatX) {
                    baseOffsetX = baseOffsetX % shapeWidthScaled;
                    while (baseOffsetX > 0) {
                        baseOffsetX -= shapeWidthScaled;
                    }
                }
                float baseOffsetY = (pParallaxValueY * this.mParallaxFactorY);
                if (this.mRepeatY) {
                    baseOffsetY = baseOffsetY % shapeHeightScaled;
                    while (baseOffsetY > 0) {
                        baseOffsetY -= shapeHeightScaled;
                    }
                }

                //optionally screen cull non repeating items
                Boolean culled = false;
                if (mShouldCull) {
                    if (!this.mRepeatX) {
                        if ((baseOffsetY + (shapeHeightScaled * 2) < 0) || (baseOffsetY > cameraHeight)) {
                            culled = true;
                        }
                    }
                    if (!this.mRepeatY) {
                        if ((baseOffsetX + (shapeWidthScaled * 2) < 0) || (baseOffsetX > cameraWidth)) {
                            culled = true;
                        }
                    }
                }

                if (!culled) {
                    //draw
                    pGL.glTranslatef(baseOffsetX, baseOffsetY, 0);
                    float currentMaxX = baseOffsetX;
                    float currentMaxY = baseOffsetY;
                    do
                    {                                                                                                            //rows
                        this.mShape.onDraw(pGL, pCamera);
                        if (this.mRepeatY) {
                            currentMaxY = baseOffsetY;
                            do
                            {                                                                                            //columns
                                pGL.glTranslatef(0, shapeHeightScaled, 0);
                                currentMaxY += shapeHeightScaled;
                                this.mShape.onDraw(pGL, pCamera);
                            }
                            while (currentMaxY < cameraHeight);                            //end columns
                            pGL.glTranslatef(0, -currentMaxY + baseOffsetY, 0);
                        }
                        pGL.glTranslatef(shapeWidthScaled, 0, 0);
                        currentMaxX += shapeWidthScaled;
                    } while (this.mRepeatX && currentMaxX < cameraWidth);           //end rows
                }
            }
            pGL.glPopMatrix();
        }

        // ===========================================================
        // Inner and Anonymous Classes
        // ===========================================================
    }
}