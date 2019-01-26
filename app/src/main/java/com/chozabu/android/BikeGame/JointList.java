package com.chozabu.android.BikeGame;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class JointList {

    static List<LoadingJoint> jointDefs = new LinkedList<LoadingJoint>();

    //static List<LoadingJoint> bodyDefs = new LinkedList<LoadingJoint>();
    //static List<LoadingJoint> bodyNames = new LinkedList<LoadingJoint>();

    static void add(Vector2 pos, String jointStart, String jointEnd, String jointType) {
        LoadingJoint lj = new LoadingJoint(pos.cpy(), jointStart, jointEnd, jointType);
        jointDefs.add(lj);
    }

    public static void passBody(String blockName, Body bod) {
        Iterator<LoadingJoint> vi = jointDefs.iterator();
        while (vi.hasNext()) {
            LoadingJoint current = vi.next();
            //if(current.contains(blockName)){
            current.pair(blockName, bod);
            //}
        }

    }

    public static void reset() {
        jointDefs.clear();
    }

    public static void makeJoints(GameWorld gameWorld) {
        gameWorld.mPhysicsWorld.getBodies();

        Iterable<Body> bs = gameWorld.mPhysicsWorld.getBodies();
        for (Body b : bs) {
            UserData ud = (UserData) b.getUserData();
            if (ud == null)
                continue;
            if (ud.name == null)
                continue;
            passBody(ud.name, b);
            //final Shape shape = ((UserData) b.getUserData()).sprite;
            //final Body body = b;
        }

        Iterator<LoadingJoint> vi = jointDefs.iterator();
        while (vi.hasNext()) {
            LoadingJoint current = vi.next();
            //if(current.contains(blockName)){
            current.makeJoint(gameWorld);
            //}
        }
    }
}
