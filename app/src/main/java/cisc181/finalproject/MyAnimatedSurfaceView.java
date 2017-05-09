package cisc181.finalproject;

/*

SimpleAnimate app -- animated drawing in a separate thread

Christopher Rasmussen
copyright 2017, University of Delaware

*/

import android.content.Context;
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

    Paint mPaint, mPaintHit, mPaintMiss;
    Bitmap bmap, ast;

    // object position, velocity

    int xPos = 0;
    int yPos = 0;
    double xDelta = 5;
    double yDelta = 5;
    double xAccel = 1.1;
    double yAccel = 1.1;

    float PLAYER_CENTER_X = 200;
    float PLAYER_CENTER_Y = 200;
    float SCREEN_WIDTH = 0;
    float SCREEN_HEIGHT = 0;

    // object orientation

    int angleDegs = 0;
    int angleDegsDelta = 5;

    // object scale

    float scaleFactor = 3.0f;

    // touch activity

    boolean touchDown = false;
    boolean touchStarted = false;
    boolean touchHit = false;
    float touchX, touchY;

    Ship playerShip;
    Asteroid aste = new Asteroid(new FloatPoint(500,500), new FloatPoint(0,0), new FloatPoint(0,0));
    Shop shop = new Shop(new FloatPoint(300,800), new FloatPoint(0,0), new FloatPoint(0,0));

    ArrayList<Entity> entities = new ArrayList<Entity>();

    SoundPool sp;
    int explosion;
    MediaPlayer booster;
    Bitmap ironImage;
    Bitmap spaceStation;


    // constructor

    public MyAnimatedSurfaceView(Context context, AttributeSet attrs) {

        super(context, attrs);

        booster = MediaPlayer.create(context,R.raw.booster2);
       // booster.setVideoScalingMode();
        booster.setLooping(true);
        sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);  // deprecated from API level 21 on
        explosion = sp.load(context, R.raw.expl, 1);
        booster.setVolume(0.2f,0.2f);

        myThread = new MyThread(this);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);

        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
        PLAYER_CENTER_X = size.x/2;
        PLAYER_CENTER_Y = size.y/2;
        playerShip = new Ship(new FloatPoint(PLAYER_CENTER_X,PLAYER_CENTER_Y), new FloatPoint(0,0), new FloatPoint(0,0));

        mPaint = new Paint();
        mPaint.setStrokeWidth(5);

        mPaintHit = new Paint();
        ColorFilter redFilter = new LightingColorFilter(Color.RED, 0);
        mPaintHit.setColorFilter(redFilter);

        mPaintMiss = new Paint();
        ColorFilter blueFilter = new LightingColorFilter(Color.BLUE, 0);
        mPaintMiss.setColorFilter(blueFilter);

        bmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship);
        ast = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid);
        bmap = Bitmap.createScaledBitmap(bmap,256,256,false);
        ast = Bitmap.createScaledBitmap(ast,256,256,false);
        spaceStation = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.iss),256,256,false);


        ironImage = BitmapFactory.decodeResource(getResources(), R.drawable.iron);
        ironImage = Bitmap.createScaledBitmap(ironImage,256,256,false);


        playerShip.setSprite(bmap);
        shop.setSprite(spaceStation);
        entities.add(shop);
        aste.setSprite(ast);
        aste.width=256;
        aste.height=256;

        //entities.add(aste);

        for(int i = 0; i < 100; i++){
            Random r = new Random();
            float randomX = (float)r.nextInt(10000);
            float randomY = (float)r.nextInt(10000);
            float randomVelX = (float)r.nextInt(10-5+1)-5;
            float randomVelY = (float)r.nextInt(10-5+1)-5;
            Asteroid a = new Asteroid(new FloatPoint(randomX,randomY), new FloatPoint(randomVelX,randomVelY), new FloatPoint(0,0));
            a.width=256;
            a.height=256;
            a.setSprite(ast);
            entities.add(a);
        }
       // a.setSprite(ast);

        SurfaceHolder holder = getHolder();

        // implement SurfaceHolder.Callback interface with anonymous inner class

        holder.addCallback(new SurfaceHolder.Callback() {

            // start draw thread

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                myThread.setRunning(true);
                myThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            // stop draw thread

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                myThread.setRunning(false);
                while (retry) {
                    try {
                        myThread.join();
                        retry = false;
                    }
                    catch (InterruptedException e) { }
                }
            }
        });

    }

    // do drawing on current canvas

    float cameraX = 0;
    float cameraY = 0;
    //FloatPoint camera = new FloatPoint(0,0);
    Camera camera = new Camera(new FloatPoint(0,0));


    public void myDraw(Canvas canvas) {
        camera = new Camera(new FloatPoint(playerShip.pos.x-PLAYER_CENTER_X, playerShip.pos.y-PLAYER_CENTER_Y));
        //canvas.drawColor(Color.BLACK);
        canvas.drawColor(Color.parseColor("#17132c"));


        playerShip.update();

        Iterator<Entity> it = entities.iterator();

        //Use an iterator so we can safely remove entities
        ArrayList<Entity> toAdd = new ArrayList<Entity>();
        while(it.hasNext()){
            Entity e = it.next();
            if(e.dead){
                it.remove();
            }else {
                e.update();
                if(e instanceof Asteroid){
                    //Log.d("Tag","YES");
                    Asteroid ast = (Asteroid)e;
                    if(ast.dead){
                        //TODO
                        sp.play(explosion, 1f, 1f, 0, 0, 1f);
                        Log.d("Hit ast","Hit it!");
                        //Add stuff with the cargo
                        //Item i = new Item();
                        //i.pos = e.pos;
                        //i.vel = new FloatPoint(1,1);
                        //Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.iron);
                        //img = Bitmap.createScaledBitmap(img,256,256,false);

                        //i.setSprite(img);
                        ArrayList<Item> cargo = ast.dropCargo();
                        //toAdd.addAll(ast.dropCargo());
                        for(Item z: cargo){
                            Log.d("Hit","cargo");
                            z.setSprite(ironImage);
                        }



                        toAdd.addAll(cargo);
                        //toAdd.add(i);
                        //entities.add(i);
                    }
                }
                e.render(canvas, mPaint, camera);
            }
        }

        for(Entity e: toAdd){
            entities.add(e);
        }

        canvas.save();
        canvas.rotate(playerShip.angle-45+90, PLAYER_CENTER_X, PLAYER_CENTER_Y);
        canvas.drawBitmap(playerShip.getSprite(), PLAYER_CENTER_X-playerShip.getSprite().getWidth()/2, PLAYER_CENTER_Y-playerShip.getSprite().getHeight()/2,mPaint);
        canvas.restore();
    }

    // respond to the SurfaceView being clicked/dragged

    public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_DOWN){
            booster.start();
            FloatPoint touchPosWorld = camera.screenToWorldPos(new FloatPoint(e.getX(),e.getY()));
            ArrayList<Entity> toAdd = new ArrayList<Entity>();

            for(Entity ent: entities){
                if(ent.containsPoint(touchPosWorld)){
                    if(ent instanceof Asteroid){
                        Asteroid a = (Asteroid)ent;
                        a.removeHealth(playerShip.damage);

                    }
                }
            }
        }

        if(e.getAction() == MotionEvent.ACTION_UP){
            booster.pause();
        }

        if(e.getAction() == MotionEvent.ACTION_MOVE){
            FloatPoint touch = camera.screenToWorldPos(new FloatPoint(e.getX(),e.getY()));

            float theta = (float)Math.atan2((touch.y-playerShip.pos.y),(touch.x-playerShip.pos.x));

            playerShip.setAcc(new FloatPoint((float)Math.cos(theta), (float)Math.sin(theta)));
            playerShip.angle = (float)Math.toDegrees(theta);
        }else{
            playerShip.setAcc(new FloatPoint(0,0));
        }

        return true;
    }
}