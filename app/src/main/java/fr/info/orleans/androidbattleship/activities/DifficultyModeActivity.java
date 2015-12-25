package fr.info.orleans.androidbattleship.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import fr.info.orleans.androidbattleship.R;

public class DifficultyModeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonEasy, buttonHard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_mode);
        buttonEasy = (Button) findViewById(R.id.button_easy);
        buttonHard = (Button) findViewById(R.id.button_hard);
        buttonEasy.setOnClickListener(this);
        buttonHard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String difficulty = null;
        Intent intent = new Intent(this, ShipsArrangementActivity.class);
        switch (v.getId()) {
            case R.id.button_easy:
                difficulty = "easy";
                break;
            case R.id.button_hard:
                difficulty = "hard";
                Toast.makeText(this, "Not yet implemented.", Toast.LENGTH_SHORT).show();
                return;
        }
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }

}
