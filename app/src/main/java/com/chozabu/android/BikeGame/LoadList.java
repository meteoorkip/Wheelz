package com.chozabu.android.BikeGame;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//import android.content.Intent;

public class LoadList extends ListActivity {
    private String[] levelNames;

    //private String[] levelIDs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Load Level");

        levelNames = FileSystem.getFileNames();
        if (levelNames == null) {
            Toast.makeText(getApplicationContext(), "This is to load levels you have made\nfrom /sdcard/xlvls/", Toast.LENGTH_SHORT).show();
            return;
        }
        if (levelNames.length == 0) {
            Toast.makeText(getApplicationContext(), "This is to load levels you have made\nfrom /sdcard/xlvls/", Toast.LENGTH_SHORT).show();
            return;
        }
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, levelNames));
        getListView().setTextFilterEnabled(true);

    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (pEvent.getAction() != KeyEvent.ACTION_DOWN)
            return super.onKeyDown(pKeyCode, pEvent);
        if (pKeyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(pKeyCode, pEvent);
    }

    @Override
    public boolean onKeyUp(final int pKeyCode, final KeyEvent pEvent) {
        if (pKeyCode == KeyEvent.KEYCODE_BACK) {
            Intent StartGameIntent = new Intent(LoadList.this, MainActivity.class);
            startActivity(StartGameIntent);
            //System.exit(0);
            this.finish();
            return true;
        }
        return super.onKeyUp(pKeyCode, pEvent);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent StartGameIntent = new Intent(LoadList.this, MainActivity.class);
        StartGameIntent.putExtra("com.chozabu.android.BikeGame.toLoad", levelNames[position]);
        startActivity(StartGameIntent);
        //System.exit(0);
        this.finish();
        //String toDl = levelIDs[position];
        //String newLevel = FileSystem.downloadLevel(toDl);
        //Log.i("PPground", newLevel);
        //Toast.makeText(getApplicationContext(), "downloaded: "+levelNames[position], Toast.LENGTH_SHORT).show();

        //FileSystem.writeFile(levelIDs[position], newLevel);
    }
}
