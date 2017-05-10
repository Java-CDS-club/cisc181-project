package cisc181.finalproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by jimmy on 5/8/17.
 *
 * Item
 * An item/mineral that you can sell for money
 */

public class Item extends Entity{

    int worth = 0;
    String name = "default item";
    Bitmap sprite;

    Item(){
        super(new FloatPoint(0,0), new FloatPoint(0,0), new FloatPoint(0,0));
    }
    Item(FloatPoint pos, FloatPoint vel, FloatPoint acc){
        super(pos,vel,acc);
    }

    @Override
    void update() {
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
        FloatPoint screenPos = camera.worldToScreenPos(pos);
        canvas.drawBitmap(sprite,screenPos.x,screenPos.y,paint);
    }
}
