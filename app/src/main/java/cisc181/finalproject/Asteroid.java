package cisc181.finalproject;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by jimmy on 5/8/17.
 */

public class Asteroid extends Entity {

    Bitmap sprite;

    Asteroid(FloatPoint pos, FloatPoint vel, FloatPoint acc){
        super(pos,vel,acc);
        health = 10;
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

    @Override
    void render(Canvas canvas, Paint paint, Camera camera) {
        //canvas.save();
        FloatPoint screenPos = camera.worldToScreenPos(pos);
        canvas.drawBitmap(sprite,screenPos.x,screenPos.y,paint);
        //canvas.restore();
    }
}
