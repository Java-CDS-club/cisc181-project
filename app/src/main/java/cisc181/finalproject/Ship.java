package cisc181.finalproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by jimmy on 5/8/17.
 *
 * Ship
 * The ship the player controls.
 * Onwards, trusty steed!
 */

public class Ship extends Entity {

    float drag = 0.90f;
    int damage = 5;
    Bitmap sprite;

    Ship(FloatPoint pos, FloatPoint vel, FloatPoint acc){
        super(pos,vel,acc);
        accelScalar=0.5f;
    }

    @Override
    void update() {
        //If there is no acceleration, slow down the ship
        //YES I KNOW THERE IS NO DRAG IN SPACE
        //BUT ITS FOR GAMEPLAY GEEZ
        if(acc.x == 0 && acc.y == 0){
            vel.x*=drag;
            vel.y*=drag;
        }

        runPhysics();
    }

    public void setSprite(Bitmap b){
        sprite = b;
    }

    public Bitmap getSprite(){
        return sprite;
    }

    @Override
    void render(Canvas canvas, Paint paint, Camera camera) {
        //canvas.save();
        //canvas.restore();
    }
}
