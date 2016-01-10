package fr.info.orleans.androidbattleship.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import fr.info.orleans.androidbattleship.AndroidBattleship;
import fr.info.orleans.androidbattleship.DatabaseManager;
import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.services.BackgroundAmbianceService;
import fr.info.orleans.androidbattleship.services.BackgroundMusicService;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonPlayerVsComputer, buttonPlayerVsPlayer, buttonSettings;
    DatabaseManager db = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        buttonPlayerVsComputer = (Button) findViewById(R.id.button_player_vs_computer);
        buttonPlayerVsPlayer = (Button) findViewById(R.id.button_player_vs_player);
        buttonSettings = (Button) findViewById(R.id.button_settings);
        buttonPlayerVsComputer.setOnClickListener(this);
        buttonPlayerVsPlayer.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);

        db.getWritableDatabase();

        SharedPreferences settings = getSharedPreferences(SettingsActivity.SETTINGS_FILE_NAME, 0);
        boolean music = settings.getBoolean(SettingsActivity.KEY_MUSIC, true);
        if (music)
            startService(new Intent(this, BackgroundMusicService.class));
        boolean ambiance = settings.getBoolean(SettingsActivity.KEY_AMBIANCE, true);
        if (ambiance)
            startService(new Intent(this, BackgroundAmbianceService.class));
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_player_vs_computer:
                intent = new Intent(this, SingleplayerMenuActivity.class);
                break;
            case R.id.button_player_vs_player:
                if(((AndroidBattleship) this.getApplication()).getConnectedPlayer() == null){
                    intent = new Intent(this, AccountActivity.class);
                }else{
                    intent = new Intent(this, ConnectionModeActivity.class);
                }
                break;
            case R.id.button_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }
        startActivity(intent);
    }

}
