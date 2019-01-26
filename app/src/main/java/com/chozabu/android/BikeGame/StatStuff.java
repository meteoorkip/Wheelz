package com.chozabu.android.BikeGame;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class StatStuff {
    public static final int CAMERA_WIDTH = 800;
    public static final int CAMERA_HEIGHT = 480;
    public static final String dbt = "ABike";
    public static final float menuSpacing = 10f;
    public static final int originalPackID = 0;
    public static final int xmClassicPackID = 1;
    public static final int janPackID = 2;
    public static final int bonusPackID = 3;
    public static final int tWheelPackID = 4;
    //free="3HXVXYADVUY16UGGWCWZ" pro="W51CCN4DSZYGJ52XFUSS"
    public static final String flurryKey = "W51CCN4DSZYGJ52XFUSS";

    public static final String[] packCompletedID = {"768973", "768983", "782982", "822422", "1720812", "ERR"};//
    public static final String[] packNames = {"ORIGNAL", "XMCLASSIC", "JAN-PACK", "BONUS PACK", "Third Wheel PACK"};
    public static final String[] packPrefix = {"level/original/l", "level/xmc/i", "level/janpack/l", "level/propack/l", "level/thirdwheel/l"};
    public static final String[] originalScoresID = {
            "605944", "605954", "605964", "605974",
            "605984", "605994", "606004", "606014",
            "606024", "606034", "606044", "606054",
            "606064", "606074", "606084", "606094",
            "ERROR"
    };
    public static final String[] xmClassicScoresID = {
            "606104", "606114", "606124", "606134",
            "606144", "606154", "606164", "606174",
            "606184", "606194", "606204", "606214",
            "606224", "606234", "606244", "606254",
            "606264", "606274", "606284", "606294",
            "606304", "606314", "606324", "606334",
            "606344", "606354", "606364", "606374",
            "606384", "606394", "606404", "606414",
            "ERROR"
    };
    public static final String[] janPackScoresID = {
            "610064", "610074", "610084", "613964",
            "615524", "620194", "622654", "632984",
            "ERROR"
    };
    public static final String[] bonusPackScoresID = {
            "642444", "695296", "695306",
            "ERROR"
    };
    public static final String[] tWheelPackScoresID = {
            "1208887", "1208897", "1208907",
            "ERROR"
    };


    public static final String[][] levelScoreIDs = {originalScoresID, xmClassicScoresID, janPackScoresID, bonusPackScoresID, tWheelPackScoresID};
    public static final int[] packLevelCount = {17, 33, 9, 4, 4};// 1 more than lvl num
    //
    public static final boolean isDemo = false;
    public static boolean isDev = false;
    public static boolean isWinner = false;

    /* The categories. */
    public static final short CATEGORYBIT_FG_DYNAMIC = 1;
    public static final short CATEGORYBIT_BG_DYNAMIC = 2;
    public static final short CATEGORYBIT_FG_STATIC = 4;
    public static final short CATEGORYBIT_BG_STATIC = 8;

    /* And what should collide with what. */
    public static final short MASKBITS_FG_DYNAMIC = CATEGORYBIT_FG_DYNAMIC + CATEGORYBIT_FG_STATIC;
    public static final short MASKBITS_BG_DYNAMIC = CATEGORYBIT_BG_DYNAMIC + CATEGORYBIT_BG_STATIC + CATEGORYBIT_FG_STATIC;
    public static final short MASKBITS_FG_STATIC = CATEGORYBIT_FG_DYNAMIC + CATEGORYBIT_BG_DYNAMIC;
    public static final short MASKBITS_BG_STATIC = CATEGORYBIT_BG_DYNAMIC;


    public static void marketLite(Activity currentActivity) {
        //String updateURI = "http://www.amazon.com/gp/mas/dl/android?p=com.chozabu.android.LightBikeGame";
        String updateURI = "market://details?id=com.chozabu.android.LightBikeGame";
        Intent updateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(updateURI));
        currentActivity.startActivity(updateIntent);
    }

    public static void marketFull(Activity currentActivity) {
        String updateURI = "market://details?id=com.chozabu.android.BikeGame";
        //String updateURI = "http://www.amazon.com/gp/mas/dl/android?p=com.chozabu.android.BikeGame";
        Intent updateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(updateURI));
        currentActivity.startActivity(updateIntent);
    }

    public static void sendMail(Activity currentActivity) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_TITLE, currentActivity.getString(R.string.email_name));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, currentActivity.getString(R.string.email_name));
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                "delete this text to avoid my wheelz spam filter"
        );
        String[] address = new String[1];
        address[0] = "chozabu+wheelzpro@gmail.com";
        //String[] address2 = String[new String("a")];
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);


        // Obtain refenerenc to String and pass it to Intent
        //emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.my_text));
        currentActivity.startActivity(emailIntent);
    }

}
