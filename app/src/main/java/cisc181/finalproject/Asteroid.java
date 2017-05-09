package cisc181.finalproject;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by jimmy on 5/8/17.
 */

public class Asteroid extends Entity {

    Bitmap sprite;
    ArrayList<Item> contents = new ArrayList<Item>();

    Asteroid(FloatPoint pos, FloatPoint vel, FloatPoint acc){
        super(pos,vel,acc);
        health = 10;

        Item i = new Item();
        i.worth =10;
        i.name = "meme";

        contents.add(i);
    }

    public void setSprite(Bitmap b){
        sprite = b;
    }

    public Bitmap getSprite(){
        return sprite;
    }

    @Override
    void update() {
        if(health <= 0){
            dead = true;
        }
        runPhysics();
    }

    public ArrayList<Item> dropCargo(){
        ArrayList<Item> toDrop = contents;
        for(Item i: toDrop){
            i.pos = pos;
            i.vel = new FloatPoint(1,1);
        }
       // contents.clear();
        return toDrop;
    }

    @Override
    void render(Canvas canvas, Paint paint, Camera camera) {
        //canvas.save();
        FloatPoint screenPos = camera.worldToScreenPos(pos);
        canvas.drawBitmap(sprite,screenPos.x,screenPos.y,paint);
        //canvas.restore();
    }
}
