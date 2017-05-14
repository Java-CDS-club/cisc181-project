package cisc181.finalproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by jimmy on 5/9/17.
 */

public class Shop extends Entity{
    String[] quips = {"Buy my stuff loser!",
            "No shirt, No shoes, No service.",
            "No refunds.",
            "Hey, give me some space man!",
            "What's the deal with astronaut food?",
            "You survived! If you died I'd be out of business.",
            "You ever feel like life is just a game?",
            "You call THAT a spaceship?",
            "Why are we here? Just to suffer?",
            "Where is this music coming from?",
            "This restaurant has no atmosphere.",
            "[canned space joke]"};

    Bitmap sprite;

    int currentTextNum = 0;
    double time;

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
        time++;
        FloatPoint screenPos = camera.worldToScreenPos(pos);
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);
        float yPos = (float)(20*Math.sin(time/30));
        canvas.drawText(quips[currentTextNum],screenPos.x+width/2,screenPos.y+yPos-40,paint);
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(sprite,screenPos.x,screenPos.y,paint);
    }

    void changeText(){
        currentTextNum = (currentTextNum+1)%quips.length;
    }
}
