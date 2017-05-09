package cisc181.finalproject;

/*

SimpleAnimate app -- animated drawing in a separate thread

Christopher Rasmussen
copyright 2017, University of Delaware

*/

import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;

public class MyThread extends Thread {

    MyAnimatedSurfaceView myView;
    private boolean running = false;  // default run state
    long sleepMillis = 16;            // default sleep time
    SoundPool SP;
    int sound_hit, sound_miss;

    // initialize thread, using default sleep time between each draw call

    public MyThread(MyAnimatedSurfaceView view) {

        myView = view;

       // SP = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);  // deprecated from API level 21 on
        //sound_miss = SP.load(myView.getContext(), R.raw.jump, 1);
        //sound_hit = SP.load(myView.getContext(), R.raw.grenade, 1);

    }

    // change whether thread is currently running or not

    public void setRunning(boolean running) {
        this.running = running;
    }

    // define what happens when thread is running

    @Override
    public void run() {
        while (running) {

            // draw one time...

            Canvas canvas = myView.getHolder().lockCanvas();

            if (canvas != null) {
                synchronized (myView.getHolder()) {
                    myView.myDraw(canvas);
                }
                myView.getHolder().unlockCanvasAndPost(canvas);
            }
            try {
                sleep(sleepMillis);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}