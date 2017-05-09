package cisc181.finalproject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by jimmy on 5/8/17.
 */

public abstract class Entity {
    FloatPoint pos;
    FloatPoint vel;
    FloatPoint acc;
    float angle;
    float accelScalar = 1;
    float width;
    float height;
    int health;
    boolean dead;

    Entity(FloatPoint pos, FloatPoint vel,FloatPoint acc){
        this.pos = pos;
        this.vel = vel;
        this.acc = acc;
        angle = 0;
        width=256;
        height=256;
        dead = false;
    }

    abstract void update();
    abstract void render(Canvas canvas, Paint paint, Camera camera);

    void addHealth(int toAdd){
        health+=toAdd;
    }

    void removeHealth(int toAdd){
        addHealth(-toAdd);
    }

    void runPhysics(){
        vel.x += acc.x;
        vel.y += acc.y;

        pos.x += vel.x;
        pos.y += vel.y;
    }

    boolean collides(FloatPoint p2, float w2, float h2){
        return p2.x<pos.x+width &&
                p2.x+w2 > pos.x &&
                p2.y < pos.y+height &&
                h2+p2.y>height;
    }



    boolean containsPoint(FloatPoint point){
        return point.x >= pos.x && point.x < pos.x + width && point.y >= pos.y && point.y < pos.y + height;
    }

    void setAcc(FloatPoint acc){
        this.acc = new FloatPoint(accelScalar*acc.x, accelScalar*acc.y);
       // this.acc = acc;
    }
}
