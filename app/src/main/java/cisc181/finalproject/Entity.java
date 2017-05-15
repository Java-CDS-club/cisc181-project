package cisc181.finalproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jimmy on 5/8/17.
 *
 * Entity
 * A general physics enabled entity that has acceleration, velocity, and position =
 * Plus health, width, height, etc etc
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
    float reboundSpeed = 0;

    Entity(FloatPoint pos, FloatPoint vel,FloatPoint acc){
        this.pos = pos;
        this.vel = vel;
        this.acc = acc;
        angle = 0;
        width=128;
        height=128;
        dead = false;
    }

    abstract void update();

    //Renders on the canvas (with the camera transformation)
    abstract void render(Canvas canvas, Paint paint, Camera camera);

    void renderBounds(Canvas canvas, Paint paint, Camera camera){
        paint.setColor(Color.RED);

        FloatPoint worldPos = camera.worldToScreenPos(pos);
        //Top
        canvas.drawLine(worldPos.x,worldPos.y,worldPos.x+width,worldPos.y,paint);

        //Right
        canvas.drawLine(worldPos.x,worldPos.y,worldPos.x,worldPos.y+height,paint);

        //Left
        canvas.drawLine(worldPos.x+width,worldPos.y,worldPos.x+width,worldPos.y+height,paint);

        //Bot
        canvas.drawLine(worldPos.x,worldPos.y+height,worldPos.x+width,worldPos.y+height,paint);
        paint.setColor(Color.BLACK);
    }

    void addHealth(int toAdd){
        health+=toAdd;
    }

    void removeHealth(int toAdd){
        addHealth(-toAdd);
    }

    //Numerical integration!
    void runPhysics(){
        vel.x += acc.x;
        vel.y += acc.y;

        pos.x += vel.x;
        pos.y += vel.y;
    }

    //Check if a rectangle collides with these bounds
    boolean collides(FloatPoint p2, float w2, float h2){
        return p2.x<pos.x+width &&
                p2.x+w2 > pos.x &&
                p2.y < pos.y+height &&
                h2+p2.y>height;
    }

    boolean collides(Entity other){
        return this.pos.x < other.pos.x + other.width &&
                this.pos.x + this.width > other.pos.x &&
                this.pos.y < other.pos.y + other.height &&
                this.height + this.pos.y > other.pos.y;
    }

    //Check if the bounds contain a point
    boolean containsPoint(FloatPoint point){
        return point.x >= pos.x && point.x < pos.x + width && point.y >= pos.y && point.y < pos.y + height;
    }

    public int collisionDirection(Entity other){
        float player_bottom = this.pos.y + this.height;
        float tiles_bottom = other.pos.y + other.height;
        float player_right = this.pos.x + this.width;
        float tiles_right = other.pos.x + other.width;

        float b_collision = tiles_bottom - this.pos.y;
        float t_collision = player_bottom - other.pos.y;
        float l_collision = player_right - other.pos.x;
        float r_collision = tiles_right - this.pos.x;

        if (t_collision < b_collision && t_collision < l_collision && t_collision < r_collision )
        {
            //Top
         return 0;
        }
        if (b_collision < t_collision && b_collision < l_collision && b_collision < r_collision)
        {
            //Bot
            return 1;
        }
        if (l_collision < r_collision && l_collision < t_collision && l_collision < b_collision)
        {
//Left collision
            //Left
            return 2;
        }
        if (r_collision < l_collision && r_collision < t_collision && r_collision < b_collision )
        {
//Right collision
            return 3;
        }

        return -1;
    }

    public void handleCollision(int collisionDir){
        switch (collisionDir){
            case 0:
                //Top

                vel.y-=reboundSpeed;
                //playerShip.vel
                break;
            case 1:
                //Bot
                vel.y+=reboundSpeed;
                break;
            case 2:
                vel.x-=reboundSpeed;
                //Left
                break;
            case 3:
                vel.x+=reboundSpeed;

                //Right
                break;
        }
    }

    void setAcc(FloatPoint acc){
        this.acc = new FloatPoint(accelScalar*acc.x, accelScalar*acc.y);
    }
}
