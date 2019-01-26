package com.chozabu.android.BikeGame;

//import java.io.File;

import android.os.Environment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileSystem {

    private static final String SERVER_ADDR = "http://www.pground.chozabu.net/";
    //File path = Environment.getExternalStoragePublicDirectory(
    //        Environment.DIRECTORY_PICTURES);
    static File prePath = Environment.getExternalStorageDirectory();
    //static File path = new File(prePath,"/Android/data/com.chozabu.android.BikeGame/cache/");
    //static File path = new File(prePath,"/ABike/");
    static File path = new File(prePath, "/xlvls/");


    static boolean mExternalStorageAvailable = false;
    static boolean mExternalStorageWriteable = false;

    public static File[] getFileList() {
        checkRW();
        if (!mExternalStorageAvailable) return null;
        path.mkdirs();
        return path.listFiles();
    }

    public static String[] getFileNames() {
        checkRW();
        if (!mExternalStorageAvailable) return null;
        path.mkdirs();
        return path.list();
    }

    public static void writeFile(String name, String data) {
        checkRW();
        if (!mExternalStorageWriteable) return;
        path.mkdirs();
        File file = new File(path, name);
        //InputStream is = data;
        OutputStream os;
        try {
            os = new FileOutputStream(file);
            try {
                os.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String name) {
        return readFilep(new File(path, name));
    }

    public static String readFilep(File file) {
        checkRW();
        if (!mExternalStorageAvailable) return null;
        path.mkdirs();
        //File file = new File(path, name);
        InputStream is;
        byte[] data = null;
        try {
            is = new FileInputStream(file);
            try {
                data = new byte[is.available()];
                is.read(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new String(data);
    }

    public static void checkRW() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    public static String uploadLevel(String name, String author, String passHash, String data) {
        String url = SERVER_ADDR + "uploadLevel";
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("author", author));
        nameValuePairs.add(new BasicNameValuePair("passHash", passHash));
        nameValuePairs.add(new BasicNameValuePair("levelData", data));
        nameValuePairs.add(new BasicNameValuePair("name", name));
        return generalPost(url, nameValuePairs);
    }

    public static String downloadLevel(String fullname) {
        String url = SERVER_ADDR + "downloadLevel";
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("fullname", fullname));
        return generalPost(url, nameValuePairs);
    }

    public static String createAccount(String userName, String passHash, String creating) {
        String url = SERVER_ADDR + "createUser";
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("userName", userName));
        nameValuePairs.add(new BasicNameValuePair("passHash", passHash));
        nameValuePairs.add(new BasicNameValuePair("creating", creating));
        return generalPost(url, nameValuePairs);
    }


    public static String getLevelList(String SortBy, int count) {
        String url = SERVER_ADDR + "listLevels";

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("SortBy", SortBy));
        String cStr = "" + count;
        nameValuePairs.add(new BasicNameValuePair("count", cStr));
        return generalGet(url, nameValuePairs);

    }

    public static String generalPost(String url, List<NameValuePair> pairs) {
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(url);
        HttpResponse response = null;

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs));
        } catch (Exception e) {
            System.out.println("Error encoding pairs: " + e);
            return "Failed to encode information";
        }
        try {
            response = httpClient.execute(httpPost);
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            response.getEntity().writeTo(ostream);
            return ostream.toString();
        } catch (ClientProtocolException e) {
            System.out.println("HTTPHelp : ClientProtocolException : " + e);
        } catch (IOException e) {
            System.out.println("HTTPHelp : IOException : " + e);
        }
        return "Unknown Failure";
    }

    public static String generalGet(String url, List<NameValuePair> pairs) {
        HttpClient httpClient = new DefaultHttpClient();

        HttpGet httpPost = new HttpGet(url);
        HttpResponse response = null;

        try {
            response = httpClient.execute(httpPost);
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            HttpEntity temp = response.getEntity();
            temp.writeTo(ostream);
            return ostream.toString();
        } catch (ClientProtocolException e) {
            System.out.println("HTTPHelp : ClientProtocolException : " + e);
        } catch (IOException e) {
            System.out.println("HTTPHelp : IOException : " + e);
        }
        return "Unknown Failure";
    }
}
