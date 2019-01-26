package com.chozabu.android.BikeGame;

import org.anddev.andengine.entity.shape.GLShape;

public class UserData {
    public GLShape sprite = null;
    public String name = null;

    UserData(GLShape pSprite, String pName) {
        sprite = pSprite;
        name = pName;
    }
}
