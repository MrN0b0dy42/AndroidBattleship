package fr.info.orleans.androidbattleship.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.services.BackgroundAmbianceService;
import fr.info.orleans.androidbattleship.services.BackgroundMusicService;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_MUSIC = "music";
    public static final String KEY_AMBIANCE = "ambiance";
    public static final String SETTINGS_FILE_NAME = "settings";

    private CheckBox checkBoxMusic, checkBoxAmbiance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        checkBoxMusic = (CheckBox) findViewById(R.id.checkbox_music);
        checkBoxAmbiance = (CheckBox) findViewById(R.id.checkbox_ambiance);
        checkBoxMusic.setOnClickListener(this);
        checkBoxAmbiance.setOnClickListener(this);
        SharedPreferences settings = getSharedPreferences(SETTINGS_FILE_NAME, 0);
        boolean musicTurnOn = settings.getBoolean("music", true);
        if (musicTurnOn)
            checkBoxMusic.setChecked(true);
        else
            checkBoxMusic.setChecked(false);
        boolean ambianceTurnOn = settings.getBoolean("ambiance", true);
        if (ambianceTurnOn)
            checkBoxAmbiance.setChecked(true);
        else
            checkBoxAmbiance.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        String key = null;
        boolean on = false;
        SharedPreferences settings = getSharedPreferences(SETTINGS_FILE_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        switch (v.getId()) {
            case R.id.checkbox_music:
                key = KEY_MUSIC;
                if (((CheckBox) v).isChecked()) {
                    on = true;
                    startService(new Intent(this, BackgroundMusicService.class));
                } else {
                    on = false;
                    stopService(new Intent(this, BackgroundMusicService.class));
                }
                break;
            case R.id.checkbox_ambiance:
                key = KEY_AMBIANCE;
                if (((CheckBox) v).isChecked()) {
                     on = true;
                    startService(new Intent(this, BackgroundAmbianceService.class));
                } else {
                    on = false;
                    stopService(new Intent(this, BackgroundAmbianceService.class));
                }
                break;
        }
        editor.putBoolean(key, on);
        editor.apply();
    }
}
