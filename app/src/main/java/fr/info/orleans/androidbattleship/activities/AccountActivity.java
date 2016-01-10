package fr.info.orleans.androidbattleship.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import fr.info.orleans.androidbattleship.AndroidBattleship;
import fr.info.orleans.androidbattleship.DatabaseManager;
import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.model.Player;
import fr.info.orleans.androidbattleship.services.BackgroundAmbianceService;
import fr.info.orleans.androidbattleship.services.BackgroundMusicService;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonGuestMode, buttonSignUp, buttonSignIn;
    private EditText editTextLogin,editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        buttonGuestMode = (Button) findViewById(R.id.button_guest_mode);
        buttonSignUp = (Button) findViewById(R.id.button_sign_up);
        buttonSignIn = (Button) findViewById(R.id.button_sign_in);
        editTextLogin = (EditText) findViewById(R.id.editText_login);
        editTextPassword = (EditText) findViewById(R.id.editText_password);

        buttonGuestMode.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_guest_mode:
                intent = new Intent(this,ConnectionModeActivity.class);
                startActivity(intent);
                break;
            case R.id.button_sign_up:
                List players = null;
                DatabaseManager db = new DatabaseManager(this);
                db.getReadableDatabase();
                players = db.selectPlayerByQuery("SELECT * FROM Player WHERE login = '" + editTextLogin.getText().toString() + "' AND PASSWORD = '" + editTextPassword.getText().toString() + "';");
                if(players.isEmpty()){
                    Toast.makeText(this, "Error login/password.", Toast.LENGTH_LONG).show();
                }else{
                    intent = new Intent(this,ConnectionModeActivity.class);
                    ((AndroidBattleship) this.getApplication()).setConnectedPlayer((Player) players.get(0));
                    Toast.makeText(this, "Connected as " + ((Player) players.get(0)).getLogin(), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }

                break;
            case R.id.button_sign_in:
                intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                break;
        }

    }

}