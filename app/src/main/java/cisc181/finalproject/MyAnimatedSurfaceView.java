package cisc181.finalproject;

/*

SimpleAnimate app -- animated drawing in a separate thread

Code Template by Christopher Rasmussen
copyright 2017, University of Delaware

*/

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.lang.Override;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

// note that we are not implementing the SurfaceHolder.Callback interface at this level anymore

public class MyAnimatedSurfaceView extends SurfaceView {

    private MyThread myThread;

    Paint mPaint;
    Bitmap bmap, ast;

    float PLAYER_CENTER_X = 200;
    float PLAYER_CENTER_Y = 200;
    float SCREEN_WIDTH = 0;
    float SCREEN_HEIGHT = 0;
    final int picSize = 128;
    int score = 0;
    //Init objects
    Ship playerShip;
    Asteroid aste = new Asteroid(new FloatPoint(500,500), new FloatPoint(0,0), new FloatPoint(0,0));
    Shop shop = new Shop(new FloatPoint(300,800), new FloatPoint(0,0), new FloatPoint(0,0));

    //Contains all of the updatable objects that aren't the player
    ArrayList<Entity> entities = new ArrayList<Entity>();

    SoundPool sp;
    int explosion;
    int laser;
    int pickupItem;
    int pickupFuel;
    MediaPlayer booster;
    MediaPlayer music;
    Bitmap ironImage;
    Bitmap spaceStation;
    Bitmap arrow;

    //Stuff to pass in from the activity
    static int startFuel = 0;
    boolean isHardMode = false;


    //Constructor
    public MyAnimatedSurfaceView(Context context, AttributeSet attrs) {

        super(context, attrs);

        //Init sounds
        booster = MediaPlayer.create(context,R.raw.booster2);

        booster.setVolume(0.2f, 0.2f);
        music = MediaPlayer.create(context,R.raw.space);
        music.setVolume(0.8f,0.8f);
        music.setLooping(true);
       // music.start();
        booster.setLooping(true);

        sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);  // deprecated from API level 21 on

        laser = sp.load(context,R.raw.laser,1);
        pickupItem = sp.load(context,R.raw.pickup_item,1);
        pickupFuel = sp.load(context,R.raw.pickup_fuel,1);
        explosion = sp.load(context, R.raw.expl, 1);

        booster.setVolume(0.8f,0.8f);

        //Init threads
        myThread = new MyThread(this);

