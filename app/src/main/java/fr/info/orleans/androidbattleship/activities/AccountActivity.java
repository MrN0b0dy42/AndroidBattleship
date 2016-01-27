package fr.info.orleans.androidbattleship.activities;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;
import fr.info.orleans.androidbattleship.AndroidBattleship;
import fr.info.orleans.androidbattleship.DatabaseManager;
import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.model.Player;


public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextLogin,editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Button buttonSignUp = (Button) findViewById(R.id.button_sign_up);
        Button buttonSignIn = (Button) findViewById(R.id.button_sign_in);
        editTextLogin = (EditText) findViewById(R.id.editText_login);
        editTextPassword = (EditText) findViewById(R.id.editText_password);

        buttonSignUp.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button_sign_up:
                List players;
                DatabaseManager db = new DatabaseManager(this);
                db.getReadableDatabase();
                players = db.selectPlayerByQuery("SELECT * FROM Player WHERE login = '" + editTextLogin.getText().toString() + "' AND PASSWORD = '" + editTextPassword.getText().toString() + "';");
                if(players.isEmpty()){
                    Toast.makeText(this, getText(R.string.toast_error_login_password), Toast.LENGTH_LONG).show();
                }else{
                    intent = new Intent(this,ConnectionModeActivity.class);
                    ((AndroidBattleship) this.getApplication()).setConnectedPlayer((Player) players.get(0));
                    Toast.makeText(this, getText(R.string.toast_connected_as) + ((Player) players.get(0)).getLogin(), Toast.LENGTH_LONG).show();
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