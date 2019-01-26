package com.chozabu.android.BikeGame;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.KeyEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;

import org.anddev.andengine.engine.camera.ZoomCamera;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.animator.AlphaMenuAnimator;
import org.anddev.andengine.entity.scene.menu.animator.IMenuAnimator;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.opengl.buffer.BufferObject;
import org.anddev.andengine.opengl.buffer.BufferObjectManager;

import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

//import com.chozabu.android.BikeGame.R;
//import com.openfeint.api.OpenFeint;
//import com.openfeint.api.ui.Dashboard;
//import android.widget.Toast;

public class AEMainMenu implements GameScene,
        IOnMenuItemClickListener {

    protected static final int MENU_START = 0;
    protected static final int MENU_GO_ROOT = MENU_START + 1;
    protected static final int MENU_LOAD = MENU_GO_ROOT + 1;//
    protected static final int MENU_HELP = MENU_LOAD + 1;
    protected static final int MENU_FEINT = MENU_HELP + 1;
    protected static final int MENU_OPTIONS = MENU_FEINT + 1;
    protected static final int MENU_CREDITS = MENU_OPTIONS + 1;
    protected static final int MENU_QUIT = MENU_CREDITS + 1;
    protected static final int MENU_BUY_GAME = MENU_QUIT + 1;
    protected static final int MENU_MORE_LEVELS = MENU_BUY_GAME + 1;
    protected static final int MENU_OLD_PACKS = MENU_MORE_LEVELS + 1;
    protected static final int MENU_ORIGNAL_PACK = MENU_OLD_PACKS + 1;
    protected static final int MENU_JAN_PACK = MENU_ORIGNAL_PACK + 1;
    protected static final int MENU_BONUS_PACK = MENU_JAN_PACK + 1;
    protected static final int MENU_TWHEEL_PACK = MENU_BONUS_PACK + 1;
    protected static final int MENU_XCLASSIC_PACK = MENU_TWHEEL_PACK + 1;

    protected static final int MENU_LEVELS = MENU_XCLASSIC_PACK + 1;

    MainActivity root;

    ZoomCamera camera;
    Textures textures;// = new Textures();
    Sounds sounds;// = new Sounds();
    private MenuScene mainMenu;
    private MenuScene levelPackMenu;
    private MenuScene oldLevelPackMenu;
    private MenuScene lockedClassicMenu;
    private MenuScene completedMenu;

    GameWorld gameWorld;// = new GameWorld();
    private Scene mScene;
    int levelsFrom = 1;

    //SharedPreferences prefs;
    // private MenuScene levelsMenu;

    //String currentPack = "orignal";
    int currentPackID = 0;
    private Builder instructionsDialog;

    private boolean loadFinished = false;

    private LinkedList<BufferObject> bufferWaste = new LinkedList<BufferObject>();

    AEMainMenu(MainActivity context) {
        root = context;
    }

    String getPackName(int ID) {
        return StatStuff.packNames[ID];
    }

    private void clearBuffer() {
        this.root.getEngine().runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                for (BufferObject pBufferObject : bufferWaste) {
                    BufferObjectManager.getActiveInstance().unloadBufferObject(pBufferObject);
                }
                bufferWaste.clear();
            }
        });
    }

    public void doIntroDialog() {
        //if(true)return;
        int playCount = root.prefs.getInt("playCount", 0);
        boolean seenInfo = root.prefs.getBoolean("seenInfo", false);

        Editor edit = root.prefs.edit();
        if (playCount == 20 && !seenInfo) {
            makeInfo();
            this.instructionsDialog.show();
            edit.putBoolean("seenInfo", true);
        } else if (root.prefs.getBoolean("showIntro", true)) {

            makeIntro();

            this.instructionsDialog.show();
        }
        edit.commit();
        // your code
    }


    //@Override
    public Scene onLoadScene() {
        textures = root.textures;
        camera = root.camera;
        gameWorld = root.gameWorld;
        sounds = root.sounds;

        this.mainMenu = this.createMenuScene();
        this.levelPackMenu = this.createLevelPackMenuScene();
        this.oldLevelPackMenu = this.createOldLevelPackMenuScene();
        this.lockedClassicMenu = this.createLockedClassicMenuScene();
        this.completedMenu = this.createPackCompleteMenuScene();
        // this.levelsMenu = this.createLevelMenuScene();

        this.mScene = new Scene(4);

        gameWorld.initScene(this.mScene);

        this.mScene.setChildScene(mainMenu);
        return this.mScene;
    }

    public void onLoadComplete() {
        gameWorld.initLoaded();


        //boolean completed = false;
        if (StatStuff.isWinner) {
            gameWorld.loadFromAsset("level/ending.lvl");
            this.mScene.setChildScene(this.completedMenu);
        } else {
            if (Math.random() > 0.1)
                gameWorld.loadFromAsset("level/intro.lvl");
            else
                gameWorld.loadFromAsset("level/intro2.lvl");
        }
        //gameWorld.loadFromAsset("level/intro.lvl");

        gameWorld.unPause();

        gameWorld.bike.setSpeed(.3f + (float) Math.random() * .7f);

        gameWorld.mPhysicsWorld.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {

                boolean reset = false;
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();


                if (gameWorld.bike.containsBody(bodyA)
                        || gameWorld.bike.containsBody(bodyB)) {

                    gameWorld.bike.beginContact(contact);
                    if (gameWorld.endList.contains(bodyA)
                            || gameWorld.endList.contains(bodyB))
                        gameWorld.bike.mBody
                                .applyAngularImpulse((float) (-50f + Math.random() * 100f));
                    return;
                }

                // truck roof hit!
                if (bodyA == gameWorld.bike.roofSensor
                        || bodyB == gameWorld.bike.roofSensor) {
                    if (Math.random() > 0.95)
                        gameWorld.bike.flipDirecion();

                    gameWorld.bike.setSpeed(.4f + (float) Math.random() * .6f);
                    float angle = gameWorld.bike.mBody.getAngle();
                    Vector2 v = new Vector2((float) Math.sin(angle) * -150f, (float) Math.cos(angle) * 150f);
                    gameWorld.bike.mBody.applyLinearImpulse(v, gameWorld.bike.mBody.getPosition());
                    gameWorld.bike.mBody
                            .applyAngularImpulse((float) (-100f + Math.random() * 200f));
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

        });

        this.root.getEngine().runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                sounds.start();
            }
        });

        //backward compatability
        int atLevelnew = root.prefs.getInt("atLevel" + getPackName(currentPackID), 2);
        int atLevel = root.prefs.getInt("atLevel", 2);
        int atLevelx = root.prefs.getInt("atLevelorignal", 2);
        atLevel = Math.max(atLevel, atLevelx);

        if (atLevelnew < atLevel) {
            Editor edit = root.prefs.edit();
            edit.putInt("atLevel" + getPackName(currentPackID), atLevel);
            edit.commit();
        }

        loadFinished = true;

    }

    //@Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (!loadFinished) return false;
        if (this.mainMenu == null) return true;
        if (pEvent.getAction() != KeyEvent.ACTION_DOWN)
            return false;
        if (pKeyCode == KeyEvent.KEYCODE_BACK) {
            if (this.mScene.getChildScene() != this.mainMenu) {

                this.mScene.setChildScene(mainMenu);
                return true;
            } else {
                //return true;
                root.superQuitFunc();
            }
        }

        return false;
    }

    //@Override
    public boolean onKeyUp(final int pKeyCode, final KeyEvent pEvent) {
        if (!loadFinished) return false;
        if (this.mainMenu == null) return true;
        if (this.mScene == null)
            return false;
        if (pKeyCode == KeyEvent.KEYCODE_BACK) {
            if (this.mScene.getChildScene() != this.mainMenu) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene,
                                     final IMenuItem pMenuItem, final float pMenuItemLocalX,
                                     final float pMenuItemLocalY) {
        this.sounds.mBeBoopSound.play();
        int itemId = pMenuItem.getID();
        switch (itemId) {
            case MENU_START:
                // levelsFrom = 1;
                this.mScene.setChildScene(this.levelPackMenu);
                return true;
            case MENU_OLD_PACKS:
                this.root.getEngine().runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {
                        mScene.setChildScene(oldLevelPackMenu);
                    }
                });
                return true;
            case MENU_JAN_PACK:
                levelsFrom = 1;
                currentPackID = StatStuff.janPackID;
                this.root.getEngine().runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {
                        AEMainMenu.this.mScene.setChildScene(createLevelMenuScene(5));
                    }
                });
                return true;
            case MENU_ORIGNAL_PACK:
                levelsFrom = 1;
                currentPackID = StatStuff.originalPackID;
                this.root.getEngine().runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {
                        AEMainMenu.this.mScene.setChildScene(createLevelMenuScene(5));
                    }
                });
                return true;
            case MENU_XCLASSIC_PACK:
                levelsFrom = 1;
                currentPackID = StatStuff.xmClassicPackID;
                this.root.getEngine().runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {
                        AEMainMenu.this.mScene.setChildScene(createLevelMenuScene(5));
                    }
                });
                return true;
            case MENU_BONUS_PACK:
                if (StatStuff.isDemo) {

                    this.root.getEngine().runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            AEMainMenu.this.mScene.setChildScene(AEMainMenu.this.lockedClassicMenu);
                        }
                    });
                    return true;
                }
                levelsFrom = 1;
                currentPackID = StatStuff.bonusPackID;
                this.root.getEngine().runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {
                        AEMainMenu.this.mScene.setChildScene(createLevelMenuScene(5));
                    }
                });
                return true;
            case MENU_TWHEEL_PACK:
                if (StatStuff.isDemo) {

                    this.root.getEngine().runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            AEMainMenu.this.mScene.setChildScene(AEMainMenu.this.lockedClassicMenu);
                        }
                    });
                    return true;
                }
                levelsFrom = 1;
                currentPackID = StatStuff.tWheelPackID;
                this.root.getEngine().runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {
                        AEMainMenu.this.mScene.setChildScene(createLevelMenuScene(5));
                    }
                });
                return true;
            case MENU_LOAD:
                Intent LoadGameIntent = new Intent(AEMainMenu.this.root, LoadList.class);
                root.startActivity(LoadGameIntent);
                root.quitFunc();
                return true;

            case MENU_HELP:
                Intent GameHelpIntent = new Intent(AEMainMenu.this.root, Help.class);
                this.root.startActivity(GameHelpIntent);
                this.root.quitFunc();
                return true;

            case MENU_FEINT:
                try {
                    //Dashboard.openLeaderboards();//TODO replace OF code
                } catch (Exception e) {

                }
                return true;

            case MENU_BUY_GAME:
                StatStuff.marketFull(this.root);
                this.root.quitFunc();
                return true;
            case MENU_GO_ROOT:
                this.mScene.setChildScene(mainMenu);
                return true;

            case MENU_OPTIONS:
                Intent GameOptionsIntent = new Intent(AEMainMenu.this.root,
                        GameOptions.class);
                this.root.startActivity(GameOptionsIntent);
                this.root.quitFunc();
                return true;
            case MENU_CREDITS:
                Intent CreditsIntent = new Intent(AEMainMenu.this.root, Credits.class);
                this.root.startActivity(CreditsIntent);
                this.root.quitFunc();
                return true;
            case MENU_MORE_LEVELS:
                levelsFrom += 5;

                this.root.getEngine().runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {
                        AEMainMenu.this.mScene.setChildScene(createLevelMenuScene(5));
                    }
                });
                return true;
            case MENU_QUIT:
                this.root.superQuitFunc();
                return true;
        }

        if (itemId > MENU_LEVELS) {
            clearBuffer();
            root.setInGame(currentPackID, itemId - MENU_LEVELS);

            return true;
        }

        return true;
    }

    protected MenuScene createMenuScene() {
        final MenuScene menuScene = new MenuScene(camera);
        // menuScene.

        final TextMenuItem titleMenuItem = new TextMenuItem(-1, textures.mFont,
                " WHEELZ");
        titleMenuItem.setScale(1.2f);
        titleMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(titleMenuItem);

        final TextMenuItem newGameMenuItem = new TextMenuItem(MENU_START,
                textures.mFont, "START GAME");
        newGameMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(newGameMenuItem);

        final TextMenuItem optionsMenuItem = new TextMenuItem(MENU_OPTIONS,
                textures.mFont, "CONTROLS + SETTINGS");
        optionsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(optionsMenuItem);

        final TextMenuItem helpMenuItem = new TextMenuItem(MENU_HELP,
                textures.mFont, "HELP");
        helpMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(helpMenuItem);

        final TextMenuItem FeintMenuItem = new TextMenuItem(MENU_FEINT,
                textures.mFont, "FEINT SCORES");
        FeintMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(FeintMenuItem);

        final TextMenuItem creditsMenuItem = new TextMenuItem(MENU_CREDITS,
                textures.mFont, "CREDITS");
        creditsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(creditsMenuItem);

        final TextMenuItem quitMenuItem = new TextMenuItem(MENU_QUIT,
                textures.mFont, "QUIT");
        quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(quitMenuItem);

        IMenuAnimator ma = new AlphaMenuAnimator(StatStuff.menuSpacing * 1.2f);
        menuScene.setMenuAnimator(ma);
        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }

    protected MenuScene createLevelPackMenuScene() {
        final MenuScene menuScene = new MenuScene(camera);
        // menuScene.


        final TextMenuItem janMenuItem = new TextMenuItem(
                MENU_JAN_PACK, textures.mFont, "JAN-PACK (" + (StatStuff.packLevelCount[StatStuff.janPackID] - 1) + ")");
        janMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(janMenuItem);

        final TextMenuItem bonusMenuItem = new TextMenuItem(
                MENU_BONUS_PACK, textures.mFont, "BONUS PACK (" + (StatStuff.packLevelCount[StatStuff.bonusPackID] - 1) + ")");
        bonusMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(bonusMenuItem);
        final TextMenuItem tWheelMenuItem = new TextMenuItem(
                MENU_TWHEEL_PACK, textures.mFont, "Third Wheel PACK (" + (StatStuff.packLevelCount[StatStuff.tWheelPackID] - 1) + ")");
        bonusMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(tWheelMenuItem);

        final TextMenuItem orignalMenuItem = new TextMenuItem(
                MENU_OLD_PACKS, textures.mFont, "OLD LEVELS(LOWGRAV)");
        orignalMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(orignalMenuItem);


        final TextMenuItem loadGameMenuItem = new TextMenuItem(MENU_LOAD,
                textures.mFont, "CUSTOM LEVEL(SD Card)");
        loadGameMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(loadGameMenuItem);

        IMenuAnimator ma = new AlphaMenuAnimator(StatStuff.menuSpacing);
        menuScene.setMenuAnimator(ma);
        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }

    protected MenuScene createOldLevelPackMenuScene() {
        final MenuScene menuScene = new MenuScene(camera);

        final TextMenuItem orignalMenuItem = new TextMenuItem(
                MENU_ORIGNAL_PACK, textures.mFont, "ORIGINAL LEVELS(16)");
        orignalMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(orignalMenuItem);

        final TextMenuItem xclassicMenuItem = new TextMenuItem(
                MENU_XCLASSIC_PACK, textures.mFont, "XMOTO CLASSIC(32)");
        xclassicMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(xclassicMenuItem);

        IMenuAnimator ma = new AlphaMenuAnimator(StatStuff.menuSpacing);
        menuScene.setMenuAnimator(ma);
        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }

    protected MenuScene createLockedClassicMenuScene() {
        final MenuScene menuScene = new MenuScene(camera);
        // menuScene.

        final TextMenuItem orignalMenuItem = new TextMenuItem(
                MENU_BUY_GAME, textures.mFont, "" + (StatStuff.packLevelCount[StatStuff.bonusPackID] - 1) + " LEVEL PACK ONLY IN FULL GAME");
        orignalMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(orignalMenuItem);

        final TextMenuItem buyMenuItem = new TextMenuItem(
                MENU_BUY_GAME, textures.mFont, "TOUCH HERE TO BUY");
        buyMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(buyMenuItem);


        final TextMenuItem newGameMenuItem = new TextMenuItem(MENU_START,
                textures.mFont, "RETURN TO MAIN MENU");
        newGameMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(newGameMenuItem);


        IMenuAnimator ma = new AlphaMenuAnimator(StatStuff.menuSpacing);
        menuScene.setMenuAnimator(ma);
        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }

    protected MenuScene createPackCompleteMenuScene() {
        final MenuScene menuScene = new MenuScene(camera);
        // menuScene.

        final TextMenuItem orignalMenuItem = new TextMenuItem(
                MENU_GO_ROOT, textures.mFont, "LEVEL PACK COMPLETED!");
        orignalMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(orignalMenuItem);


        final TextMenuItem newGameMenuItem = new TextMenuItem(MENU_GO_ROOT,
                textures.mFont, "GO TO MENU");
        newGameMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(newGameMenuItem);


        IMenuAnimator ma = new AlphaMenuAnimator(StatStuff.menuSpacing);
        menuScene.setMenuAnimator(ma);
        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }

    protected MenuScene createLevelMenuScene(int count) {
        final MenuScene menuScene = new MenuScene(camera);

        final TextMenuItem levelMenuTitle = new TextMenuItem(MENU_LEVELS
                + levelsFrom, textures.mFont, "PICK A LEVEL");
        levelMenuTitle.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        bufferWaste.add(levelMenuTitle.getVertexBuffer());
        menuScene.addMenuItem(levelMenuTitle);

        int atLevel;
        int maxLvl = 0;

		/*if (currentPack.compareTo(StatStuff.orignalPack) == 0) {
			maxLvl = StatStuff.lvlMax;
		} else if (currentPack.compareTo(StatStuff.xmClassicPack) == 0) {
			maxLvl = StatStuff.lvlMaxClassic;
		}*/
        maxLvl = StatStuff.packLevelCount[currentPackID];
        atLevel = root.prefs.getInt("atLevel" + this.getPackName(currentPackID), 2);

        int end = levelsFrom + count;
        for (int levelId = levelsFrom; levelId < end; levelId++) {
            if (levelId < atLevel && levelId < maxLvl) {
                final TextMenuItem leveleMenuItem = new TextMenuItem(
                        MENU_LEVELS + levelId, textures.mFont, "- LEVEL "
                        + levelId + " -");
                leveleMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                        GL10.GL_ONE_MINUS_SRC_ALPHA);
                bufferWaste.add(leveleMenuItem.getVertexBuffer());
                menuScene.addMenuItem(leveleMenuItem);
            } else {
                if (levelId + 1 > maxLvl) {
                    break;
                }
                final TextMenuItem leveleMenuItem = new TextMenuItem(-1,
                        textures.mFont, "- LEVEL LOCKED -");
                leveleMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                        GL10.GL_ONE_MINUS_SRC_ALPHA);
                bufferWaste.add(leveleMenuItem.getVertexBuffer());
                menuScene.addMenuItem(leveleMenuItem);
            }
        }
        if (end < maxLvl) {
            //if (atLevel > end) {
            final TextMenuItem leveleMenuItem = new TextMenuItem(
                    MENU_MORE_LEVELS, textures.mFont, "More");
            leveleMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                    GL10.GL_ONE_MINUS_SRC_ALPHA);
            bufferWaste.add(leveleMenuItem.getVertexBuffer());
            menuScene.addMenuItem(leveleMenuItem);
        }
        IMenuAnimator ma = new AlphaMenuAnimator(StatStuff.menuSpacing);
        menuScene.setMenuAnimator(ma);
        menuScene.buildAnimations();
        menuScene.setBackgroundEnabled(false);
        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }


    private void makeIntro() {
        instructionsDialog = new Builder(this.root);
        instructionsDialog.setTitle("Important");
        String dTxt = "";
        instructionsDialog.setMessage(dTxt + "" +
                "Look in Options to change the controls \n" +
                "Help will give you details of controls \n" +
                "email me with any questions or requests! \n" +
                "There is no way to respond to market place comments," +
                " if you email me I will let you know when a bug/feature is done :)");
        instructionsDialog.setPositiveButton("Don't show Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editor edit = root.prefs.edit();
                edit.putBoolean("showIntro", false);
                edit.commit();
            }

        });
        instructionsDialog.setNegativeButton("OK", null);
    }
	/*private void makeFeint(){
        instructionsDialog = new AlertDialog.Builder(this.root);
        instructionsDialog.setTitle("OpenFeint");
        String dTxt = "";
        instructionsDialog.setMessage(dTxt+"" +
        		"OpenFeint allows you to see others highscores and" +
        		" get your global rank on a level.\n" +
        		"Enable?");
        instructionsDialog.setPositiveButton("yes!", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor edit = root.prefs.edit();
				edit.putBoolean("autoFeint", true);
				edit.commit();

				try{
					//OpenFeint.login();//TODO replace OF code
				}catch (Exception e){

				}
			}

        });
        instructionsDialog.setNegativeButton("No!", null);
	}*/

    private void makeInfo() {
        instructionsDialog = new Builder(this.root);
        instructionsDialog.setTitle("Having Fun?");
        String dTxt = "";
        if (StatStuff.isDemo)
            dTxt = "\nAlso the full version of the game has Some Bonus levels and no Adverts!";
        instructionsDialog.setMessage(
                "Looks Like you've been playing for a while :) \n" +
                        "I hope you are enjoying it! Please consider leaving a nice comment and 5-Star rating" + dTxt);
        instructionsDialog.setPositiveButton("Leave comment", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (StatStuff.isDemo)
                    StatStuff.marketLite(AEMainMenu.this.root);
                else
                    StatStuff.marketFull(AEMainMenu.this.root);
            }

        });
        if (StatStuff.isDemo)
            instructionsDialog.setNeutralButton("Full version", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StatStuff.marketFull(AEMainMenu.this.root);
                }

            });
        instructionsDialog.setNegativeButton("No Thanks!", null);
    }

    float timeSinceLLoad = 0f;
    float speed = 1000f;
    boolean moved = false;

    @Override
    public void frameUpdate(float pSecondsElapsed) {
        timeSinceLLoad += pSecondsElapsed;
        if (timeSinceLLoad > 5 && speed < 10) {
            speed = 1000;
            timeSinceLLoad = 0;
            moved = false;
            loadRandomLevel();
        } else if (timeSinceLLoad > 1 && !moved) {
            moved = true;
            gameWorld.bike.setSpeed(.3f + (float) Math.random() * .7f);

        }
        gameWorld.frameUpdate(pSecondsElapsed);
        speed += gameWorld.bike.mBody.getLinearVelocity().len2();
        speed *= 0.95;

    }

    void loadRandomLevel() {
        gameWorld.bike.stopWheels();
        int pack = (int) (Math.random() * 3);
        int levelId = (int) ((StatStuff.packLevelCount[pack] - 2) * Math.random()) + 1;
        gameWorld.setLevelPack(pack);
        gameWorld.levelId = levelId;
        gameWorld.loadCurrentFromAsset();

    }


}
