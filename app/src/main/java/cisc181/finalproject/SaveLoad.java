package cisc181.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jimmy on 5/14/17.
 */

public class SaveLoad {

    static void save(ArrayList<Entity> eList, Context context){
        //SharedPreferences mPref = ap
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        ArrayList<Item> items = new ArrayList<>();
        ArrayList<Asteroid> asteroids = new ArrayList<>();
        for(Entity e : eList) {
            if (e instanceof Item)
                items.add((Item) e);
            else
                asteroids.add((Asteroid) e);
        }
        Gson gson = new Gson();
        String itemJSon = gson.toJson(items);
        String asterJSon = gson.toJson(asteroids);
        edit.putString("ITEMS",itemJSon);
        edit.putString("ASTERS",asterJSon);
        edit.commit();
    }

    static ArrayList<Entity> load(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        Gson gson = new Gson();
        String itemJSon = prefs.getString("ITEMS", "");
        Type type = new TypeToken<ArrayList<Item>>(){}.getType();
        ArrayList<Item> i = gson.fromJson(itemJSon,type);
        String asterJSon = prefs.getString("ASTERS","");
        type = new TypeToken<ArrayList<Asteroid>>(){}.getType();
        ArrayList<Asteroid> a = gson.fromJson(asterJSon,type);
        ArrayList<Entity> entities = new ArrayList<>();
        entities.addAll(i);
        entities.addAll(a);
        return entities;
    }
}
