package cisc181.finalproject;

import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

public class StartActivity extends AppCompatActivity {

    MediaPlayer music;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        RadioButton easyButton = (RadioButton) findViewById(R.id.easy);

        music = MediaPlayer.create(getApplicationContext(),R.raw.title);
        music.start();
        music.setLooping(true);

        easyButton.setChecked(true);
    }

    public void startGame(View v){
        music.stop();
        Intent i = new Intent(this, MainActivity.class);
        RadioButton hardButton = (RadioButton) findViewById(R.id.hard);
        RadioButton easyButton = (RadioButton) findViewById(R.id.easy);
        if(hardButton.isChecked()) {
            i.putExtra("startFuel", 2000);
        }
        else if(easyButton.isChecked()) {
            i.putExtra("startFuel", 4000);
        }

        startActivity(i);
    }

    public void quitGame(View v){
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        return;
    }

    public void showInstructions(View v){
        Intent i = new Intent(this, Instructions.class);
        music.stop();
        startActivity(i);
    }
}
