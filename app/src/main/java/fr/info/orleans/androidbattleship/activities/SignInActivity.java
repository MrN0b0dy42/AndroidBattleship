package fr.info.orleans.androidbattleship.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import fr.info.orleans.androidbattleship.DatabaseManager;
import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.model.Player;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignIn;
    private EditText editTextLastname, editTextFirstname, editTextLogin, editTextPassword;
    List players = null;
    DatabaseManager db = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        buttonSignIn = (Button) findViewById(R.id.button_sign_in);
        editTextFirstname = (EditText) findViewById(R.id.editText_firstname);
        editTextLastname = (EditText) findViewById(R.id.editText_lastname);
        editTextLogin = (EditText) findViewById(R.id.editText_login);
        editTextPassword = (EditText) findViewById(R.id.editText_password);
        buttonSignIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        String firstname = editTextFirstname.getText().toString();
        String lastname = editTextLastname.getText().toString();
        String login = editTextLogin.getText().toString();
        String password = editTextPassword.getText().toString();
        Player p = new Player(firstname,lastname,login,password);

        try{
            db.insertPlayer(p);
            Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(this, "DB insertion error.", Toast.LENGTH_SHORT).show();
        }
    }
}
