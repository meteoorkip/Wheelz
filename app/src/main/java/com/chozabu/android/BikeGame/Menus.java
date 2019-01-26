package com.chozabu.android.BikeGame;

import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.animator.AlphaMenuAnimator;
import org.anddev.andengine.entity.scene.menu.animator.IMenuAnimator;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;

import javax.microedition.khronos.opengles.GL10;


public class Menus implements IOnMenuItemClickListener {

    protected static final int MENU_RESET = 0;
    protected static final int MENU_SKIP = MENU_RESET + 1;
    protected static final int MENU_QUIT = MENU_SKIP + 1;
    protected static final int MENU_RESUME = MENU_QUIT + 1;
    protected static final int MENU_BEGIN = MENU_RESUME + 1;
    protected static final int MENU_NEXT = MENU_BEGIN + 1;

    protected MenuScene mMenuFromButton;
    protected MenuScene mMenuComplete;
    protected MenuScene mMenuBegin;
    protected MenuScene mMenuLoading;
    protected MenuScene mMenuDead;

    GameRoot root = null;

    void init(GameRoot pRoot) {
        this.root = pRoot;
        this.mMenuFromButton = this.createPauseMenuScene();
        this.mMenuComplete = this.createCompleteMenuScene();
        this.mMenuBegin = this.createBeginMenuScene();
        this.mMenuLoading = this.createLoadingMenuScene();
        this.mMenuDead = this.createDeadMenuScene();
    }

    protected MenuScene createPauseMenuScene() {
        final MenuScene menuScene = new MenuScene(root.camera);


        final TextMenuItem title = new TextMenuItem(MENU_RESUME, root.textures.mFont, "PAUSED");
        title.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(title);

        final TextMenuItem resumeMenuItem = new TextMenuItem(MENU_RESUME, root.textures.mFont, "RESUME");
        resumeMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(resumeMenuItem);

        final TextMenuItem resetMenuItem = new TextMenuItem(MENU_RESET, root.textures.mFont, "RESET");
        resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(resetMenuItem);

        if (StatStuff.isDev) {
            final TextMenuItem skipMenuItem = new TextMenuItem(MENU_SKIP, root.textures.mFont, "SKIP");
            skipMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            menuScene.addMenuItem(skipMenuItem);
        }

        final TextMenuItem quitMenuItem = new TextMenuItem(MENU_QUIT, root.textures.mFont, "QUIT");
        quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(quitMenuItem);

        IMenuAnimator ma = new AlphaMenuAnimator(StatStuff.menuSpacing);
        menuScene.setMenuAnimator(ma);
        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }

    protected MenuScene createCompleteMenuScene() {
        final MenuScene menuScene = new MenuScene(root.camera);

        final TextMenuItem title = new TextMenuItem(MENU_NEXT, root.textures.mFont, "LEVEL COMPLETE");
        title.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(title);

        final TextMenuItem nextMenuItem = new TextMenuItem(MENU_NEXT, root.textures.mFont, "NEXT LEVEL");
        nextMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(nextMenuItem);

        final TextMenuItem resetMenuItem = new TextMenuItem(MENU_RESET, root.textures.mFont, "REPLAY");
        resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(resetMenuItem);

        final TextMenuItem quitMenuItem = new TextMenuItem(MENU_QUIT, root.textures.mFont, "QUIT");
        quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(quitMenuItem);

        IMenuAnimator ma = new AlphaMenuAnimator(StatStuff.menuSpacing);
        menuScene.setMenuAnimator(ma);
        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }

    protected MenuScene createBeginMenuScene() {
        final MenuScene menuScene = new MenuScene(root.camera);



		/*final TextMenuItem titleMenuItem = new TextMenuItem(MENU_BEGIN, root.textures.mFont, this.mName);
		titleMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(titleMenuItem);*/

        final TextMenuItem resetMenuItem = new TextMenuItem(MENU_BEGIN, root.textures.mFont, "TOUCH TO BEGIN");
        resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(resetMenuItem);


        IMenuAnimator ma = new AlphaMenuAnimator(StatStuff.menuSpacing);
        menuScene.setMenuAnimator(ma);
        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }

    protected MenuScene createLoadingMenuScene() {
        final MenuScene menuScene = new MenuScene(root.camera);

        final TextMenuItem nextMenuItem = new TextMenuItem(-1, root.textures.mFont, "-|-");
        nextMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(nextMenuItem);

        final TextMenuItem title = new TextMenuItem(-1, root.textures.mFont, "LOADING");
        title.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(title);

        final TextMenuItem decMenuItem = new TextMenuItem(-1, root.textures.mFont, "-|-");
        decMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(decMenuItem);


        IMenuAnimator ma = new AlphaMenuAnimator(StatStuff.menuSpacing);
        menuScene.setMenuAnimator(ma);
        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }

    protected MenuScene createDeadMenuScene() {
        final MenuScene menuScene = new MenuScene(root.camera);


        final TextMenuItem title = new TextMenuItem(MENU_RESET, root.textures.mFont, "CRASHED");
        title.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(title);

        final TextMenuItem resetMenuItem = new TextMenuItem(MENU_RESET, root.textures.mFont, "RESET");
        resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(resetMenuItem);

        final TextMenuItem quitMenuItem = new TextMenuItem(MENU_QUIT, root.textures.mFont, "QUIT");
        quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(quitMenuItem);

        IMenuAnimator ma = new AlphaMenuAnimator(StatStuff.menuSpacing);
        menuScene.setMenuAnimator(ma);
        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }


    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
        root.sounds.mBeBoopSound.play();
        switch (pMenuItem.getID()) {
            case MENU_RESET:
                //root.getScene().clearChildScene();
                //root.getScene().setChildScene(root.menus.mMenuLoading);
                //root.restartLevel();

                if (root.gameWorld.levelFromFile) {
                    root.nextLevel();
                    return true;
                }

                root.root.getEngine().runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {

                        root.getScene().clearChildScene();
                        root.getScene().setChildScene(root.menus.mMenuLoading);
                        root.restartLevel();
                        root.getScene().setChildScene(root.menus.mMenuBegin);

                        root.root.getEngine().runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                root.getScene().setChildScene(root.menus.mMenuBegin);

                            }
                        });
                    }
                });
                return true;

            case MENU_NEXT:
                root.nextLevel();
                return true;

            case MENU_RESUME:
                root.unPause();
                return true;
            case MENU_SKIP:
                root.passLevel();
                root.getScene().clearChildScene();
                root.getScene().setChildScene(root.menus.mMenuLoading);
                root.gameWorld.nextLevel();


                root.root.getEngine().runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {
                        root.getScene().setChildScene(root.menus.mMenuBegin);

                    }
                });
                return true;

            case MENU_BEGIN:
                root.begin();
                //Debug.startMethodTracing("ABikeTrace");
                return true;
            case MENU_QUIT:
                //TODO this
				/*Intent mainMenuIntent = new Intent(root, AEMainMenu.class);
				root.startActivity(mainMenuIntent);
				root.sounds.stop();
				root.finish();*/
                root.quitGame();
                //System.exit(0);
                return true;
            default:
                return true;
        }
    }

}
