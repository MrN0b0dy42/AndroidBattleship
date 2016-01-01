package fr.info.orleans.androidbattleship.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import fr.info.orleans.androidbattleship.R;

public class ConnectionModeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonBluetooth, buttonWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_mode);
        buttonBluetooth = (Button) findViewById(R.id.button_bluetooth);
        buttonWifi = (Button) findViewById(R.id.button_wifi);
        buttonBluetooth.setOnClickListener(this);
        buttonWifi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_bluetooth:
                Toast.makeText(this, "Not yet implemented.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_wifi:
                //Toast.makeText(this, "Not yet implemented.", Toast.LENGTH_SHORT).show();
                //intent = new Intent(this,ConnectionActivity.class);
                break;
        }
    }

}
