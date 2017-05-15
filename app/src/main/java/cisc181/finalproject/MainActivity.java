package cisc181.finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    static Context baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        baseContext = getBaseContext();
        super.onCreate(savedInstanceState);
        //Remove title bar

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int sf = getIntent().getIntExtra("startFuel",0);
        MyAnimatedSurfaceView.startFuel = sf;
        setContentView(R.layout.activity_main);
    }

    // invoked when the activity loses user focus.

    public static void endGame(int loseCode, int score){
        String msg = "";
        if(loseCode == 0)
            msg = "You Died!";
        else
            msg = "No Fuel!";
        Intent i = new Intent(baseContext, ScoreActivity.class);
        i.putExtra("msg",msg);
        i.putExtra("score",score);
        baseContext.startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  finish();   // just die...getting force close on resume otherwise...
    }
}