        //Getting the size of our screen/canvas
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);

        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
        PLAYER_CENTER_X = size.x/2-picSize/2;
        PLAYER_CENTER_Y = size.y/2-picSize/2;


        //Set up paint
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);

        //Init player
        playerShip = new Ship(new FloatPoint(PLAYER_CENTER_X,PLAYER_CENTER_Y), new FloatPoint(0,0), new FloatPoint(0,0));
        playerShip.currentFuel=startFuel;
        if(startFuel == 2000)
            isHardMode = true;


        //Image loading
        bmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship);
        ast = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid);
        bmap = Bitmap.createScaledBitmap(bmap,picSize,picSize,false);
        ast = Bitmap.createScaledBitmap(ast,picSize,picSize,false);
        spaceStation = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.iss),picSize,picSize,false);
        ironImage = BitmapFactory.decodeResource(getResources(), R.drawable.iron);
        ironImage = Bitmap.createScaledBitmap(ironImage,picSize,picSize,false);

        arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);


        playerShip.setSprite(bmap);
        shop.setSprite(spaceStation);
       // entities.add(shop);
        aste.setSprite(ast);
        aste.width=picSize;
        aste.height=picSize;
        entities.add(aste);

        //Generate a random asteroid field
        //TODO make it more uniformly distributed
        int bound = 10000;
        for(int i = 0; i < 500; i++){
            Random r = new Random();
            //float randomX = (float)r.nextInt(10000);
            //float randomY = (float)r.nextInt(10000);
            FloatPoint randPos = getPositionInSquare(-bound/2,-bound/2,bound,bound);
            float randomVelX = (float)r.nextInt(10-5+1)-5;
            float randomVelY = (float)r.nextInt(10-5+1)-5;
            Asteroid a = new Asteroid(randPos, new FloatPoint(randomVelX,randomVelY), new FloatPoint(0,0));
            a.width=picSize;
            a.height=picSize;
            a.setSprite(ast);
            entities.add(a);
        }

       // SaveLoad.save(entities,context);
       //entities = SaveLoad.load(context);


       //testSaveLoad(context);


        SurfaceHolder holder = getHolder();


        //Weird thread stuff
        //It just werks
        holder.addCallback(new SurfaceHolder.Callback() {

            // start draw thread

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
               // if (myThread.getState() == Thread.State.NEW) {
                    myThread.setRunning(true);
                //Log.d("III","running!");
                if (myThread.getState() == Thread.State.NEW) {
                    myThread.start();
                }
               // }
               // }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            // stop draw thread

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d("mem","jjjjj");
                boolean retry = true;
               // myThread.setRunning(false);
//                myThread.stop();

            }
        });

    }

    //Init world camera
    Camera camera = new Camera(new FloatPoint(0,0));

    //A nice spacey blue
    String backgroundColor = "#17132c";

    FloatPoint getPositionInSquare(int x, int y, int width, int height){
        Random r = new Random();
        float randomX = r.nextInt(width)+x;
        float randomY = r.nextInt(height)+y;
        return new FloatPoint(randomX,randomY);
    }

    String collision = "null";
    boolean gameOver = false;
    boolean pause = false;

    public void myDraw(Canvas canvas) {
        if(playerShip.currentHealth <= 0 || playerShip.currentFuel <= 0){
            gameOver = true;
        }

        //Update camera based on player position
        camera = new Camera(new FloatPoint(playerShip.pos.x-PLAYER_CENTER_X, playerShip.pos.y-PLAYER_CENTER_Y));

        //Draw background solor
        canvas.drawColor(Color.parseColor(backgroundColor));



        if(!gameOver) {
            updateEntites();
        }
        renderEntities(canvas);

        if(gameOver){
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(60);

            mPaint.setTextAlign(Paint.Align.CENTER);

            if(playerShip.currentHealth <= 0){
                booster.stop();
                MainActivity.endGame(0,score);//0 means dead
            }

            if(playerShip.currentFuel <= 0){
                booster.stop();
                MainActivity.endGame(1,score);//1 means no fuel
            }
        }
        mPaint.setColor(Color.BLACK);
       // playerShip.update();


        mPaint.setColor(Color.WHITE);
        int barWidth = 400;
        int barHeight = 30;
        mPaint.setTextAlign(Paint.Align.LEFT);
        //canvas.drawText(playerShip.currentHealth+"",0,100,mPaint);
        canvas.drawRect(0,0,barWidth,barHeight,mPaint);

        mPaint.setColor(Color.BLUE);
        int xRight = interpolate(barWidth,playerShip.maxFuel,playerShip.currentFuel);
        canvas.drawRect(0,0,xRight,barHeight,mPaint);
        //FloatPoint shopText = new
        //canvas.drawText(collision,shop.x,200,mPaint);

        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0,60,barWidth,60+barHeight,mPaint);

        mPaint.setColor(Color.RED);
        xRight = interpolate(barWidth,playerShip.maxHealth,playerShip.currentHealth);
        canvas.drawRect(0,60,xRight,60+barHeight,mPaint);

        mPaint.setTextAlign(Paint.Align.RIGHT);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(70);
            canvas.drawText("Score: "+score, SCREEN_WIDTH, 60, mPaint);

        mPaint.setColor(Color.BLACK);



    }

    public int interpolate(float x1, float y1, float y2){

        return (int)((x1/y1)*y2);
    }

    public void updateEntites(){
        //Update all of the entities in the world
        //Use an iterator so we can safely remove entities
        Iterator<Entity> it = entities.iterator();
        ArrayList<Entity> toAdd = new ArrayList<Entity>();
        while(it.hasNext()){
            Entity e = it.next();

            if(e.dead){
                //Remove the entity if it is dead
                it.remove();
            }else {
                //Update it
                e.update();

                //For asteroids
                //TODO why does this work when it already checks for dead?
                if(e instanceof Asteroid){
                    Asteroid ast = (Asteroid)e;
                    if(playerShip.collides(ast)){
                        playerShip.handleCollision(playerShip.collisionDirection(ast));
                    }

                    //If asteroid is dead, spew out the bounty
                    if(ast.dead){
                        sp.play(explosion, 1f, 1f, 0, 0, 1f);

                        ArrayList<Item> cargo = ast.dropCargo();

                        for(Item z: cargo){
                            z.setSprite(ironImage);
                        }

                        //Add cargo to toAdd
                        toAdd.addAll(cargo);
                    }
                }else if(e instanceof Shop){
                    Shop s = (Shop)e;

                    if(distance(playerShip,s)>2000){
                        s.changeText();
                    }

                }else if(e instanceof Item){
                    Item itm = (Item)e;
                    if(playerShip.collides(itm)){
                        sp.play(pickupItem, 1f, 1f, 0, 0, 1f);

                        playerShip.cargo.add(itm);
                        itm.dead = true;
                        score+=itm.worth;
                        if(isHardMode)
                            score+=itm.worth;//double points on hard mode
                    }
                }
                // e.render(canvas, mPaint, camera);
            }
        }

        //Add entities to the world, since we can't do it concurrently
        for(Entity e: toAdd){
            entities.add(e);
        }

        playerShip.update();


    }

    public void gameOver(String message){

    }

    public void renderEntities(Canvas canvas ){
        for(Entity e: entities){
            e.render(canvas, mPaint, camera);
        }

        //Draw the player ship
        canvas.save();
        playerShip.render(canvas,mPaint,camera);
        canvas.rotate(playerShip.angle-45+90, PLAYER_CENTER_X+playerShip.width/2, PLAYER_CENTER_Y+playerShip.height/2);
        //canvas.drawBitmap(playerShip.getSprite(), PLAYER_CENTER_X-playerShip.getSprite().getWidth()/2, PLAYER_CENTER_Y-playerShip.getSprite().getHeight()/2,mPaint);
        canvas.drawBitmap(playerShip.getSprite(), PLAYER_CENTER_X, PLAYER_CENTER_Y,mPaint);

        canvas.restore();
    }

    public float distance(Entity a, Entity b){
        float dx = b.pos.x - a.pos.x;
        float yx = b.pos.y - a.pos.y;
        return (float)Math.sqrt((dx*dx)+(yx*yx));
    }

    // respond to the SurfaceView being clicked/dragged
    public boolean onTouchEvent(MotionEvent e) {

        //Finger down (fires once)
        if(e.getAction() == MotionEvent.ACTION_DOWN){
            booster.start();
            FloatPoint touchPosWorld = camera.screenToWorldPos(new FloatPoint(e.getX(),e.getY()));

            //Check which entity we touch
            for(Entity ent: entities){
                if(ent.containsPoint(touchPosWorld)){

                    //If it is an asteroid, do damage to it
                    if(ent instanceof Asteroid){
                        Asteroid a = (Asteroid)ent;
                        sp.play(laser, 1f, 1f, 0, 0, 1f);

                        a.removeHealth(playerShip.damage);

                    }
                }
            }
        }

        //Finger up
        if(e.getAction() == MotionEvent.ACTION_UP){
            booster.pause();
        }

        //Finger dragging
        if(e.getAction() == MotionEvent.ACTION_MOVE){

            //Convert the point touched on screen to world coordinates
            FloatPoint touch = camera.screenToWorldPos(new FloatPoint(e.getX(),e.getY()));

            //Calculate the angle between the touch point and the player
            float theta = (float)Math.atan2((touch.y-(playerShip.pos.y+playerShip.height/2)),(touch.x-(playerShip.pos.x+playerShip.width/2)));

            //Set acceleration using trig
            playerShip.setAcc(new FloatPoint((float)Math.cos(theta), (float)Math.sin(theta)));
            playerShip.angle = (float)Math.toDegrees(theta);
        }else{
            //If we're not touching, stop accelerating
            playerShip.setAcc(new FloatPoint(0,0));
        }

        return true;
    }

    public void testSaveLoad(Context context){
        Item i = new Item();
        i.worth = 420;
        i.name = "ooga chacka";
        ArrayList<Entity>saveItems = new ArrayList<Entity>();
        Asteroid a = new Asteroid(new FloatPoint(0,0),new FloatPoint(0,0),new FloatPoint(0,0));
        saveItems.add(i);
        saveItems.add(a);
        saveItems.add(i);
        saveItems.add(a);
        SaveLoad.save(saveItems, context);
        Log.d("ITEM","SAVED");
        ArrayList<Entity> loadItems= SaveLoad.load(context);
        Log.d("ITEM", "LOADED");
        Log.d("ITEM",loadItems.size()+"");
        for(Object j : loadItems){
            Log.d("ITEM",j.getClass()+"");
        }
    }
}