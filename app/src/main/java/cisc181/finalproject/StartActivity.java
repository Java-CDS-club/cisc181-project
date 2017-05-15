package cisc181.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        RadioButton easyButton = (RadioButton) findViewById(R.id.fourthou);
        easyButton.setChecked(true);
    }

    public void startGame(View v){
        Intent i = new Intent(this, MainActivity.class);
        RadioButton hardButton = (RadioButton) findViewById(R.id.twothou);
        RadioButton easyButton = (RadioButton) findViewById(R.id.fourthou);
        if(hardButton.isChecked()){
            i.putExtra("startFuel",2000);
        }
        else
            i.putExtra("startFuel",4000);

        startActivity(i);
    }

    public void quitGame(View v){
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        return;
    }
}
