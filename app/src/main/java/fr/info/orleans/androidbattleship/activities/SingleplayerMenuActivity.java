package fr.info.orleans.androidbattleship.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import fr.info.orleans.androidbattleship.InternalStorageManager;
import fr.info.orleans.androidbattleship.R;

public class SingleplayerMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonNewGame, buttonLoadGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer_menu);
        buttonNewGame = (Button) findViewById(R.id.button_new_game);
        buttonLoadGame = (Button) findViewById(R.id.button_load_game);
        buttonNewGame.setOnClickListener(this);
        buttonLoadGame.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_new_game:
                intent = new Intent(this, DifficultyModeActivity.class);
                break;
            case R.id.button_load_game:
                if (previousSavedGameIsAvailable()) {
                    intent = new Intent(this, GameActivity.class);
                    intent.putExtra("loadGame", true);
                    break;
                } else {
                    Toast.makeText(this, getString(R.string.toast_no_saved_game_text), Toast.LENGTH_SHORT).show();
                    return;
                }
        }
        startActivity(intent);
    }

    private boolean previousSavedGameIsAvailable() {
        return InternalStorageManager.containsObject(this, GameActivity.KEY_PLAYER_GRID);
    }

}
