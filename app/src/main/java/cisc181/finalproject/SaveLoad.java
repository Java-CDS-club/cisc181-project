package cisc181.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by jimmy on 5/14/17.
 */

public class SaveLoad {

    static void save(Entity e, Context context){
        //SharedPreferences mPref = ap
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();


        Item i = new Item();
        i.name = "Meme1";
        i.worth = 69;

        e = i;
//
        Gson gson = new Gson();
       String json = gson.toJson(e);
        edit.putString("Test",json);
        edit.commit();
//        //String json = gson.
//       // String json = gson.toJSon();
    }

    static Item load(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
//
      Gson gson = new Gson();
        String json = prefs.getString("Test", "");
        Entity z = gson.fromJson(json,Entity.class);
        Item i = (Item)z;
        return i;
       // return json;
//        Entity ent = gson.fromJson(json, Entity.class);
//        Log.d("ITEM","Loaded!");
//        Item z = (Item)ent;
//        return z;
    }
}
