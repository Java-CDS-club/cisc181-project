package cisc181.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {
    int score;
    int highScore;
    boolean resetScore = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        score = getIntent().getIntExtra("score",0);
        String message = getIntent().getStringExtra("msg");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        highScore = prefs.getInt("highScore",0);
        String highScoreName = prefs.getString("highScoreName","");

        TextView msgBox = (TextView) findViewById(R.id.msgBox);
        TextView scoreBox = (TextView) findViewById(R.id.scoreBox);
        TextView highScoreBox = (TextView) findViewById(R.id.highScoreBox);

        msgBox.setText(message);
        scoreBox.setText(score + "");
        highScoreBox.setText(highScoreName + ":"+highScore);
    }

    public void finishGame(View v){
        if(score > highScore) {//update high score
            EditText nameBox = (EditText) findViewById(R.id.nameBox);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("highScoreName",nameBox.getText().toString());
            edit.putInt("highScore",score);
            edit.commit();
        }
        Intent i = new Intent(this, StartActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        if(resetScore){
            resetHighScore();
        }
    }

    public void setReset(View v){
        resetScore = true;
    }

    public void resetHighScore(){
        EditText nameBox = (EditText) findViewById(R.id.nameBox);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("highScoreName","");
        edit.putInt("highScore",0);
        edit.commit();
    }
}
