package com.chozabu.android.BikeGame;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class LoadingJoint {
    Vector2 pos;
    String jointStart;
    Body start = null;
    String jointEnd;
    Body end = null;
    String jointType;

    //boolean foundStart = false;
    //boolean foundEnd = false;
    LoadingJoint(Vector2 posIn, String jointStartIn, String jointEndIn, String jointTypeIn) {
        pos = posIn;
        jointStart = jointStartIn;
        jointEnd = jointEndIn;
        jointType = jointTypeIn;
        //Log.d("ABike","got a joint");
    }

    public void pair(String blockName, Body bod) {
        if (blockName.compareTo(jointStart) == 0) {
            start = bod;
            //Log.d("ABike","got start:"+blockName);
            return;
        } else if (blockName.compareTo(jointEnd) == 0) {
            end = bod;
            //Log.d("ABike","got end:"+blockName);
            return;
        }

    }

    public void makeJoint(GameWorld gameWorld) {

        if (start == null || end == null) {
            //Log.d("ABike","failed to make joint - needs 2 bodies!");
            return;
        }
        //DistanceJointDef djd = new DistanceJointDef();
        //WeldJointDef wjd = new WeldJointDef();
        //wjd.initialize(start, end, start.getPosition());
        //djd.initialize(start, end, start.getPosition(), end.getPosition());
        //djd.frequencyHz=30.0f;
        //djd.dampingRatio=0.001f;
        //djd.
        //gameWorld.mPhysicsWorld.createJoint(wjd);

        if (jointType.equals("pivot")) {

            RevoluteJointDef jd = new RevoluteJointDef();
            jd.initialize(start, end, pos);
            //Log.d("ABike","made a piviot joint");
            gameWorld.mPhysicsWorld.createJoint(jd);
        } else if (jointType.equals("pin")) {

            DistanceJointDef djd = new DistanceJointDef();
            djd.initialize(start, end, start.getPosition(), end.getPosition());
            //Log.d("ABike","made a pin joint");
            gameWorld.mPhysicsWorld.createJoint(djd);
        } else if (jointType.equals("pin2point")) {

            DistanceJointDef djd = new DistanceJointDef();
            djd.initialize(start, end, start.getPosition(), pos);
            //Log.d("ABike","made a pin2point joint");
            gameWorld.mPhysicsWorld.createJoint(djd);
        }


    }

}
