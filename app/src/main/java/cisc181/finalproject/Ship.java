package cisc181.finalproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;

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
    ArrayList<Item> cargo = new ArrayList<Item>();
    int maxFuel = 4000;
    int currentFuel = maxFuel;

    int maxHealth = 200;
    int currentHealth = maxHealth;

    Ship(FloatPoint pos, FloatPoint vel, FloatPoint acc){
        super(pos,vel,acc);
        accelScalar=0.5f;
        reboundSpeed=7;
    }

    @Override
    public void handleCollision(int collisionDir) {
        super.handleCollision(collisionDir);

        currentHealth-=1;
    }

    @Override
    void update() {
        //If there is no acceleration, slow down the ship
        //YES I KNOW THERE IS NO DRAG IN SPACE
        //BUT ITS FOR GAMEPLAY GEEZ
        if(acc.x == 0 && acc.y == 0){
            vel.x*=drag;
            vel.y*=drag;
            currentFuel-=1;
        }else{
            currentFuel-=2;
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
       // renderBounds(canvas,paint,camera);
        //canvas.save();
        //canvas.restore();
    }
}
