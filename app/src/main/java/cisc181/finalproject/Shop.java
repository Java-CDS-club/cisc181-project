package cisc181.finalproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by jimmy on 5/9/17.
 */

public class Shop extends Entity{
    String[] quips = {"Hello, welkomm to mein shop. Get out.","No shirt, No shoes, No service."};

    Bitmap sprite;

    Shop(FloatPoint pos, FloatPoint vel, FloatPoint acc) {
        super(pos, vel, acc);
    }

    public void setSprite(Bitmap b){
        sprite = b;
    }

    public Bitmap getSprite(){
        return sprite;
    }

    @Override
    void update() {

    }

    @Override
    void render(Canvas canvas, Paint paint, Camera camera) {
        FloatPoint screenPos = camera.worldToScreenPos(pos);
        canvas.drawBitmap(sprite,screenPos.x,screenPos.y,paint);
    }
}
