package com.chozabu.android.BikeGame;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import javax.microedition.khronos.opengles.GL10;

public class ParallaxSprite extends Sprite {

    float parallaxX = 1f;
    float parallaxY = 1f;

    public ParallaxSprite(float pX, float pY, float pWidth, float pHeight,
                          TextureRegion pTextureRegion) {
        super(pX, pY, pWidth, pHeight, pTextureRegion);
    }

    public ParallaxSprite(float pX, float pY, float pWidth, float pHeight,
                          float parX, float parY, TextureRegion pTextureRegion) {
        super(pX, pY, pWidth, pHeight, pTextureRegion);
        setParallaxFactor(parX, parY);
    }

    public void setParallaxFactor(float parX, float parY) {
        parallaxX = parX;
        parallaxY = parY;
    }

    @Override
    protected void applyTranslation(final GL10 pGL) {
        pGL.glTranslatef(this.mX * parallaxX, this.mY * parallaxY, 0);
    }

}
