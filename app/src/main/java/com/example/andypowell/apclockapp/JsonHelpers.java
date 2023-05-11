package com.example.andypowell.apclockapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class JsonHelpers {

    public String loadJSONFromAsset(InputStream is) {
        String json = null;
        try {
            //InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public JSONObject changeStat(InputStream is){
        JSONObject q = new JSONObject();
        try {
            Random r = new Random();
            int i1 = r.nextInt(207 - 1) + 1;
            JSONObject obj = new JSONObject(loadJSONFromAsset(is));
            JSONArray m_jArry = obj.getJSONArray("stats");
            JSONObject jo_inside = m_jArry.getJSONObject(i1);
            q = jo_inside;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return q;
    }
}
