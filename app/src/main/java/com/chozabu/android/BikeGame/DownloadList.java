package com.chozabu.android.BikeGame;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

//import org.json.JSONArray;
//import android.content.Intent;

public class DownloadList extends ListActivity {
    private String[] levelNames;
    private String[] levelIDs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Download PP Worlds");

        String lList = FileSystem.getLevelList("default", 10);
        JSONObject levels;

        try {
            levels = new JSONObject(lList);
            levelNames = new String[levels.length()];
            levelIDs = new String[levels.length()];
            Iterator<String> keys = levels.keys();
            int i = 0;
            while (keys.hasNext()) {//
                JSONObject level = levels.getJSONObject(keys.next());
                String Id = level.getString("name") + "(" + level.getString("author") + ")";
                levelNames[i] = Id;//level.getString("name");
                levelIDs[i] = level.getString("filename");
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, levelNames));
        getListView().setTextFilterEnabled(true);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String toDl = levelIDs[position];
        String newLevel = FileSystem.downloadLevel(toDl);
        //Log.i("PPground", newLevel);
        Toast.makeText(getApplicationContext(), "downloaded: " + levelNames[position], Toast.LENGTH_SHORT).show();

        FileSystem.writeFile(levelIDs[position], newLevel);
    }
}
